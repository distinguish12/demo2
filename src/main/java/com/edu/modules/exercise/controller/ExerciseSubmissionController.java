package com.edu.modules.exercise.controller;

import com.edu.common.result.Result;
import com.edu.modules.exercise.entity.ExerciseSubmission;
import com.edu.modules.exercise.service.ExerciseStats;
import com.edu.modules.exercise.service.ExerciseSubmissionService;
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
 * 练习提交控制器
 */
@Slf4j
@Api(tags = "练习提交管理")
@RestController
@RequestMapping("/api/exercises")
@Validated
public class ExerciseSubmissionController {

    @Autowired
    private ExerciseSubmissionService exerciseSubmissionService;

    @ApiOperation("提交练习答案")
    @PostMapping("/{exerciseId}/submit")
    public Result<ExerciseSubmission> submitAnswer(
            @ApiParam("练习题ID") @PathVariable Long exerciseId,
            @ApiParam("答案") @NotBlank(message = "答案不能为空") @RequestParam String answer) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        ExerciseSubmission submission = exerciseSubmissionService.submitAnswer(userId, exerciseId, answer);
        return Result.success("答案提交成功", submission);
    }

    @ApiOperation("重新提交练习答案")
    @PutMapping("/{exerciseId}/resubmit")
    public Result<ExerciseSubmission> resubmitAnswer(
            @ApiParam("练习题ID") @PathVariable Long exerciseId,
            @ApiParam("答案") @NotBlank(message = "答案不能为空") @RequestParam String answer) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        ExerciseSubmission submission = exerciseSubmissionService.resubmitAnswer(userId, exerciseId, answer);
        return Result.success("答案重新提交成功", submission);
    }

    @ApiOperation("获取我的提交记录")
    @GetMapping("/{exerciseId}/my-submission")
    public Result<ExerciseSubmission> getMySubmission(@ApiParam("练习题ID") @PathVariable Long exerciseId) {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        ExerciseSubmission submission = exerciseSubmissionService.getUserSubmission(userId, exerciseId);
        if (submission == null) {
            return Result.success(null); // 未提交过
        }
        return Result.success(submission);
    }

    @ApiOperation("获取练习题的所有提交记录")
    @GetMapping("/{exerciseId}/submissions")
    public Result<List<ExerciseSubmission>> getExerciseSubmissions(@ApiParam("练习题ID") @PathVariable Long exerciseId) {
        List<ExerciseSubmission> submissions = exerciseSubmissionService.getExerciseSubmissions(exerciseId);
        return Result.success(submissions);
    }

    @ApiOperation("获取我的所有提交记录")
    @GetMapping("/my-submissions")
    public Result<List<ExerciseSubmission>> getMySubmissions() {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        List<ExerciseSubmission> submissions = exerciseSubmissionService.getUserSubmissions(userId);
        return Result.success(submissions);
    }

    @ApiOperation("获取练习题统计信息")
    @GetMapping("/{exerciseId}/stats")
    public Result<ExerciseStats> getExerciseStats(@ApiParam("练习题ID") @PathVariable Long exerciseId) {
        ExerciseStats stats = exerciseSubmissionService.getExerciseStats(exerciseId);
        return Result.success(stats);
    }
}