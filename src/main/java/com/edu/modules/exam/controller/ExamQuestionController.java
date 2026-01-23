package com.edu.modules.exam.controller;

import com.edu.common.result.Result;
import com.edu.modules.exam.entity.ExamQuestion;
import com.edu.modules.exam.service.ExamQuestionService;
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
 * 考试题目控制器
 */
@Slf4j
@Api(tags = "考试题目管理")
@RestController
@RequestMapping("/api/exams/{examId}/questions")
@Validated
public class ExamQuestionController {

    @Autowired
    private ExamQuestionService examQuestionService;

    @ApiOperation("批量添加考试题目")
    @PostMapping("/batch")
    public Result<String> addQuestions(
            @ApiParam("考试ID") @PathVariable Long examId,
            @ApiParam("题目列表") @RequestBody List<ExamQuestion> questions) {

        boolean success = examQuestionService.addQuestionsToExam(examId, questions);
        return success ? Result.success("题目添加成功") : Result.fail("题目添加失败");
    }

    @ApiOperation("添加单个题目")
    @PostMapping
    public Result<ExamQuestion> addQuestion(
            @ApiParam("考试ID") @PathVariable Long examId,
            @ApiParam("题目") @NotBlank(message = "题目不能为空") @RequestParam String title,
            @ApiParam("题目内容") @RequestParam(required = false) String content,
            @ApiParam("类型：1-单选，2-多选，3-判断，4-填空") @NotNull(message = "类型不能为空") @RequestParam Integer type,
            @ApiParam("选项（JSON格式）") @RequestParam(required = false) String options,
            @ApiParam("答案") @RequestParam(required = false) String answer,
            @ApiParam("分数") @RequestParam(defaultValue = "5") BigDecimal score,
            @ApiParam("排序") @RequestParam(defaultValue = "0") Integer sortOrder) {

        ExamQuestion question = new ExamQuestion();
        question.setExamId(examId);
        question.setTitle(title);
        question.setContent(content);
        question.setType(type);
        question.setOptions(options);
        question.setAnswer(answer);
        question.setScore(score);
        question.setSortOrder(sortOrder);

        boolean success = examQuestionService.save(question);
        return success ? Result.success("题目添加成功", question) : Result.fail("题目添加失败");
    }

    @ApiOperation("更新题目")
    @PutMapping("/{questionId}")
    public Result<String> updateQuestion(
            @ApiParam("考试ID") @PathVariable Long examId,
            @ApiParam("题目ID") @PathVariable Long questionId,
            @ApiParam("题目") @RequestParam(required = false) String title,
            @ApiParam("题目内容") @RequestParam(required = false) String content,
            @ApiParam("选项（JSON格式）") @RequestParam(required = false) String options,
            @ApiParam("答案") @RequestParam(required = false) String answer,
            @ApiParam("分数") @RequestParam(required = false) BigDecimal score,
            @ApiParam("排序") @RequestParam(required = false) Integer sortOrder) {

        ExamQuestion question = new ExamQuestion();
        question.setId(questionId);
        question.setTitle(title);
        question.setContent(content);
        question.setOptions(options);
        question.setAnswer(answer);
        question.setScore(score);
        question.setSortOrder(sortOrder);

        boolean success = examQuestionService.updateById(question);
        return success ? Result.success("题目更新成功") : Result.fail("题目更新失败");
    }

    @ApiOperation("删除题目")
    @DeleteMapping("/{questionId}")
    public Result<String> deleteQuestion(
            @ApiParam("考试ID") @PathVariable Long examId,
            @ApiParam("题目ID") @PathVariable Long questionId) {

        boolean success = examQuestionService.removeById(questionId);
        return success ? Result.success("题目删除成功") : Result.fail("题目删除失败");
    }

    @ApiOperation("获取题目详情")
    @GetMapping("/{questionId}")
    public Result<ExamQuestion> getQuestion(
            @ApiParam("考试ID") @PathVariable Long examId,
            @ApiParam("题目ID") @PathVariable Long questionId) {

        ExamQuestion question = examQuestionService.getById(questionId);
        if (question == null || !question.getExamId().equals(examId)) {
            return Result.fail("题目不存在");
        }
        return Result.success(question);
    }

    @ApiOperation("更新题目排序")
    @PutMapping("/sort")
    public Result<String> updateQuestionSort(
            @ApiParam("考试ID") @PathVariable Long examId,
            @ApiParam("题目ID列表，按顺序排列") @RequestBody List<Long> questionIds) {

        boolean success = examQuestionService.updateQuestionSort(examId, questionIds);
        return success ? Result.success("题目排序更新成功") : Result.fail("题目排序更新失败");
    }
}