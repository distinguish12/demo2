package com.edu.modules.learning.controller;

import com.edu.common.result.Result;
import com.edu.modules.learning.entity.LearningProgress;
import com.edu.modules.learning.service.LearningProgressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 学习进度控制器
 */
@Slf4j
@Api(tags = "学习进度管理")
@RestController
@RequestMapping("/api/learning")
@Validated
public class LearningProgressController {

    @Autowired
    private LearningProgressService learningProgressService;

    @ApiOperation("更新学习进度")
    @PostMapping("/progress")
    public Result<LearningProgress> updateProgress(
            @ApiParam("课时ID") @NotNull(message = "课时ID不能为空") @RequestParam Long lessonId,
            @ApiParam("进度百分比(0-100)") @Min(0) @Max(100) @RequestParam Integer progress,
            @ApiParam("最后播放位置(秒)") @Min(0) @RequestParam(required = false) Integer lastPosition) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        LearningProgress progressRecord = learningProgressService.updateProgress(userId, lessonId, progress, lastPosition);
        return Result.success("进度更新成功", progressRecord);
    }

    @ApiOperation("完成课时")
    @PostMapping("/complete/{lessonId}")
    public Result<String> completeLesson(@ApiParam("课时ID") @PathVariable Long lessonId) {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        boolean success = learningProgressService.completeLesson(userId, lessonId);
        return success ? Result.success("课时完成") : Result.fail("操作失败");
    }

    @ApiOperation("获取课时学习进度")
    @GetMapping("/progress/{lessonId}")
    public Result<LearningProgress> getLessonProgress(@ApiParam("课时ID") @PathVariable Long lessonId) {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        LearningProgress progress = learningProgressService.getUserLessonProgress(userId, lessonId);
        if (progress == null) {
            return Result.success(null); // 未开始学习
        }
        return Result.success(progress);
    }

    @ApiOperation("获取课程学习进度")
    @GetMapping("/courses/{courseId}/progress")
    public Result<List<LearningProgress>> getCourseProgress(@ApiParam("课程ID") @PathVariable Long courseId) {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        List<LearningProgress> progresses = learningProgressService.getUserCourseProgress(userId, courseId);
        return Result.success(progresses);
    }

    @ApiOperation("获取学习统计")
    @GetMapping("/stats")
    public Result<LearningProgressService.LearningProgressStats> getLearningStats() {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        LearningProgressService.LearningProgressStats stats = learningProgressService.getUserLearningStats(userId);
        return Result.success(stats);
    }
}