package com.edu.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.course.entity.CourseCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程分类Mapper
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
}