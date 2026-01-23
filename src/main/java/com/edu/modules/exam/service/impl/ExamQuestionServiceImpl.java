package com.edu.modules.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.modules.exam.entity.ExamQuestion;
import com.edu.modules.exam.mapper.ExamQuestionMapper;
import com.edu.modules.exam.service.ExamQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 考试题目服务实现
 */
@Slf4j
@Service
public class ExamQuestionServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion>
        implements ExamQuestionService {

    @Override
    public boolean addQuestionsToExam(Long examId, List<ExamQuestion> questions) {
        if (examId == null || questions == null || questions.isEmpty()) {
            return false;
        }

        // 设置考试ID和排序
        for (int i = 0; i < questions.size(); i++) {
            ExamQuestion question = questions.get(i);
            question.setExamId(examId);
            if (question.getSortOrder() == null) {
                question.setSortOrder(i);
            }
        }

        boolean success = saveBatch(questions);
        if (success) {
            log.info("批量添加考试题目成功: examId={}, count={}", examId, questions.size());
        }
        return success;
    }

    @Override
    public List<ExamQuestion> getQuestionsByExamId(Long examId) {
        if (examId == null) {
            return List.of();
        }

        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, examId)
               .orderByAsc(ExamQuestion::getSortOrder);

        return list(wrapper);
    }

    @Override
    public boolean removeQuestionsByExamId(Long examId) {
        if (examId == null) {
            return false;
        }

        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, examId);

        boolean success = remove(wrapper);
        if (success) {
            log.info("删除考试题目成功: examId={}", examId);
        }
        return success;
    }

    @Override
    public boolean updateQuestionSort(Long examId, List<Long> questionIds) {
        if (examId == null || questionIds == null || questionIds.isEmpty()) {
            return false;
        }

        boolean success = true;
        for (int i = 0; i < questionIds.size(); i++) {
            ExamQuestion question = new ExamQuestion();
            question.setId(questionIds.get(i));
            question.setSortOrder(i);
            success = success && updateById(question);
        }

        if (success) {
            log.info("更新题目排序成功: examId={}", examId);
        }
        return success;
    }
}