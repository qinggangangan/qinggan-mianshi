package com.qinggan.qingganmianshi.job.cycle;

import cn.hutool.core.collection.CollUtil;
import com.qinggan.qingganmianshi.annotation.DistributedLock;
import com.qinggan.qingganmianshi.esdao.QuestionEsDao;
import com.qinggan.qingganmianshi.mapper.QuestionMapper;
import com.qinggan.qingganmianshi.model.dto.question.QuestionEsDTO;
import com.qinggan.qingganmianshi.model.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 增量同步题目
 * Author: 1401687501x's
 * Date: 2024/9/26 22:57
 */
@Component
@Slf4j
public class InSyncQuestionToEs {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionEsDao questionEsDao;

    /**
     * 每分钟执行一次
     */
    @DistributedLock(key = "inSync")
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - 5 * 60 * 1000L);
        List<Question> questionList = questionMapper.listPostWithDelete(fiveMinutesAgoDate);
        if (CollUtil.isEmpty(questionList)) {
            log.info("no inc question");
            return;
        }
        List<QuestionEsDTO> questionEsDTOList = questionList.stream()
                .map(QuestionEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("IncSyncQuestionToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            questionEsDao.saveAll(questionEsDTOList.subList(i, end));
        }
        log.info("IncSyncQuestionToEs end, total {}", total);
    }
}
