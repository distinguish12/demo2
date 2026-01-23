package com.edu.modules.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.review.entity.CourseReview;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程评价Mapper
 */
@Mapper
public interface CourseReviewMapper extends BaseMapper<CourseReview> {
}