package com.qinggan.qingganmianshi.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qinggan.qingganmianshi.common.ErrorCode;
import com.qinggan.qingganmianshi.constant.CommonConstant;
import com.qinggan.qingganmianshi.exception.BusinessException;
import com.qinggan.qingganmianshi.exception.ThrowUtils;
import com.qinggan.qingganmianshi.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.qinggan.qingganmianshi.model.entity.Question;
import com.qinggan.qingganmianshi.model.entity.QuestionBank;
import com.qinggan.qingganmianshi.model.entity.QuestionBankQuestion;
import com.qinggan.qingganmianshi.model.entity.User;
import com.qinggan.qingganmianshi.model.vo.QuestionBankQuestionVO;
import com.qinggan.qingganmianshi.model.vo.UserVO;
import com.qinggan.qingganmianshi.service.QuestionBankQuestionService;
import com.qinggan.qingganmianshi.service.QuestionBankService;
import com.qinggan.qingganmianshi.service.QuestionService;
import com.qinggan.qingganmianshi.service.UserService;
import com.qinggan.qingganmianshi.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题库题目关联服务实现
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<com.qinggan.qingganmianshi.mapper.QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private QuestionService questionService;

    @Resource
    private QuestionBankService questionBankService;

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);
        //题库和题目必须都存在
        Long questionId = questionBankQuestion.getQuestionId();
        if(questionId!= null){
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR,"题目不存在");
        }

        Long questionBankId = questionBankQuestion.getQuestionBankId();
        if(questionBankId!= null){
            QuestionBank questionBank = questionBankService.getById(questionBankId);
            ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR,"题库不存在");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();
        Long userId = questionBankQuestionQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库题目关联封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(questionBankQuestion);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBankQuestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankQuestionVO.setUser(userVO);
        return questionBankQuestionVO;
    }

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(), questionBankQuestionPage.getSize(), questionBankQuestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream().map(questionBankQuestion -> {
            return QuestionBankQuestionVO.objToVo(questionBankQuestion);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankQuestionList.stream().map(QuestionBankQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionBankQuestionVOList.forEach(questionBankQuestionVO -> {
            Long userId = questionBankQuestionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankQuestionVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }

    /**
     * 批量向题库中添加题目
     * @param questionIdList 需要添加的题目id列表
     * @param questionBankId 添加到哪个题库id
     * @param loginUser 哪个用户添加的
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestionsToBank(List<Long> questionIdList, Long questionBankId, User loginUser){
        //验证传入参数的合法性
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目id列表为空");
        ThrowUtils.throwIf(questionBankId==null || questionBankId<=0, ErrorCode.PARAMS_ERROR, "题库id为空");
        ThrowUtils.throwIf(loginUser==null, ErrorCode.NOT_LOGIN_ERROR);

        //验证数据库中是否有传入的题目id及题库id
        List<Question> questionList = questionService.listByIds(questionIdList);
        List<Long> validQuestionIdList = questionList.stream().map(Question::getId).collect(Collectors.toList());
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.NOT_FOUND_ERROR, "合法的题目列表为空");
        QuestionBank validQuestionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(validQuestionBank==null, ErrorCode.NOT_FOUND_ERROR, "题库为空");

        //执行插入逻辑
        for(Long questionId : validQuestionIdList){
            QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
            questionBankQuestion.setQuestionBankId(questionBankId);
            questionBankQuestion.setQuestionId(questionId);
            questionBankQuestion.setUserId(loginUser.getId());
            boolean result = this.save(questionBankQuestion);
            if(!result){
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
            }
        }
    }

    /**
     * 批量从题库中删除题目
     * @param questionIdList
     * @param questionBankId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchRemoveQuestionsFromBank(List<Long> questionIdList, Long questionBankId) {
        //验证传入参数的合法性
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目id列表为空");
        ThrowUtils.throwIf(questionBankId==null || questionBankId<=0, ErrorCode.PARAMS_ERROR, "题库id为空");

        //执行删除逻辑
        for(Long questionId : questionIdList){
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                    .eq(QuestionBankQuestion::getQuestionId, questionId);
            boolean result = this.remove(lambdaQueryWrapper);
            if(!result){
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "从题库中删除题目失败");
            }
        }
    }
}
