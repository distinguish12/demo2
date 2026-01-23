package com.edu.modules.exercise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.exercise.entity.Exercise;
import com.edu.modules.exercise.mapper.ExerciseMapper;
import com.edu.modules.exercise.service.ExerciseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 练习题服务实现
 */
@Slf4j
@Service
public class ExerciseServiceImpl extends ServiceImpl<ExerciseMapper, Exercise> implements ExerciseService {

    @Override
    public Exercise createExercise(Exercise exercise) {
        // 参数校验
        if (exercise == null || !StringUtils.hasText(exercise.getTitle())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "练习题标题不能为空");
        }
        if (exercise.getCourseId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "课程ID不能为空");
        }
        if (exercise.getType() == null || exercise.getType() < 1 || exercise.getType() > 4) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "练习题类型无效");
        }

        // 设置默认值
        if (exercise.getScore() == null) {
            exercise.setScore(BigDecimal.valueOf(5.0)); // 默认5分
        }
        if (exercise.getSortOrder() == null) {
            exercise.setSortOrder(0);
        }
        if (exercise.getStatus() == null) {
            exercise.setStatus(1); // 默认启用
        }

        boolean success = save(exercise);
        if (!success) {
            throw new BusinessException("创建练习题失败");
        }

        log.info("练习题创建成功: title={}, courseId={}", exercise.getTitle(), exercise.getCourseId());
        return exercise;
    }

    @Override
    public boolean updateExercise(Exercise exercise) {
        if (exercise == null || exercise.getId() == null) {
            return false;
        }

        boolean success = updateById(exercise);
        if (success) {
            log.info("练习题更新成功: id={}", exercise.getId());
        }
        return success;
    }

    @Override
    public boolean deleteExercise(Long exerciseId) {
        if (exerciseId == null) {
            return false;
        }

        boolean success = removeById(exerciseId);
        if (success) {
            log.info("练习题删除成功: id={}", exerciseId);
        }
        return success;
    }

    @Override
    public List<Exercise> getExercisesByCourseId(Long courseId) {
        if (courseId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Exercise> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exercise::getCourseId, courseId)
               .eq(Exercise::getStatus, 1) // 只查询启用的练习题
               .orderByAsc(Exercise::getSortOrder)
               .orderByAsc(Exercise::getCreateTime);

        return list(wrapper);
    }

    @Override
    public List<Exercise> getExercisesByChapterId(Long chapterId) {
        if (chapterId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Exercise> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exercise::getChapterId, chapterId)
               .eq(Exercise::getStatus, 1) // 只查询启用的练习题
               .orderByAsc(Exercise::getSortOrder)
               .orderByAsc(Exercise::getCreateTime);

        return list(wrapper);
    }

    @Override
    public BigDecimal autoGrade(String userAnswer, String correctAnswer, Integer exerciseType) {
        if (!StringUtils.hasText(userAnswer) || !StringUtils.hasText(correctAnswer)) {
            return BigDecimal.ZERO;
        }

        // 根据题型进行自动评分
        switch (exerciseType) {
            case 1: // 单选题
            case 2: // 多选题
            case 3: // 判断题
                return userAnswer.trim().equalsIgnoreCase(correctAnswer.trim())
                       ? BigDecimal.ONE : BigDecimal.ZERO;
            case 4: // 填空题（简单文本匹配）
                return userAnswer.trim().equalsIgnoreCase(correctAnswer.trim())
                       ? BigDecimal.ONE : BigDecimal.ZERO;
            default:
                return BigDecimal.ZERO;
        }
    }

    @Override
    public boolean manualGrade(Long submissionId, BigDecimal score, String feedback) {
        // 这里可以实现手动评分逻辑
        // 由于需要更新ExerciseSubmission表，这里暂时返回true
        // 实际实现时需要调用ExerciseSubmissionService
        log.info("手动评分: submissionId={}, score={}", submissionId, score);
        return true;
    }
}