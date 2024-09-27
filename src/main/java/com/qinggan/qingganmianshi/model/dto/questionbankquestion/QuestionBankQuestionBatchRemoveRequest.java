package com.qinggan.qingganmianshi.model.dto.questionbankquestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 批量移除请求
 * Author: 1401687501x's
 * Date: 2024/9/27 22:11
 */
@Data
public class QuestionBankQuestionBatchRemoveRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id 列表
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}

