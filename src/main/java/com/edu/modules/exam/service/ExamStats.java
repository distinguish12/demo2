package com.edu.modules.exam.service;

/**
 * 考试统计信息
 */
public class ExamStats {
    private int totalParticipants;    // 总参加人数
    private int completedCount;       // 已完成人数
    private double averageScore;      // 平均分
    private double passRate;          // 及格率
    private java.math.BigDecimal highestScore;  // 最高分
    private java.math.BigDecimal lowestScore;   // 最低分

    public ExamStats(int totalParticipants, int completedCount, double averageScore,
                    double passRate, java.math.BigDecimal highestScore, java.math.BigDecimal lowestScore) {
        this.totalParticipants = totalParticipants;
        this.completedCount = completedCount;
        this.averageScore = averageScore;
        this.passRate = passRate;
        this.highestScore = highestScore;
        this.lowestScore = lowestScore;
    }

    // Getters
    public int getTotalParticipants() { return totalParticipants; }
    public int getCompletedCount() { return completedCount; }
    public double getAverageScore() { return averageScore; }
    public double getPassRate() { return passRate; }
    public java.math.BigDecimal getHighestScore() { return highestScore; }
    public java.math.BigDecimal getLowestScore() { return lowestScore; }
}