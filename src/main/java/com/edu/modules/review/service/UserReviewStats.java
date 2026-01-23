package com.edu.modules.review.service;

/**
 * 用户评价统计信息
 */
public class UserReviewStats {
    private int totalReviews;      // 总评价数
    private java.math.BigDecimal averageRating;  // 平均评分
    private int helpfulReviews;    // 有帮助的评价数

    public UserReviewStats(int totalReviews, java.math.BigDecimal averageRating, int helpfulReviews) {
        this.totalReviews = totalReviews;
        this.averageRating = averageRating;
        this.helpfulReviews = helpfulReviews;
    }

    // Getters
    public int getTotalReviews() { return totalReviews; }
    public java.math.BigDecimal getAverageRating() { return averageRating; }
    public int getHelpfulReviews() { return helpfulReviews; }
}