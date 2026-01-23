package com.edu.modules.review.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.review.entity.CourseReview;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课程评价服务接口
 */
public interface CourseReviewService extends IService<CourseReview> {

    /**
     * 发表课程评价
     */
    CourseReview createReview(CourseReview review);

    /**
     * 更新课程评价
     */
    boolean updateReview(CourseReview review);

    /**
     * 删除课程评价
     */
    boolean deleteReview(Long reviewId);

    /**
     * 根据课程ID获取评价列表
     */
    List<CourseReview> getReviewsByCourseId(Long courseId);

    /**
     * 根据用户ID获取评价列表
     */
    List<CourseReview> getReviewsByUserId(Long userId);

    /**
     * 检查用户是否已评价课程
     */
    boolean hasUserReviewedCourse(Long userId, Long courseId);

    /**
     * 获取课程的平均评分
     */
    BigDecimal getCourseAverageRating(Long courseId);

    /**
     * 获取课程的评价统计
     */
    CourseReviewStats getCourseReviewStats(Long courseId);

    /**
     * 获取用户的评价统计
     */
    UserReviewStats getUserReviewStats(Long userId);
}