package com.edu.modules.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.review.entity.CourseReview;
import com.edu.modules.review.mapper.CourseReviewMapper;
import com.edu.modules.review.service.CourseReviewService;
import com.edu.modules.review.service.CourseReviewStats;
import com.edu.modules.review.service.UserReviewStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

/**
 * 课程评价服务实现
 */
@Slf4j
@Service
public class CourseReviewServiceImpl extends ServiceImpl<CourseReviewMapper, CourseReview>
        implements CourseReviewService {

    @Override
    public CourseReview createReview(CourseReview review) {
        // 参数校验
        if (review == null || review.getCourseId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "课程ID不能为空");
        }
        if (review.getUserId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户ID不能为空");
        }
        if (review.getRating() == null ||
                review.getRating().compareTo(BigDecimal.ONE) < 0 ||
                review.getRating().compareTo(BigDecimal.valueOf(5)) > 0) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "评分必须在1-5之间");
        }

        // 检查用户是否已评价过此课程
        if (hasUserReviewedCourse(review.getUserId(), review.getCourseId())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "您已评价过此课程");
        }

        // 设置默认值
        if (review.getIsAnonymous() == null) {
            review.setIsAnonymous(0); // 默认不匿名
        }
        if (review.getStatus() == null) {
            review.setStatus(1); // 默认显示
        }

        boolean success = save(review);
        if (!success) {
            throw new BusinessException("发表评价失败");
        }

        log.info("课程评价发表成功: userId={}, courseId={}, rating={}",
                review.getUserId(), review.getCourseId(), review.getRating());
        return review;
    }

    @Override
    public boolean updateReview(CourseReview review) {
        if (review == null || review.getId() == null) {
            return false;
        }

        // 检查权限：只有评价者可以修改自己的评价
        CourseReview existing = getById(review.getId());
        if (existing == null) {
            return false;
        }

        Long currentUserId = getCurrentUserId();
        if (!existing.getUserId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限修改此评价");
        }

        boolean success = updateById(review);
        if (success) {
            log.info("课程评价更新成功: id={}", review.getId());
        }
        return success;
    }

    @Override
    public boolean deleteReview(Long reviewId) {
        if (reviewId == null) {
            return false;
        }

        // 检查权限：只有评价者可以删除自己的评价
        CourseReview existing = getById(reviewId);
        if (existing == null) {
            return false;
        }

        Long currentUserId = getCurrentUserId();
        if (!existing.getUserId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限删除此评价");
        }

        boolean success = removeById(reviewId);
        if (success) {
            log.info("课程评价删除成功: id={}", reviewId);
        }
        return success;
    }

    @Override
    public List<CourseReview> getReviewsByCourseId(Long courseId) {
        if (courseId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<CourseReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReview::getCourseId, courseId)
                .eq(CourseReview::getStatus, 1) // 只查询显示状态的评价
                .orderByDesc(CourseReview::getCreateTime);

        return list(wrapper);
    }

    @Override
    public List<CourseReview> getReviewsByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<CourseReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReview::getUserId, userId)
                .orderByDesc(CourseReview::getCreateTime);

        return list(wrapper);
    }

    @Override
    public boolean hasUserReviewedCourse(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            return false;
        }

        LambdaQueryWrapper<CourseReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReview::getUserId, userId)
                .eq(CourseReview::getCourseId, courseId);

        return count(wrapper) > 0;
    }

    @Override
    public BigDecimal getCourseAverageRating(Long courseId) {
        if (courseId == null) {
            return BigDecimal.ZERO;
        }

        List<CourseReview> reviews = getReviewsByCourseId(courseId);
        if (reviews.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = reviews.stream()
                .filter(r -> r.getRating() != null)
                .map(CourseReview::getRating)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP);
    }

    @Override
    public CourseReviewStats getCourseReviewStats(Long courseId) {
        if (courseId == null) {
            return new CourseReviewStats(0, BigDecimal.ZERO, new int[5], 0.0);
        }

        List<CourseReview> reviews = getReviewsByCourseId(courseId);

        int totalReviews = reviews.size();
        if (totalReviews == 0) {
            return new CourseReviewStats(0, BigDecimal.ZERO, new int[5], 0.0);
        }

        // 计算平均评分
        BigDecimal averageRating = getCourseAverageRating(courseId);

        // 计算评分分布
        int[] ratingDistribution = new int[5]; // 索引0-4对应1-5星
        int positiveCount = 0; // 4-5星好评数

        for (CourseReview review : reviews) {
            if (review.getRating() != null) {
                int rating = review.getRating().intValue();
                if (rating >= 1 && rating <= 5) {
                    ratingDistribution[rating - 1]++;
                    if (rating >= 4) {
                        positiveCount++;
                    }
                }
            }
        }

        // 计算好评率
        double positiveRate = (double) positiveCount / totalReviews;

        return new CourseReviewStats(totalReviews, averageRating, ratingDistribution, positiveRate);
    }

    @Override
    public UserReviewStats getUserReviewStats(Long userId) {
        if (userId == null) {
            return new UserReviewStats(0, BigDecimal.ZERO, 0);
        }

        List<CourseReview> reviews = getReviewsByUserId(userId);

        int totalReviews = reviews.size();
        if (totalReviews == 0) {
            return new UserReviewStats(0, BigDecimal.ZERO, 0);
        }

        // 计算平均评分
        BigDecimal sum = reviews.stream()
                .filter(r -> r.getRating() != null)
                .map(CourseReview::getRating)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageRating = sum.divide(BigDecimal.valueOf(totalReviews), 2, RoundingMode.HALF_UP);

        // 暂时将helpfulReviews设为0，后续可扩展
        int helpfulReviews = 0;

        return new UserReviewStats(totalReviews, averageRating, helpfulReviews);
    }

    /**
     * 获取当前用户ID（从请求上下文中获取）
     */
    private Long getCurrentUserId() {
        try {
            return (Long) org.springframework.web.context.request.RequestContextHolder
                    .currentRequestAttributes().getAttribute("userId",
                            org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);
        } catch (Exception e) {
            return null;
        }
    }
}