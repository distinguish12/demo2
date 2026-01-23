package com.edu.modules.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.learning.entity.CourseEnrollment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 选课记录Mapper
 */
@Mapper
public interface CourseEnrollmentMapper extends BaseMapper<CourseEnrollment> {
}