package com.edu.modules.review.controller;

import com.edu.common.result.Result;
import com.edu.modules.review.entity.CourseReview;
import com.edu.modules.review.service.CourseReviewService;
import com.edu.modules.review.service.CourseReviewStats;
import com.edu.modules.review.service.UserReviewStats;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 课程评价控制器
 */
@Slf4j
@Api(tags = "课程评价管理")
@RestController
@RequestMapping("/api/reviews")
@Validated
public class CourseReviewController {

    @Autowired
    private CourseReviewService courseReviewService;

    @ApiOperation("发表课程评价")
    @PostMapping
    public Result<CourseReview> createReview(
            @ApiParam("课程ID") @NotNull(message = "课程ID不能为空") @RequestParam Long courseId,
            @ApiParam("评分(1-5)") @NotNull(message = "评分不能为空")
            @DecimalMin(value = "1.0", message = "评分不能低于1星")
            @DecimalMax(value = "5.0", message = "评分不能高于5星") @RequestParam BigDecimal rating,
            @ApiParam("评价内容") @RequestParam(required = false) String comment,
            @ApiParam("是否匿名：0-否，1-是") @RequestParam(defaultValue = "0") Integer isAnonymous) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        CourseReview review = new CourseReview();
        review.setUserId(userId);
        review.setCourseId(courseId);
        review.setRating(rating);
        review.setComment(comment);
        review.setIsAnonymous(isAnonymous);

        CourseReview result = courseReviewService.createReview(review);
        return Result.success("评价发表成功", result);
    }

    @ApiOperation("更新课程评价")
    @PutMapping("/{id}")
    public Result<String> updateReview(
            @ApiParam("评价ID") @PathVariable Long id,
            @ApiParam("评分(1-5)") @RequestParam(required = false)
            @DecimalMin(value = "1.0", message = "评分不能低于1星")
            @DecimalMax(value = "5.0", message = "评分不能高于5星") BigDecimal rating,
            @ApiParam("评价内容") @RequestParam(required = false) String comment) {

        CourseReview review = new CourseReview();
        review.setId(id);
        review.setRating(rating);
        review.setComment(comment);

        boolean success = courseReviewService.updateReview(review);
        return success ? Result.success("评价更新成功") : Result.fail("评价更新失败");
    }

    @ApiOperation("删除课程评价")
    @DeleteMapping("/{id}")
    public Result<String> deleteReview(@ApiParam("评价ID") @PathVariable Long id) {
        boolean success = courseReviewService.deleteReview(id);
        return success ? Result.success("评价删除成功") : Result.fail("评价删除失败");
    }

    @ApiOperation("获取课程评价列表")
    @GetMapping("/course/{courseId}")
    public Result<List<CourseReview>> getCourseReviews(@ApiParam("课程ID") @PathVariable Long courseId) {
        List<CourseReview> reviews = courseReviewService.getReviewsByCourseId(courseId);
        return Result.success(reviews);
    }

    @ApiOperation("获取我的评价列表")
    @GetMapping("/my-reviews")
    public Result<List<CourseReview>> getMyReviews() {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        List<CourseReview> reviews = courseReviewService.getReviewsByUserId(userId);
        return Result.success(reviews);
    }

    @ApiOperation("获取课程平均评分")
    @GetMapping("/course/{courseId}/average-rating")
    public Result<BigDecimal> getCourseAverageRating(@ApiParam("课程ID") @PathVariable Long courseId) {
        BigDecimal averageRating = courseReviewService.getCourseAverageRating(courseId);
        return Result.success(averageRating);
    }

    @ApiOperation("获取课程评价统计")
    @GetMapping("/course/{courseId}/stats")
    public Result<CourseReviewStats> getCourseReviewStats(@ApiParam("课程ID") @PathVariable Long courseId) {
        CourseReviewStats stats = courseReviewService.getCourseReviewStats(courseId);
        return Result.success(stats);
    }

    @ApiOperation("检查是否已评价")
    @GetMapping("/course/{courseId}/has-reviewed")
    public Result<Boolean> hasReviewed(@ApiParam("课程ID") @PathVariable Long courseId) {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.success(false);
        }

        boolean hasReviewed = courseReviewService.hasUserReviewedCourse(userId, courseId);
        return Result.success(hasReviewed);
    }

    @ApiOperation("获取用户评价统计")
    @GetMapping("/user/stats")
    public Result<UserReviewStats> getUserReviewStats() {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        UserReviewStats stats = courseReviewService.getUserReviewStats(userId);
        return Result.success(stats);
    }
}