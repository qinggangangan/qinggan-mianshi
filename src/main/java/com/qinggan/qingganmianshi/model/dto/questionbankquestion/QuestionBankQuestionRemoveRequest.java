package com.qinggan.qingganmianshi.model.dto.questionbankquestion;

import lombok.Data;

/**
 * Description: 移除题库题目关联请求
 * Author: 1401687501x's
 * Date: 2024/9/22 21:25
 */
@Data
public class QuestionBankQuestionRemoveRequest {

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
