package com.edu.modules.exam.controller;

import com.edu.common.result.Result;
import com.edu.modules.exam.entity.Exam;
import com.edu.modules.exam.entity.ExamQuestion;
import com.edu.modules.exam.entity.ExamRecord;
import com.edu.modules.exam.service.ExamService;
import com.edu.modules.exam.service.ExamQuestionService;
import com.edu.modules.exam.service.ExamRecordService;
import com.edu.modules.exam.service.ExamStats;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 考试控制器
 */
@Slf4j
@Api(tags = "考试管理")
@RestController
@RequestMapping("/api/exams")
@Validated
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamQuestionService examQuestionService;

    @Autowired
    private ExamRecordService examRecordService;

    @ApiOperation("创建考试")
    @PostMapping
    public Result<Exam> createExam(
            @ApiParam("课程ID") @NotNull(message = "课程ID不能为空") @RequestParam Long courseId,
            @ApiParam("考试标题") @NotBlank(message = "考试标题不能为空") @RequestParam String title,
            @ApiParam("考试描述") @RequestParam(required = false) String description,
            @ApiParam("考试时长(分钟)") @RequestParam(defaultValue = "60") Integer duration,
            @ApiParam("总分") @RequestParam(defaultValue = "100") BigDecimal totalScore,
            @ApiParam("及格分数") @RequestParam(defaultValue = "60") BigDecimal passScore,
            @ApiParam("开始时间") @RequestParam(required = false) LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) LocalDateTime endTime) {

        Exam exam = new Exam();
        exam.setCourseId(courseId);
        exam.setTitle(title);
        exam.setDescription(description);
        exam.setDuration(duration);
        exam.setTotalScore(totalScore);
        exam.setPassScore(passScore);
        exam.setStartTime(startTime);
        exam.setEndTime(endTime);

        Exam result = examService.createExam(exam);
        return Result.success("考试创建成功", result);
    }

    @ApiOperation("更新考试")
    @PutMapping("/{id}")
    public Result<String> updateExam(
            @ApiParam("考试ID") @PathVariable Long id,
            @ApiParam("考试标题") @RequestParam(required = false) String title,
            @ApiParam("考试描述") @RequestParam(required = false) String description,
            @ApiParam("考试时长") @RequestParam(required = false) Integer duration,
            @ApiParam("总分") @RequestParam(required = false) BigDecimal totalScore,
            @ApiParam("及格分数") @RequestParam(required = false) BigDecimal passScore,
            @ApiParam("开始时间") @RequestParam(required = false) LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) LocalDateTime endTime) {

        Exam exam = new Exam();
        exam.setId(id);
        exam.setTitle(title);
        exam.setDescription(description);
        exam.setDuration(duration);
        exam.setTotalScore(totalScore);
        exam.setPassScore(passScore);
        exam.setStartTime(startTime);
        exam.setEndTime(endTime);

        boolean success = examService.updateExam(exam);
        return success ? Result.success("考试更新成功") : Result.fail("考试更新失败");
    }

    @ApiOperation("删除考试")
    @DeleteMapping("/{id}")
    public Result<String> deleteExam(@ApiParam("考试ID") @PathVariable Long id) {
        boolean success = examService.deleteExam(id);
        return success ? Result.success("考试删除成功") : Result.fail("考试删除失败");
    }

    @ApiOperation("获取考试详情")
    @GetMapping("/{id}")
    public Result<Exam> getExam(@ApiParam("考试ID") @PathVariable Long id) {
        Exam exam = examService.getById(id);
        return exam != null ? Result.success(exam) : Result.fail("考试不存在");
    }

    @ApiOperation("获取课程考试列表")
    @GetMapping("/course/{courseId}")
    public Result<List<Exam>> getCourseExams(@ApiParam("课程ID") @PathVariable Long courseId) {
        List<Exam> exams = examService.getExamsByCourseId(courseId);
        return Result.success(exams);
    }

    @ApiOperation("开始考试")
    @PostMapping("/{examId}/start")
    public Result<ExamRecord> startExam(@ApiParam("考试ID") @PathVariable Long examId) {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        ExamRecord record = examService.startExam(userId, examId);
        return Result.success("考试开始", record);
    }

    @ApiOperation("提交考试")
    @PostMapping("/{examId}/submit")
    public Result<ExamRecord> submitExam(
            @ApiParam("考试ID") @PathVariable Long examId,
            @ApiParam("答案Map，key为题目ID，value为答案") @RequestBody Map<Long, String> answers) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        ExamRecord record = examService.submitExam(userId, examId, answers);
        return Result.success("考试提交成功", record);
    }

    @ApiOperation("获取考试题目")
    @GetMapping("/{examId}/questions")
    public Result<List<ExamQuestion>> getExamQuestions(@ApiParam("考试ID") @PathVariable Long examId) {
        List<ExamQuestion> questions = examQuestionService.getQuestionsByExamId(examId);
        return Result.success(questions);
    }

    @ApiOperation("获取考试统计")
    @GetMapping("/{examId}/stats")
    public Result<ExamStats> getExamStats(@ApiParam("考试ID") @PathVariable Long examId) {
        ExamStats stats = examService.getExamStats(examId);
        return Result.success(stats);
    }

    @ApiOperation("获取我的考试记录")
    @GetMapping("/my-records")
    public Result<List<ExamRecord>> getMyExamRecords() {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        List<ExamRecord> records = examRecordService.getUserExamRecords(userId);
        return Result.success(records);
    }
}