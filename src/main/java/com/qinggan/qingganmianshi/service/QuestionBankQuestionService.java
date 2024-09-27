package com.qinggan.qingganmianshi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qinggan.qingganmianshi.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.qinggan.qingganmianshi.model.entity.QuestionBankQuestion;
import com.qinggan.qingganmianshi.model.entity.User;
import com.qinggan.qingganmianshi.model.vo.QuestionBankQuestionVO;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题库题目关联服务
 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add 对创建的数据进行校验
     */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);
    
    /**
     * 获取题库题目关联封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request);

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request);

    /**
     * 批量向题库中添加题目
     * @param questionIdList 需要添加的题目id列表
     * @param questionBankId 添加到哪个题库id
     * @param loginUser 哪个用户添加的
     * @return
     */
    void batchAddQuestionsToBank(List<Long> questionIdList, Long questionBankId, User loginUser);

    /**
     * 批量从题库中删除题目
     * @param questionIdList
     * @param questionBankId
     */
    void batchRemoveQuestionsFromBank(List<Long> questionIdList, Long questionBankId);
}
