package com.edu.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.course.entity.CourseLesson;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程课时Mapper
 */
@Mapper
public interface CourseLessonMapper extends BaseMapper<CourseLesson> {
}