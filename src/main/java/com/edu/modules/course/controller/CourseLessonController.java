package com.edu.modules.course.controller;

import com.edu.common.result.Result;
import com.edu.modules.course.entity.CourseLesson;
import com.edu.modules.course.service.CourseLessonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 课程课时控制器
 */
@Slf4j
@Api(tags = "课程课时管理")
@RestController
@RequestMapping("/api/chapters/{chapterId}/lessons")
@Validated
public class CourseLessonController {

    @Autowired
    private CourseLessonService courseLessonService;

    @ApiOperation("创建课时")
    @PostMapping
    public Result<CourseLesson> createLesson(
            @ApiParam("章节ID") @PathVariable Long chapterId,
            @ApiParam("课时标题") @NotBlank(message = "课时标题不能为空") @RequestParam String title,
            @ApiParam("课时描述") @RequestParam(required = false) String description,
            @ApiParam("视频URL") @RequestParam(required = false) String videoUrl,
            @ApiParam("时长（秒）") @RequestParam(defaultValue = "0") Integer duration,
            @ApiParam("排序") @RequestParam(defaultValue = "0") Integer sortOrder,
            @ApiParam("是否免费：0-收费，1-免费") @RequestParam(defaultValue = "0") Integer isFree) {

        CourseLesson lesson = new CourseLesson();
        lesson.setChapterId(chapterId);
        lesson.setTitle(title);
        lesson.setDescription(description);
        lesson.setVideoUrl(videoUrl);
        lesson.setDuration(duration);
        lesson.setSortOrder(sortOrder);
        lesson.setIsFree(isFree);

        CourseLesson result = courseLessonService.createLesson(lesson);
        return Result.success("课时创建成功", result);
    }

    @ApiOperation("更新课时")
    @PutMapping("/{lessonId}")
    public Result<String> updateLesson(
            @ApiParam("章节ID") @PathVariable Long chapterId,
            @ApiParam("课时ID") @PathVariable Long lessonId,
            @ApiParam("课时标题") @RequestParam(required = false) String title,
            @ApiParam("课时描述") @RequestParam(required = false) String description,
            @ApiParam("视频URL") @RequestParam(required = false) String videoUrl,
            @ApiParam("时长（秒）") @RequestParam(required = false) Integer duration,
            @ApiParam("排序") @RequestParam(required = false) Integer sortOrder,
            @ApiParam("是否免费") @RequestParam(required = false) Integer isFree) {

        CourseLesson lesson = new CourseLesson();
        lesson.setId(lessonId);
        lesson.setTitle(title);
        lesson.setDescription(description);
        lesson.setVideoUrl(videoUrl);
        lesson.setDuration(duration);
        lesson.setSortOrder(sortOrder);
        lesson.setIsFree(isFree);

        boolean success = courseLessonService.updateLesson(lesson);
        return success ? Result.success("课时更新成功") : Result.fail("课时更新失败");
    }

    @ApiOperation("删除课时")
    @DeleteMapping("/{lessonId}")
    public Result<String> deleteLesson(
            @ApiParam("章节ID") @PathVariable Long chapterId,
            @ApiParam("课时ID") @PathVariable Long lessonId) {

        boolean success = courseLessonService.deleteLesson(lessonId);
        return success ? Result.success("课时删除成功") : Result.fail("课时删除失败");
    }

    @ApiOperation("获取课时列表")
    @GetMapping
    public Result<List<CourseLesson>> getLessons(@ApiParam("章节ID") @PathVariable Long chapterId) {
        List<CourseLesson> lessons = courseLessonService.getLessonsByChapterId(chapterId);
        return Result.success(lessons);
    }

    @ApiOperation("获取课时详情")
    @GetMapping("/{lessonId}")
    public Result<CourseLesson> getLesson(
            @ApiParam("章节ID") @PathVariable Long chapterId,
            @ApiParam("课时ID") @PathVariable Long lessonId) {

        CourseLesson lesson = courseLessonService.getById(lessonId);
        if (lesson == null || !lesson.getChapterId().equals(chapterId)) {
            return Result.fail("课时不存在");
        }
        return Result.success(lesson);
    }

    @ApiOperation("更新课时排序")
    @PutMapping("/sort")
    public Result<String> updateLessonSort(
            @ApiParam("章节ID") @PathVariable Long chapterId,
            @ApiParam("课时ID列表，按顺序排列") @RequestBody List<Long> lessonIds) {

        boolean success = courseLessonService.updateLessonSort(lessonIds);
        return success ? Result.success("课时排序更新成功") : Result.fail("课时排序更新失败");
    }
}