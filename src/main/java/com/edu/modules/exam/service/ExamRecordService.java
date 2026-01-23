package com.edu.modules.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.exam.entity.ExamRecord;

import java.util.List;

/**
 * 考试记录服务接口
 */
public interface ExamRecordService extends IService<ExamRecord> {

    /**
     * 获取用户考试记录
     */
    ExamRecord getUserExamRecord(Long userId, Long examId);

    /**
     * 获取用户的考试记录列表
     */
    List<ExamRecord> getUserExamRecords(Long userId);

    /**
     * 获取考试的所有记录
     */
    List<ExamRecord> getExamRecords(Long examId);

    /**
     * 检查用户是否已参加考试
     */
    boolean hasUserTakenExam(Long userId, Long examId);

    /**
     * 获取考试平均分
     */
    double getExamAverageScore(Long examId);

    /**
     * 获取考试最高分
     */
    java.math.BigDecimal getExamHighestScore(Long examId);
}