package com.edu.modules.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.exam.entity.Exam;
import com.edu.modules.exam.entity.ExamQuestion;
import com.edu.modules.exam.entity.ExamRecord;
import com.edu.modules.exam.mapper.ExamMapper;
import com.edu.modules.exam.service.ExamQuestionService;
import com.edu.modules.exam.service.ExamRecordService;
import com.edu.modules.exam.service.ExamService;
import com.edu.modules.exam.service.ExamStats;
import com.edu.modules.exam.service.ExamStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 考试服务实现
 */
@Slf4j
@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    @Autowired
    private ExamQuestionService examQuestionService;

    @Autowired
    private ExamRecordService examRecordService;

    @Override
    public Exam createExam(Exam exam) {
        // 参数校验
        if (exam == null || exam.getCourseId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "课程ID不能为空");
        }
        if (exam.getTitle() == null || exam.getTitle().trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "考试标题不能为空");
        }

        // 设置默认值
        if (exam.getDuration() == null) {
            exam.setDuration(60); // 默认60分钟
        }
        if (exam.getTotalScore() == null) {
            exam.setTotalScore(BigDecimal.valueOf(100)); // 默认100分
        }
        if (exam.getPassScore() == null) {
            exam.setPassScore(BigDecimal.valueOf(60)); // 默认60分及格
        }
        if (exam.getQuestionCount() == null) {
            exam.setQuestionCount(0);
        }
        if (exam.getStatus() == null) {
            exam.setStatus(0); // 默认未开始
        }

        // 检查时间设置
        if (exam.getStartTime() != null && exam.getEndTime() != null) {
            if (exam.getStartTime().isAfter(exam.getEndTime())) {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "开始时间不能晚于结束时间");
            }
        }

        boolean success = save(exam);
        if (!success) {
            throw new BusinessException("创建考试失败");
        }

        log.info("考试创建成功: title={}, courseId={}", exam.getTitle(), exam.getCourseId());
        return exam;
    }

    @Override
    public boolean updateExam(Exam exam) {
        if (exam == null || exam.getId() == null) {
            return false;
        }

        // 检查考试是否已开始
        Exam existingExam = getById(exam.getId());
        if (existingExam != null && existingExam.getStatus() != 0) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "考试已开始或已结束，不能修改");
        }

        boolean success = updateById(exam);
        if (success) {
            log.info("考试更新成功: id={}", exam.getId());
        }
        return success;
    }

    @Override
    public boolean deleteExam(Long examId) {
        if (examId == null) {
            return false;
        }

        // 删除考试相关的题目和记录
        examQuestionService.removeQuestionsByExamId(examId);

        boolean success = removeById(examId);
        if (success) {
            log.info("考试删除成功: id={}", examId);
        }
        return success;
    }

    @Override
    public List<Exam> getExamsByCourseId(Long courseId) {
        if (courseId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getCourseId, courseId)
               .orderByDesc(Exam::getCreateTime);

        return list(wrapper);
    }

    @Override
    public ExamRecord startExam(Long userId, Long examId) {
        // 参数校验
        if (userId == null || examId == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户ID和考试ID不能为空");
        }

        // 检查考试是否存在
        Exam exam = getById(examId);
        if (exam == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "考试不存在");
        }

        // 检查考试状态
        ExamStatus status = checkExamStatus(exam);
        if (status == ExamStatus.NOT_STARTED) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "考试还未开始");
        }
        if (status == ExamStatus.ENDED || status == ExamStatus.TIMEOUT) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "考试已结束");
        }

        // 检查是否已参加过考试
        if (examRecordService.hasUserTakenExam(userId, examId)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "您已参加过此考试");
        }

        // 创建考试记录
        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setExamId(examId);
        record.setStartTime(LocalDateTime.now());
        record.setStatus(0); // 未完成
        record.setTotalScore(exam.getTotalScore());

        examRecordService.save(record);

        log.info("用户开始考试: userId={}, examId={}", userId, examId);
        return record;
    }

    @Override
    public ExamRecord submitExam(Long userId, Long examId, Map<Long, String> answers) {
        // 参数校验
        if (userId == null || examId == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户ID和考试ID不能为空");
        }

        // 获取考试记录
        ExamRecord record = examRecordService.getUserExamRecord(userId, examId);
        if (record == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "未找到考试记录，请先开始考试");
        }

        if (record.getStatus() == 1) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "考试已提交，不能重复提交");
        }

        // 获取考试题目
        List<ExamQuestion> questions = examQuestionService.getQuestionsByExamId(examId);

        // 自动评分
        BigDecimal score = autoGrade(answers, questions);

        // 更新考试记录
        record.setScore(score);
        record.setEndTime(LocalDateTime.now());
        record.setSubmitTime(LocalDateTime.now());
        record.setStatus(1); // 已完成
        record.setAnswers(answers.toString()); // 存储答案（简化处理）

        examRecordService.updateById(record);

        log.info("用户提交考试: userId={}, examId={}, score={}", userId, examId, score);
        return record;
    }

    @Override
    public BigDecimal autoGrade(Map<Long, String> userAnswers, List<ExamQuestion> questions) {
        if (userAnswers == null || questions == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalScore = BigDecimal.ZERO;

        for (ExamQuestion question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            if (userAnswer != null && !userAnswer.trim().isEmpty()) {
                // 简单评分逻辑（可根据题型扩展）
                if (userAnswer.trim().equalsIgnoreCase(question.getAnswer().trim())) {
                    totalScore = totalScore.add(question.getScore());
                }
            }
        }

        return totalScore;
    }

    @Override
    public ExamStatus checkExamStatus(Exam exam) {
        if (exam == null) {
            return ExamStatus.ENDED;
        }

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
            return ExamStatus.NOT_STARTED;
        }

        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            return ExamStatus.ENDED;
        }

        return ExamStatus.IN_PROGRESS;
    }

    @Override
    public ExamStats getExamStats(Long examId) {
        if (examId == null) {
            return new ExamStats(0, 0, 0.0, 0.0, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        List<ExamRecord> records = examRecordService.getExamRecords(examId);

        int totalParticipants = records.size();
        int completedCount = (int) records.stream().filter(r -> r.getStatus() == 1).count();

        if (completedCount == 0) {
            return new ExamStats(0, 0, 0.0, 0.0, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        // 计算平均分
        double averageScore = records.stream()
                .filter(r -> r.getScore() != null)
                .mapToDouble(r -> r.getScore().doubleValue())
                .average()
                .orElse(0.0);

        // 计算及格率
        Exam exam = getById(examId);
        BigDecimal passScore = exam != null && exam.getPassScore() != null ? exam.getPassScore() : BigDecimal.valueOf(60);

        long passedCount = records.stream()
                .filter(r -> r.getScore() != null && r.getScore().compareTo(passScore) >= 0)
                .count();

        double passRate = (double) passedCount / completedCount;

        // 最高分和最低分
        BigDecimal highestScore = records.stream()
                .filter(r -> r.getScore() != null)
                .map(ExamRecord::getScore)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal lowestScore = records.stream()
                .filter(r -> r.getScore() != null)
                .map(ExamRecord::getScore)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return new ExamStats(totalParticipants, completedCount, averageScore, passRate, highestScore, lowestScore);
    }
}