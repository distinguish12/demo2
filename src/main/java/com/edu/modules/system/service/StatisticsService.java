package com.edu.modules.system.service;

/**
 * 数据统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取系统概览统计
     */
    SystemOverviewStats getSystemOverview();

    /**
     * 获取用户统计
     */
    UserStats getUserStats();

    /**
     * 获取课程统计
     */
    CourseStats getCourseStats();

    /**
     * 获取学习统计
     */
    LearningStats getLearningStats();

    /**
     * 获取访问统计
     */
    AccessStats getAccessStats();

    // ========== 统计数据类 ==========

    /**
     * 系统概览统计
     */
    class SystemOverviewStats {
        private long totalUsers;        // 总用户数
        private long totalCourses;      // 总课程数
        private long totalEnrollments;  // 总选课数
        private long totalViews;        // 总访问量

        public SystemOverviewStats(long totalUsers, long totalCourses, long totalEnrollments, long totalViews) {
            this.totalUsers = totalUsers;
            this.totalCourses = totalCourses;
            this.totalEnrollments = totalEnrollments;
            this.totalViews = totalViews;
        }

        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getTotalCourses() { return totalCourses; }
        public long getTotalEnrollments() { return totalEnrollments; }
        public long getTotalViews() { return totalViews; }
    }

    /**
     * 用户统计
     */
    class UserStats {
        private long totalUsers;        // 总用户数
        private long activeUsers;       // 活跃用户数
        private long newUsersToday;     // 今日新增用户
        private long newUsersThisWeek;  // 本周新增用户
        private long newUsersThisMonth; // 本月新增用户

        public UserStats(long totalUsers, long activeUsers, long newUsersToday,
                        long newUsersThisWeek, long newUsersThisMonth) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.newUsersToday = newUsersToday;
            this.newUsersThisWeek = newUsersThisWeek;
            this.newUsersThisMonth = newUsersThisMonth;
        }

        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getNewUsersToday() { return newUsersToday; }
        public long getNewUsersThisWeek() { return newUsersThisWeek; }
        public long getNewUsersThisMonth() { return newUsersThisMonth; }
    }

    /**
     * 课程统计
     */
    class CourseStats {
        private long totalCourses;      // 总课程数
        private long publishedCourses;  // 已发布课程数
        private long totalEnrollments;  // 总选课数
        private long totalLessons;      // 总课时数
        private double averageRating;   // 平均评分

        public CourseStats(long totalCourses, long publishedCourses, long totalEnrollments,
                          long totalLessons, double averageRating) {
            this.totalCourses = totalCourses;
            this.publishedCourses = publishedCourses;
            this.totalEnrollments = totalEnrollments;
            this.totalLessons = totalLessons;
            this.averageRating = averageRating;
        }

        // Getters
        public long getTotalCourses() { return totalCourses; }
        public long getPublishedCourses() { return publishedCourses; }
        public long getTotalEnrollments() { return totalEnrollments; }
        public long getTotalLessons() { return totalLessons; }
        public double getAverageRating() { return averageRating; }
    }

    /**
     * 学习统计
     */
    class LearningStats {
        private long totalLearningRecords;  // 总学习记录数
        private long completedLessons;      // 已完成课时数
        private double completionRate;      // 完成率
        private long totalWatchTime;        // 总观看时长（秒）
        private long averageWatchTime;      // 平均观看时长（秒）

        public LearningStats(long totalLearningRecords, long completedLessons, double completionRate,
                           long totalWatchTime, long averageWatchTime) {
            this.totalLearningRecords = totalLearningRecords;
            this.completedLessons = completedLessons;
            this.completionRate = completionRate;
            this.totalWatchTime = totalWatchTime;
            this.averageWatchTime = averageWatchTime;
        }

        // Getters
        public long getTotalLearningRecords() { return totalLearningRecords; }
        public long getCompletedLessons() { return completedLessons; }
        public double getCompletionRate() { return completionRate; }
        public long getTotalWatchTime() { return totalWatchTime; }
        public long getAverageWatchTime() { return averageWatchTime; }
    }

    /**
     * 访问统计
     */
    class AccessStats {
        private long totalVisits;       // 总访问量
        private long todayVisits;       // 今日访问量
        private long weekVisits;        // 本周访问量
        private long monthVisits;       // 本月访问量
        private long totalPageViews;    // 总页面浏览量

        public AccessStats(long totalVisits, long todayVisits, long weekVisits,
                          long monthVisits, long totalPageViews) {
            this.totalVisits = totalVisits;
            this.todayVisits = todayVisits;
            this.weekVisits = weekVisits;
            this.monthVisits = monthVisits;
            this.totalPageViews = totalPageViews;
        }

        // Getters
        public long getTotalVisits() { return totalVisits; }
        public long getTodayVisits() { return todayVisits; }
        public long getWeekVisits() { return weekVisits; }
        public long getMonthVisits() { return monthVisits; }
        public long getTotalPageViews() { return totalPageViews; }
    }
}