package com.edu.modules.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.learning.entity.LearningProgress;

import java.util.List;

/**
 * 学习进度服务接口
 */
public interface LearningProgressService extends IService<LearningProgress> {

    /**
     * 更新学习进度
     */
    LearningProgress updateProgress(Long userId, Long lessonId, Integer progress, Integer lastPosition);

    /**
     * 完成课时学习
     */
    boolean completeLesson(Long userId, Long lessonId);

    /**
     * 获取用户课时学习进度
     */
    LearningProgress getUserLessonProgress(Long userId, Long lessonId);

    /**
     * 获取用户课程学习进度
     */
    List<LearningProgress> getUserCourseProgress(Long userId, Long courseId);

    /**
     * 获取用户学习统计
     */
    LearningProgressStats getUserLearningStats(Long userId);

    /**
     * 学习进度统计
     */
    public static class LearningProgressStats {
        private int totalLessons;      // 总课时数
        private int completedLessons;  // 已完成课时数
        private int totalWatchTime;    // 总观看时长（秒）

        public LearningProgressStats(int totalLessons, int completedLessons, int totalWatchTime) {
            this.totalLessons = totalLessons;
            this.completedLessons = completedLessons;
            this.totalWatchTime = totalWatchTime;
        }

        // Getters
        public int getTotalLessons() { return totalLessons; }
        public int getCompletedLessons() { return completedLessons; }
        public int getTotalWatchTime() { return totalWatchTime; }

        // 计算完成率
        public double getCompletionRate() {
            return totalLessons > 0 ? (double) completedLessons / totalLessons : 0.0;
        }
    }
}