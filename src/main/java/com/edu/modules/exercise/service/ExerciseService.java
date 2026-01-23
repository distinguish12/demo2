package com.edu.modules.exercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.exercise.entity.Exercise;
import com.edu.modules.exercise.entity.ExerciseSubmission;

import java.math.BigDecimal;
import java.util.List;

/**
 * 练习题服务接口
 */
public interface ExerciseService extends IService<Exercise> {

    /**
     * 创建练习题
     */
    Exercise createExercise(Exercise exercise);

    /**
     * 更新练习题
     */
    boolean updateExercise(Exercise exercise);

    /**
     * 删除练习题
     */
    boolean deleteExercise(Long exerciseId);

    /**
     * 根据课程ID获取练习题列表
     */
    List<Exercise> getExercisesByCourseId(Long courseId);

    /**
     * 根据章节ID获取练习题列表
     */
    List<Exercise> getExercisesByChapterId(Long chapterId);

    /**
     * 自动评分
     */
    BigDecimal autoGrade(String userAnswer, String correctAnswer, Integer exerciseType);

    /**
     * 手动评分
     */
    boolean manualGrade(Long submissionId, BigDecimal score, String feedback);
}