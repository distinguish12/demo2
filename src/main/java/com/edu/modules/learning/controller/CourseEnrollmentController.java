package com.edu.modules.learning.controller;

import com.edu.common.result.Result;
import com.edu.modules.learning.entity.CourseEnrollment;
import com.edu.modules.learning.service.CourseEnrollmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 选课控制器
 */
@Slf4j
@Api(tags = "选课管理")
@RestController
@RequestMapping("/api/learning")
@Validated
public class CourseEnrollmentController {

    @Autowired
    private CourseEnrollmentService courseEnrollmentService;

    @ApiOperation("选课")
    @PostMapping("/enroll")
    public Result<CourseEnrollment> enrollCourse(
            @ApiParam("课程ID") @NotNull(message = "课程ID不能为空") @RequestParam Long courseId) {

        // 从JWT过滤器设置的请求属性中获取用户ID
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        CourseEnrollment enrollment = courseEnrollmentService.enrollCourse(userId, courseId);
        return Result.success("选课成功", enrollment);
    }

    @ApiOperation("退课")
    @PostMapping("/unenroll")
    public Result<String> unenrollCourse(
            @ApiParam("课程ID") @NotNull(message = "课程ID不能为空") @RequestParam Long courseId) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        boolean success = courseEnrollmentService.unenrollCourse(userId, courseId);
        return success ? Result.success("退课成功") : Result.fail("退课失败");
    }

    @ApiOperation("检查选课状态")
    @GetMapping("/enrolled/{courseId}")
    public Result<Boolean> checkEnrollment(@ApiParam("课程ID") @PathVariable Long courseId) {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        boolean enrolled = courseEnrollmentService.isEnrolled(userId, courseId);
        return Result.success(enrolled);
    }

    @ApiOperation("获取我的选课列表")
    @GetMapping("/my-courses")
    public Result<List<CourseEnrollment>> getMyEnrollments() {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        List<CourseEnrollment> enrollments = courseEnrollmentService.getUserEnrollments(userId);
        return Result.success(enrollments);
    }

    @ApiOperation("获取课程选课统计")
    @GetMapping("/courses/{courseId}/enrollment-count")
    public Result<Integer> getEnrollmentCount(@ApiParam("课程ID") @PathVariable Long courseId) {
        int count = courseEnrollmentService.getEnrollmentCount(courseId);
        return Result.success(count);
    }
}