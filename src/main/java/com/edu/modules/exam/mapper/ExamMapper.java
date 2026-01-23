package com.edu.modules.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.exam.entity.Exam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试Mapper
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
}