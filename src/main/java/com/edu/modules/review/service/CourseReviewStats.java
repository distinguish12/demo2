package com.edu.modules.review.service;

/**
 * 课程评价统计信息
 */
public class CourseReviewStats {
    private int totalReviews;      // 总评价数
    private java.math.BigDecimal averageRating;  // 平均评分
    private int[] ratingDistribution;  // 评分分布（1-5星）
    private double positiveRate;       // 好评率（4-5星）

    public CourseReviewStats(int totalReviews, java.math.BigDecimal averageRating, int[] ratingDistribution, double positiveRate) {
        this.totalReviews = totalReviews;
        this.averageRating = averageRating;
        this.ratingDistribution = ratingDistribution;
        this.positiveRate = positiveRate;
    }

    // Getters
    public int getTotalReviews() { return totalReviews; }
    public java.math.BigDecimal getAverageRating() { return averageRating; }
    public int[] getRatingDistribution() { return ratingDistribution; }
    public double getPositiveRate() { return positiveRate; }
}