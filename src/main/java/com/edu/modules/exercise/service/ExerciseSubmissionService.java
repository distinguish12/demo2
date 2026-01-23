package com.edu.modules.exercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.exercise.entity.ExerciseSubmission;

import java.util.List;

/**
 * 练习提交服务接口
 */
public interface ExerciseSubmissionService extends IService<ExerciseSubmission> {

    /**
     * 提交练习答案
     */
    ExerciseSubmission submitAnswer(Long userId, Long exerciseId, String answer);

    /**
     * 重新提交答案
     */
    ExerciseSubmission resubmitAnswer(Long userId, Long exerciseId, String answer);

    /**
     * 获取用户练习提交记录
     */
    ExerciseSubmission getUserSubmission(Long userId, Long exerciseId);

    /**
     * 获取练习的提交记录列表
     */
    List<ExerciseSubmission> getExerciseSubmissions(Long exerciseId);

    /**
     * 获取用户的所有提交记录
     */
    List<ExerciseSubmission> getUserSubmissions(Long userId);

    /**
     * 获取练习的提交统计
     */
    ExerciseStats getExerciseStats(Long exerciseId);
}