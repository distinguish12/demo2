package com.edu.modules.exercise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.exercise.entity.Exercise;
import com.edu.modules.exercise.entity.ExerciseSubmission;
import com.edu.modules.exercise.mapper.ExerciseSubmissionMapper;
import com.edu.modules.exercise.service.ExerciseService;
import com.edu.modules.exercise.service.ExerciseStats;
import com.edu.modules.exercise.service.ExerciseSubmissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 练习提交服务实现
 */
@Slf4j
@Service
public class ExerciseSubmissionServiceImpl extends ServiceImpl<ExerciseSubmissionMapper, ExerciseSubmission>
        implements ExerciseSubmissionService {

    @Autowired
    private ExerciseService exerciseService;

    @Override
    public ExerciseSubmission submitAnswer(Long userId, Long exerciseId, String answer) {
        // 参数校验
        if (userId == null || exerciseId == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户ID和练习ID不能为空");
        }
        if (!StringUtils.hasText(answer)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "答案不能为空");
        }

        // 检查练习题是否存在
        Exercise exercise = exerciseService.getById(exerciseId);
        if (exercise == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "练习题不存在");
        }

        // 检查是否已提交过
        ExerciseSubmission existing = getUserSubmission(userId, exerciseId);
        if (existing != null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "该练习已提交过答案，请使用重新提交功能");
        }

        // 创建提交记录
        ExerciseSubmission submission = new ExerciseSubmission();
        submission.setUserId(userId);
        submission.setExerciseId(exerciseId);
        submission.setAnswer(answer.trim());
        submission.setSubmitTime(LocalDateTime.now());

        // 自动评分
        BigDecimal score = exerciseService.autoGrade(answer, exercise.getAnswer(), exercise.getType());
        BigDecimal maxScore = exercise.getScore() != null ? exercise.getScore() : BigDecimal.ONE;
        BigDecimal actualScore = score.multiply(maxScore);

        submission.setScore(actualScore);
        submission.setIsCorrect(score.compareTo(BigDecimal.ONE) == 0 ? 1 : 0);

        boolean success = save(submission);
        if (!success) {
            throw new BusinessException("提交答案失败");
        }

        log.info("练习答案提交成功: userId={}, exerciseId={}, score={}", userId, exerciseId, actualScore);
        return submission;
    }

    @Override
    public ExerciseSubmission resubmitAnswer(Long userId, Long exerciseId, String answer) {
        // 参数校验
        if (userId == null || exerciseId == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户ID和练习ID不能为空");
        }
        if (!StringUtils.hasText(answer)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "答案不能为空");
        }

        // 检查练习题是否存在
        Exercise exercise = exerciseService.getById(exerciseId);
        if (exercise == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "练习题不存在");
        }

        // 获取现有提交记录
        ExerciseSubmission submission = getUserSubmission(userId, exerciseId);
        if (submission == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "未找到提交记录，请先提交答案");
        }

        // 更新答案和评分
        submission.setAnswer(answer.trim());
        submission.setSubmitTime(LocalDateTime.now());

        // 重新自动评分
        BigDecimal score = exerciseService.autoGrade(answer, exercise.getAnswer(), exercise.getType());
        BigDecimal maxScore = exercise.getScore() != null ? exercise.getScore() : BigDecimal.ONE;
        BigDecimal actualScore = score.multiply(maxScore);

        submission.setScore(actualScore);
        submission.setIsCorrect(score.compareTo(BigDecimal.ONE) == 0 ? 1 : 0);

        boolean success = updateById(submission);
        if (!success) {
            throw new BusinessException("重新提交答案失败");
        }

        log.info("练习答案重新提交成功: userId={}, exerciseId={}, score={}", userId, exerciseId, actualScore);
        return submission;
    }

    @Override
    public ExerciseSubmission getUserSubmission(Long userId, Long exerciseId) {
        if (userId == null || exerciseId == null) {
            return null;
        }

        LambdaQueryWrapper<ExerciseSubmission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExerciseSubmission::getUserId, userId)
               .eq(ExerciseSubmission::getExerciseId, exerciseId)
               .orderByDesc(ExerciseSubmission::getSubmitTime)
               .last("limit 1");

        return getOne(wrapper);
    }

    @Override
    public List<ExerciseSubmission> getExerciseSubmissions(Long exerciseId) {
        if (exerciseId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<ExerciseSubmission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExerciseSubmission::getExerciseId, exerciseId)
               .orderByDesc(ExerciseSubmission::getSubmitTime);

        return list(wrapper);
    }

    @Override
    public List<ExerciseSubmission> getUserSubmissions(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<ExerciseSubmission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExerciseSubmission::getUserId, userId)
               .orderByDesc(ExerciseSubmission::getSubmitTime);

        return list(wrapper);
    }

    @Override
    public ExerciseStats getExerciseStats(Long exerciseId) {
        if (exerciseId == null) {
            return new ExerciseStats(0, 0, 0.0, 0.0);
        }

        List<ExerciseSubmission> submissions = getExerciseSubmissions(exerciseId);

        int totalSubmissions = submissions.size();
        if (totalSubmissions == 0) {
            return new ExerciseStats(0, 0, 0.0, 0.0);
        }

        int correctSubmissions = (int) submissions.stream()
                .filter(s -> s.getIsCorrect() != null && s.getIsCorrect() == 1)
                .count();

        double averageScore = submissions.stream()
                .filter(s -> s.getScore() != null)
                .mapToDouble(s -> s.getScore().doubleValue())
                .average()
                .orElse(0.0);

        double correctRate = (double) correctSubmissions / totalSubmissions;

        return new ExerciseStats(totalSubmissions, correctSubmissions, averageScore, correctRate);
    }
}