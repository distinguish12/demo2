package com.edu.modules.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.exam.entity.ExamQuestion;

import java.util.List;

/**
 * 考试题目服务接口
 */
public interface ExamQuestionService extends IService<ExamQuestion> {

    /**
     * 批量添加考试题目
     */
    boolean addQuestionsToExam(Long examId, List<ExamQuestion> questions);

    /**
     * 根据考试ID获取题目列表
     */
    List<ExamQuestion> getQuestionsByExamId(Long examId);

    /**
     * 删除考试的所有题目
     */
    boolean removeQuestionsByExamId(Long examId);

    /**
     * 更新题目排序
     */
    boolean updateQuestionSort(Long examId, List<Long> questionIds);
}