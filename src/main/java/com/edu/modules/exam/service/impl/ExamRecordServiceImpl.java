package com.edu.modules.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.modules.exam.entity.ExamRecord;
import com.edu.modules.exam.mapper.ExamRecordMapper;
import com.edu.modules.exam.service.ExamRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 考试记录服务实现
 */
@Slf4j
@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord>
        implements ExamRecordService {

    @Override
    public ExamRecord getUserExamRecord(Long userId, Long examId) {
        if (userId == null || examId == null) {
            return null;
        }

        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getUserId, userId)
               .eq(ExamRecord::getExamId, examId);

        return getOne(wrapper);
    }

    @Override
    public List<ExamRecord> getUserExamRecords(Long userId) {
        if (userId == null) {
            return List.of();
        }

        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getUserId, userId)
               .orderByDesc(ExamRecord::getSubmitTime);

        return list(wrapper);
    }

    @Override
    public List<ExamRecord> getExamRecords(Long examId) {
        if (examId == null) {
            return List.of();
        }

        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
               .orderByDesc(ExamRecord::getSubmitTime);

        return list(wrapper);
    }

    @Override
    public boolean hasUserTakenExam(Long userId, Long examId) {
        if (userId == null || examId == null) {
            return false;
        }

        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getUserId, userId)
               .eq(ExamRecord::getExamId, examId)
               .eq(ExamRecord::getStatus, 1); // 已完成

        return count(wrapper) > 0;
    }

    @Override
    public double getExamAverageScore(Long examId) {
        if (examId == null) {
            return 0.0;
        }

        List<ExamRecord> records = getExamRecords(examId);
        if (records.isEmpty()) {
            return 0.0;
        }

        return records.stream()
                .filter(r -> r.getScore() != null && r.getStatus() == 1)
                .mapToDouble(r -> r.getScore().doubleValue())
                .average()
                .orElse(0.0);
    }

    @Override
    public BigDecimal getExamHighestScore(Long examId) {
        if (examId == null) {
            return BigDecimal.ZERO;
        }

        List<ExamRecord> records = getExamRecords(examId);
        if (records.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return records.stream()
                .filter(r -> r.getScore() != null && r.getStatus() == 1)
                .map(ExamRecord::getScore)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}