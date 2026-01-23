package com.edu.modules.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.exam.entity.Exam;
import com.edu.modules.exam.entity.ExamQuestion;
import com.edu.modules.exam.entity.ExamRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 考试服务接口
 */
public interface ExamService extends IService<Exam> {

    /**
     * 创建考试
     */
    Exam createExam(Exam exam);

    /**
     * 更新考试
     */
    boolean updateExam(Exam exam);

    /**
     * 删除考试
     */
    boolean deleteExam(Long examId);

    /**
     * 根据课程ID获取考试列表
     */
    List<Exam> getExamsByCourseId(Long courseId);

    /**
     * 开始考试
     */
    ExamRecord startExam(Long userId, Long examId);

    /**
     * 提交考试
     */
    ExamRecord submitExam(Long userId, Long examId, Map<Long, String> answers);

    /**
     * 自动评分
     */
    BigDecimal autoGrade(Map<Long, String> userAnswers, List<ExamQuestion> questions);

    /**
     * 检查考试状态
     */
    ExamStatus checkExamStatus(Exam exam);

    /**
     * 获取考试统计
     */
    ExamStats getExamStats(Long examId);
}