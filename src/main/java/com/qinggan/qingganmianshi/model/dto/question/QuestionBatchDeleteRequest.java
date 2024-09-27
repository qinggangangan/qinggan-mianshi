package com.qinggan.qingganmianshi.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 批量删除题目请求
 * Author: 1401687501x's
 * Date: 2024/9/27 22:22
 */
@Data
public class QuestionBatchDeleteRequest implements Serializable {

    /**
     * 题目 id 列表
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}
