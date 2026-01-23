package com.edu.modules.course.controller;

import com.edu.common.result.Result;
import com.edu.modules.course.entity.CourseChapter;
import com.edu.modules.course.service.CourseChapterService;
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
 * 课程章节控制器
 */
@Slf4j
@Api(tags = "课程章节管理")
@RestController
@RequestMapping("/api/courses/{courseId}/chapters")
@Validated
public class CourseChapterController {

    @Autowired
    private CourseChapterService courseChapterService;

    @ApiOperation("创建章节")
    @PostMapping
    public Result<CourseChapter> createChapter(
            @ApiParam("课程ID") @PathVariable Long courseId,
            @ApiParam("章节标题") @NotBlank(message = "章节标题不能为空") @RequestParam String title,
            @ApiParam("章节描述") @RequestParam(required = false) String description,
            @ApiParam("排序") @RequestParam(defaultValue = "0") Integer sortOrder) {

        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(courseId);
        chapter.setTitle(title);
        chapter.setDescription(description);
        chapter.setSortOrder(sortOrder);

        CourseChapter result = courseChapterService.createChapter(chapter);
        return Result.success("章节创建成功", result);
    }

    @ApiOperation("更新章节")
    @PutMapping("/{chapterId}")
    public Result<String> updateChapter(
            @ApiParam("课程ID") @PathVariable Long courseId,
            @ApiParam("章节ID") @PathVariable Long chapterId,
            @ApiParam("章节标题") @RequestParam(required = false) String title,
            @ApiParam("章节描述") @RequestParam(required = false) String description,
            @ApiParam("排序") @RequestParam(required = false) Integer sortOrder) {

        CourseChapter chapter = new CourseChapter();
        chapter.setId(chapterId);
        chapter.setTitle(title);
        chapter.setDescription(description);
        chapter.setSortOrder(sortOrder);

        boolean success = courseChapterService.updateChapter(chapter);
        return success ? Result.success("章节更新成功") : Result.fail("章节更新失败");
    }

    @ApiOperation("删除章节")
    @DeleteMapping("/{chapterId}")
    public Result<String> deleteChapter(
            @ApiParam("课程ID") @PathVariable Long courseId,
            @ApiParam("章节ID") @PathVariable Long chapterId) {

        boolean success = courseChapterService.deleteChapter(chapterId);
        return success ? Result.success("章节删除成功") : Result.fail("章节删除失败");
    }

    @ApiOperation("获取章节列表")
    @GetMapping
    public Result<List<CourseChapter>> getChapters(@ApiParam("课程ID") @PathVariable Long courseId) {
        List<CourseChapter> chapters = courseChapterService.getChaptersByCourseId(courseId);
        return Result.success(chapters);
    }

    @ApiOperation("获取章节详情")
    @GetMapping("/{chapterId}")
    public Result<CourseChapter> getChapter(
            @ApiParam("课程ID") @PathVariable Long courseId,
            @ApiParam("章节ID") @PathVariable Long chapterId) {

        CourseChapter chapter = courseChapterService.getById(chapterId);
        if (chapter == null || !chapter.getCourseId().equals(courseId)) {
            return Result.fail("章节不存在");
        }
        return Result.success(chapter);
    }

    @ApiOperation("更新章节排序")
    @PutMapping("/sort")
    public Result<String> updateChapterSort(
            @ApiParam("课程ID") @PathVariable Long courseId,
            @ApiParam("章节ID列表，按顺序排列") @RequestBody List<Long> chapterIds) {

        boolean success = courseChapterService.updateChapterSort(chapterIds);
        return success ? Result.success("章节排序更新成功") : Result.fail("章节排序更新失败");
    }
}