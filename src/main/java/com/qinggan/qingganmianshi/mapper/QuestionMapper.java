package com.qinggan.qingganmianshi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qinggan.qingganmianshi.model.entity.Question;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author 1401687501x's
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2024-09-22 00:14:45
* @Entity generator.domain.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    @Select("select * from question where updateTime >= #{minUpdateTime}")
    List<Question> listPostWithDelete(Date minUpdateTime);
}




