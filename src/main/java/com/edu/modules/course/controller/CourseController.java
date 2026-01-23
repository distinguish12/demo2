package com.edu.modules.course.controller;

import com.edu.common.result.Result;
import com.edu.modules.course.entity.Course;
import com.edu.modules.course.service.CourseService;
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
 * 课程控制器
 */
@Slf4j
@Api(tags = "课程管理")
@RestController
@RequestMapping("/api/courses")
@Validated
public class CourseController {

    @Autowired
    private CourseService courseService;

    @ApiOperation("创建课程")
    @PostMapping
    public Result<Course> createCourse(
            @ApiParam("课程标题") @NotBlank(message = "课程标题不能为空") @RequestParam String title,
            @ApiParam("课程描述") @RequestParam(required = false) String description,
            @ApiParam("封面图片URL") @RequestParam(required = false) String coverUrl,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("讲师ID") @NotNull(message = "讲师ID不能为空") @RequestParam Long instructorId,
            @ApiParam("价格") @RequestParam(defaultValue = "0") java.math.BigDecimal price,
            @ApiParam("难度级别：1-初级，2-中级，3-高级") @RequestParam(defaultValue = "1") Integer level) {

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setCoverUrl(coverUrl);
        course.setCategoryId(categoryId);
        course.setInstructorId(instructorId);
        course.setPrice(price);
        course.setLevel(level);

        Course result = courseService.createCourse(course);
        return Result.success("课程创建成功", result);
    }

    @ApiOperation("更新课程")
    @PutMapping("/{id}")
    public Result<String> updateCourse(
            @ApiParam("课程ID") @PathVariable Long id,
            @ApiParam("课程标题") @RequestParam(required = false) String title,
            @ApiParam("课程描述") @RequestParam(required = false) String description,
            @ApiParam("封面图片URL") @RequestParam(required = false) String coverUrl,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("价格") @RequestParam(required = false) java.math.BigDecimal price,
            @ApiParam("难度级别") @RequestParam(required = false) Integer level) {

        Course course = new Course();
        course.setId(id);
        course.setTitle(title);
        course.setDescription(description);
        course.setCoverUrl(coverUrl);
        course.setCategoryId(categoryId);
        course.setPrice(price);
        course.setLevel(level);

        boolean success = courseService.updateCourse(course);
        return success ? Result.success("课程更新成功") : Result.fail("课程更新失败");
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/{id}")
    public Result<String> deleteCourse(@ApiParam("课程ID") @PathVariable Long id) {
        boolean success = courseService.deleteCourse(id);
        return success ? Result.success("课程删除成功") : Result.fail("课程删除失败");
    }

    @ApiOperation("获取课程详情")
    @GetMapping("/{id}")
    public Result<Course> getCourse(@ApiParam("课程ID") @PathVariable Long id) {
        Course course = courseService.getById(id);
        return course != null ? Result.success(course) : Result.fail("课程不存在");
    }

    @ApiOperation("获取课程列表")
    @GetMapping
    public Result<List<Course>> getCourses(
            @ApiParam("讲师ID") @RequestParam(required = false) Long instructorId,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("状态：0-草稿，1-发布，2-下架") @RequestParam(required = false) Integer status) {

        List<Course> courses;
        if (instructorId != null) {
            courses = courseService.getCoursesByInstructor(instructorId);
        } else if (categoryId != null) {
            courses = courseService.getCoursesByCategory(categoryId);
        } else {
            courses = courseService.list();
        }

        return Result.success(courses);
    }

    @ApiOperation("发布课程")
    @PutMapping("/{id}/publish")
    public Result<String> publishCourse(@ApiParam("课程ID") @PathVariable Long id) {
        boolean success = courseService.publishCourse(id);
        return success ? Result.success("课程发布成功") : Result.fail("课程发布失败");
    }

    @ApiOperation("下架课程")
    @PutMapping("/{id}/unpublish")
    public Result<String> unpublishCourse(@ApiParam("课程ID") @PathVariable Long id) {
        boolean success = courseService.unpublishCourse(id);
        return success ? Result.success("课程下架成功") : Result.fail("课程下架失败");
    }

    @ApiOperation("获取热门课程")
    @GetMapping("/hot")
    public Result<List<Course>> getHotCourses(
            @ApiParam("数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Course> courses = courseService.getHotCourses(limit);
        return Result.success(courses);
    }

    @ApiOperation("搜索课程")
    @GetMapping("/search")
    public Result<List<Course>> searchCourses(
            @ApiParam("搜索关键词") @RequestParam String keyword) {
        List<Course> courses = courseService.searchCourses(keyword);
        return Result.success(courses);
    }
}