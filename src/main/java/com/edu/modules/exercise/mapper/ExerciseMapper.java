package com.edu.modules.exercise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.exercise.entity.Exercise;
import org.apache.ibatis.annotations.Mapper;

/**
 * 练习题Mapper
 */
@Mapper
public interface ExerciseMapper extends BaseMapper<Exercise> {
}