package com.edu.modules.exercise.controller;

import com.edu.common.result.Result;
import com.edu.modules.exercise.entity.Exercise;
import com.edu.modules.exercise.service.ExerciseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 练习题控制器
 */
@Slf4j
@Api(tags = "练习题管理")
@RestController
@RequestMapping("/api/exercises")
@Validated
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @ApiOperation("创建练习题")
    @PostMapping
    public Result<Exercise> createExercise(
            @ApiParam("课程ID") @NotNull(message = "课程ID不能为空") @RequestParam Long courseId,
            @ApiParam("章节ID") @RequestParam(required = false) Long chapterId,
            @ApiParam("题目") @NotBlank(message = "题目不能为空") @RequestParam String title,
            @ApiParam("题目内容") @RequestParam(required = false) String content,
            @ApiParam("类型：1-单选，2-多选，3-判断，4-填空") @NotNull(message = "类型不能为空") @RequestParam Integer type,
            @ApiParam("选项（JSON格式）") @RequestParam(required = false) String options,
            @ApiParam("答案") @RequestParam(required = false) String answer,
            @ApiParam("解析") @RequestParam(required = false) String explanation,
            @ApiParam("分数") @RequestParam(defaultValue = "5") BigDecimal score) {

        Exercise exercise = new Exercise();
        exercise.setCourseId(courseId);
        exercise.setChapterId(chapterId);
        exercise.setTitle(title);
        exercise.setContent(content);
        exercise.setType(type);
        exercise.setOptions(options);
        exercise.setAnswer(answer);
        exercise.setExplanation(explanation);
        exercise.setScore(score);

        Exercise result = exerciseService.createExercise(exercise);
        return Result.success("练习题创建成功", result);
    }

    @ApiOperation("更新练习题")
    @PutMapping("/{id}")
    public Result<String> updateExercise(
            @ApiParam("练习题ID") @PathVariable Long id,
            @ApiParam("题目") @RequestParam(required = false) String title,
            @ApiParam("题目内容") @RequestParam(required = false) String content,
            @ApiParam("选项（JSON格式）") @RequestParam(required = false) String options,
            @ApiParam("答案") @RequestParam(required = false) String answer,
            @ApiParam("解析") @RequestParam(required = false) String explanation,
            @ApiParam("分数") @RequestParam(required = false) BigDecimal score) {

        Exercise exercise = new Exercise();
        exercise.setId(id);
        exercise.setTitle(title);
        exercise.setContent(content);
        exercise.setOptions(options);
        exercise.setAnswer(answer);
        exercise.setExplanation(explanation);
        exercise.setScore(score);

        boolean success = exerciseService.updateExercise(exercise);
        return success ? Result.success("练习题更新成功") : Result.fail("练习题更新失败");
    }

    @ApiOperation("删除练习题")
    @DeleteMapping("/{id}")
    public Result<String> deleteExercise(@ApiParam("练习题ID") @PathVariable Long id) {
        boolean success = exerciseService.deleteExercise(id);
        return success ? Result.success("练习题删除成功") : Result.fail("练习题删除失败");
    }

    @ApiOperation("获取练习题详情")
    @GetMapping("/{id}")
    public Result<Exercise> getExercise(@ApiParam("练习题ID") @PathVariable Long id) {
        Exercise exercise = exerciseService.getById(id);
        return exercise != null ? Result.success(exercise) : Result.fail("练习题不存在");
    }

    @ApiOperation("获取课程练习题列表")
    @GetMapping("/course/{courseId}")
    public Result<List<Exercise>> getExercisesByCourse(@ApiParam("课程ID") @PathVariable Long courseId) {
        List<Exercise> exercises = exerciseService.getExercisesByCourseId(courseId);
        return Result.success(exercises);
    }

    @ApiOperation("获取章节练习题列表")
    @GetMapping("/chapter/{chapterId}")
    public Result<List<Exercise>> getExercisesByChapter(@ApiParam("章节ID") @PathVariable Long chapterId) {
        List<Exercise> exercises = exerciseService.getExercisesByChapterId(chapterId);
        return Result.success(exercises);
    }

    @ApiOperation("手动评分")
    @PostMapping("/{id}/grade")
    public Result<String> manualGrade(
            @ApiParam("练习提交ID") @PathVariable Long id,
            @ApiParam("分数") @NotNull(message = "分数不能为空") @RequestParam BigDecimal score,
            @ApiParam("反馈") @RequestParam(required = false) String feedback) {

        boolean success = exerciseService.manualGrade(id, score, feedback);
        return success ? Result.success("评分成功") : Result.fail("评分失败");
    }
}