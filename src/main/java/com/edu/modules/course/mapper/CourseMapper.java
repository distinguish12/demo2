package com.edu.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.course.entity.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程Mapper
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}