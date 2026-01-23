package com.edu.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.learning.entity.LearningProgress;
import com.edu.modules.learning.mapper.LearningProgressMapper;
import com.edu.modules.learning.service.LearningProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习进度服务实现
 */
@Slf4j
@Service
public class LearningProgressServiceImpl extends ServiceImpl<LearningProgressMapper, LearningProgress>
        implements LearningProgressService {

    @Override
    public LearningProgress updateProgress(Long userId, Long lessonId, Integer progress, Integer lastPosition) {
        // 参数校验
        if (userId == null || lessonId == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户ID和课时ID不能为空");
        }

        if (progress != null && (progress < 0 || progress > 100)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "进度值必须在0-100之间");
        }

        // 获取或创建学习进度记录
        LearningProgress progressRecord = getUserLessonProgress(userId, lessonId);
        if (progressRecord == null) {
            progressRecord = new LearningProgress();
            progressRecord.setUserId(userId);
            progressRecord.setLessonId(lessonId);
            progressRecord.setProgress(progress != null ? progress : 0);
            progressRecord.setLastPosition(lastPosition != null ? lastPosition : 0);
            progressRecord.setDuration(0);
            progressRecord.setCompleted(0);
            progressRecord.setLastAccessTime(LocalDateTime.now());

            boolean success = save(progressRecord);
            if (!success) {
                throw new BusinessException("创建学习进度记录失败");
            }
        } else {
            // 更新现有记录
            LambdaUpdateWrapper<LearningProgress> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(LearningProgress::getId, progressRecord.getId());

            if (progress != null) {
                wrapper.set(LearningProgress::getProgress, progress);
                // 如果进度达到100%，标记为完成
                if (progress >= 100) {
                    wrapper.set(LearningProgress::getCompleted, 1);
                }
            }
            if (lastPosition != null) {
                wrapper.set(LearningProgress::getLastPosition, lastPosition);
            }
            wrapper.set(LearningProgress::getLastAccessTime, LocalDateTime.now());

            update(wrapper);
        }

        log.info("学习进度更新成功: userId={}, lessonId={}, progress={}", userId, lessonId, progress);
        return getUserLessonProgress(userId, lessonId);
    }

    @Override
    public boolean completeLesson(Long userId, Long lessonId) {
        if (userId == null || lessonId == null) {
            return false;
        }

        LambdaUpdateWrapper<LearningProgress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(LearningProgress::getUserId, userId)
               .eq(LearningProgress::getLessonId, lessonId)
               .set(LearningProgress::getProgress, 100)
               .set(LearningProgress::getCompleted, 1)
               .set(LearningProgress::getLastAccessTime, LocalDateTime.now());

        boolean success = update(wrapper);
        if (success) {
            log.info("课时完成标记成功: userId={}, lessonId={}", userId, lessonId);
        }
        return success;
    }

    @Override
    public LearningProgress getUserLessonProgress(Long userId, Long lessonId) {
        if (userId == null || lessonId == null) {
            return null;
        }

        LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningProgress::getUserId, userId)
               .eq(LearningProgress::getLessonId, lessonId);

        return getOne(wrapper);
    }

    @Override
    public List<LearningProgress> getUserCourseProgress(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            return List.of();
        }

        // 这里需要通过关联查询获取课程下的所有课时进度
        // 暂时返回用户的全部学习进度，后续优化
        LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningProgress::getUserId, userId)
               .orderByDesc(LearningProgress::getLastAccessTime);

        return list(wrapper);
    }

    @Override
    public LearningProgressStats getUserLearningStats(Long userId) {
        if (userId == null) {
            return new LearningProgressStats(0, 0, 0);
        }

        LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningProgress::getUserId, userId);

        List<LearningProgress> progresses = list(wrapper);

        int totalLessons = progresses.size();
        int completedLessons = (int) progresses.stream()
                .filter(p -> p.getCompleted() != null && p.getCompleted() == 1)
                .count();
        int totalWatchTime = progresses.stream()
                .mapToInt(p -> p.getDuration() != null ? p.getDuration() : 0)
                .sum();

        return new LearningProgressStats(totalLessons, completedLessons, totalWatchTime);
    }
}