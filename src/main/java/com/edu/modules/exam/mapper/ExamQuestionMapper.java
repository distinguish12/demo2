package com.edu.modules.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.exam.entity.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试题目Mapper
 */
@Mapper
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {
}