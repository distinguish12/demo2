package com.edu.modules.exercise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.exercise.entity.ExerciseSubmission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 练习提交Mapper
 */
@Mapper
public interface ExerciseSubmissionMapper extends BaseMapper<ExerciseSubmission> {
}