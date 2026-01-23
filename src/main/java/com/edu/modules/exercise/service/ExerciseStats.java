package com.edu.modules.exercise.service;

/**
 * 练习统计信息
 */
public class ExerciseStats {
    private int totalSubmissions;    // 总提交数
    private int correctSubmissions;  // 正确提交数
    private double averageScore;     // 平均分
    private double correctRate;      // 正确率

    public ExerciseStats(int totalSubmissions, int correctSubmissions, double averageScore, double correctRate) {
        this.totalSubmissions = totalSubmissions;
        this.correctSubmissions = correctSubmissions;
        this.averageScore = averageScore;
        this.correctRate = correctRate;
    }

    // Getters
    public int getTotalSubmissions() { return totalSubmissions; }
    public int getCorrectSubmissions() { return correctSubmissions; }
    public double getAverageScore() { return averageScore; }
    public double getCorrectRate() { return correctRate; }
}