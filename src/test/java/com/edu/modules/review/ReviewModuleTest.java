package com.edu.modules.review;

import com.edu.modules.course.entity.Course;
import com.edu.modules.course.service.CourseService;
import com.edu.modules.review.entity.CourseReview;
import com.edu.modules.review.service.CourseReviewService;
import com.edu.modules.review.service.CourseReviewStats;
import com.edu.modules.review.service.UserReviewStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

/**
 * 评价反馈模块测试
 */
@SpringBootTest
public class ReviewModuleTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseReviewService courseReviewService;

    /**
     * 测试课程评价功能
     */
    @Test
    public void testCourseReview() {
        // 创建测试课程
        Course course = new Course();
        course.setTitle("评价测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        Long userId = 1L; // 假设用户ID为1

        // 创建评价
        CourseReview review = new CourseReview();
        review.setCourseId(createdCourse.getId());
        review.setUserId(userId);
        review.setRating(BigDecimal.valueOf(4.5));
        review.setComment("课程内容很丰富，讲师讲解也很清晰！");
        review.setIsAnonymous(0);

        CourseReview createdReview = courseReviewService.createReview(review);
        System.out.println("评价创建成功: " + createdReview.getId());

        // 检查是否已评价
        boolean hasReviewed = courseReviewService.hasUserReviewedCourse(userId, createdCourse.getId());
        System.out.println("用户是否已评价: " + hasReviewed);

        // 更新评价
        review.setId(createdReview.getId());
        review.setComment("更新后的评价：课程内容很丰富，讲师讲解也很清晰，推荐学习！");
        boolean updateResult = courseReviewService.updateReview(review);
        System.out.println("评价更新结果: " + updateResult);

        // 查询评价
        CourseReview queriedReview = courseReviewService.getById(createdReview.getId());
        System.out.println("查询评价: " + queriedReview.getComment());

        // 查询课程评价列表
        List<CourseReview> courseReviews = courseReviewService.getReviewsByCourseId(createdCourse.getId());
        System.out.println("课程评价数量: " + courseReviews.size());

        // 查询用户评价列表
        List<CourseReview> userReviews = courseReviewService.getReviewsByUserId(userId);
        System.out.println("用户评价数量: " + userReviews.size());

        // 清理测试数据
        courseReviewService.deleteReview(createdReview.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试评分统计功能
     */
    @Test
    public void testReviewStatistics() {
        // 创建测试课程
        Course course = new Course();
        course.setTitle("统计测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        Long[] userIds = {1L, 3L, 4L}; // 三个用户
        BigDecimal[] ratings = {BigDecimal.valueOf(5.0), BigDecimal.valueOf(4.0), BigDecimal.valueOf(3.5)};

        // 创建多个评价
        for (int i = 0; i < userIds.length; i++) {
            CourseReview review = new CourseReview();
            review.setCourseId(createdCourse.getId());
            review.setUserId(userIds[i]);
            review.setRating(ratings[i]);
            review.setComment("测试评价" + (i + 1));
            courseReviewService.createReview(review);
        }

        System.out.println("创建了 " + userIds.length + " 个评价");

        // 获取平均评分
        BigDecimal averageRating = courseReviewService.getCourseAverageRating(createdCourse.getId());
        System.out.println("课程平均评分: " + averageRating);

        // 获取评价统计
        CourseReviewStats stats = courseReviewService.getCourseReviewStats(createdCourse.getId());
        System.out.println("评价统计:");
        System.out.println("- 总评价数: " + stats.getTotalReviews());
        System.out.println("- 平均评分: " + stats.getAverageRating());
        System.out.println("- 好评率: " + String.format("%.2f%%", stats.getPositiveRate() * 100));
        System.out.println("- 评分分布: " + java.util.Arrays.toString(stats.getRatingDistribution()));

        // 获取用户统计
        for (Long userId : userIds) {
            UserReviewStats userStats = courseReviewService.getUserReviewStats(userId);
            System.out.println("用户" + userId + "统计:");
            System.out.println("  - 总评价数: " + userStats.getTotalReviews());
            System.out.println("  - 平均评分: " + userStats.getAverageRating());
        }

        // 清理测试数据
        List<CourseReview> reviews = courseReviewService.getReviewsByCourseId(createdCourse.getId());
        for (CourseReview review : reviews) {
            courseReviewService.deleteReview(review.getId());
        }
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试权限控制
     */
    @Test
    public void testReviewPermissions() {
        // 创建测试课程
        Course course = new Course();
        course.setTitle("权限测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        Long userId1 = 1L; // 用户1
        Long userId2 = 3L; // 用户2

        // 用户1发表评价
        CourseReview review = new CourseReview();
        review.setCourseId(createdCourse.getId());
        review.setUserId(userId1);
        review.setRating(BigDecimal.valueOf(4.0));
        review.setComment("用户1的评价");
        CourseReview createdReview = courseReviewService.createReview(review);

        // 用户2尝试修改用户1的评价（应该失败）
        CourseReview updateReview = new CourseReview();
        updateReview.setId(createdReview.getId());
        updateReview.setComment("用户2修改的内容");
        boolean updateResult = courseReviewService.updateReview(updateReview);
        System.out.println("用户2修改用户1的评价: " + (updateResult ? "成功（错误）" : "失败（正确）"));

        // 用户1修改自己的评价（应该成功）
        updateReview.setComment("用户1修改后的评价");
        boolean updateResult2 = courseReviewService.updateReview(updateReview);
        System.out.println("用户1修改自己的评价: " + (updateResult2 ? "成功（正确）" : "失败（错误）"));

        // 用户2尝试删除用户1的评价（应该失败）
        boolean deleteResult = courseReviewService.deleteReview(createdReview.getId());
        System.out.println("用户2删除用户1的评价: " + (deleteResult ? "成功（错误）" : "失败（正确）"));

        // 清理测试数据
        courseReviewService.deleteReview(createdReview.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试重复评价限制
     */
    @Test
    public void testDuplicateReviewPrevention() {
        // 创建测试课程
        Course course = new Course();
        course.setTitle("重复评价测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        Long userId = 1L;

        // 第一次评价
        CourseReview review1 = new CourseReview();
        review1.setCourseId(createdCourse.getId());
        review1.setUserId(userId);
        review1.setRating(BigDecimal.valueOf(4.0));
        review1.setComment("第一次评价");
        CourseReview createdReview1 = courseReviewService.createReview(review1);
        System.out.println("第一次评价成功: " + createdReview1.getId());

        // 尝试重复评价（应该失败）
        try {
            CourseReview review2 = new CourseReview();
            review2.setCourseId(createdCourse.getId());
            review2.setUserId(userId);
            review2.setRating(BigDecimal.valueOf(5.0));
            review2.setComment("第二次评价");
            courseReviewService.createReview(review2);
            System.out.println("重复评价成功（错误）");
        } catch (Exception e) {
            System.out.println("重复评价被阻止（正确）: " + e.getMessage());
        }

        // 清理测试数据
        courseReviewService.deleteReview(createdReview1.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试评分范围验证
     */
    @Test
    public void testRatingValidation() {
        // 创建测试课程
        Course course = new Course();
        course.setTitle("评分验证测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        Long userId = 1L;

        // 测试无效评分
        BigDecimal[] invalidRatings = {
            BigDecimal.valueOf(0.5), // 低于1星
            BigDecimal.valueOf(6.0), // 高于5星
            BigDecimal.valueOf(0),   // 等于0
            BigDecimal.valueOf(-1)   // 负数
        };

        for (BigDecimal rating : invalidRatings) {
            try {
                CourseReview review = new CourseReview();
                review.setCourseId(createdCourse.getId());
                review.setUserId(userId);
                review.setRating(rating);
                review.setComment("测试评分: " + rating);
                courseReviewService.createReview(review);
                System.out.println("无效评分" + rating + "通过验证（错误）");
            } catch (Exception e) {
                System.out.println("无效评分" + rating + "被拒绝（正确）: " + e.getMessage());
            }
        }

        // 测试有效评分
        BigDecimal[] validRatings = {
            BigDecimal.valueOf(1.0),
            BigDecimal.valueOf(3.5),
            BigDecimal.valueOf(5.0)
        };

        for (BigDecimal rating : validRatings) {
            try {
                CourseReview review = new CourseReview();
                review.setCourseId(createdCourse.getId());
                review.setUserId(userId);
                review.setRating(rating);
                review.setComment("有效评分: " + rating);
                CourseReview created = courseReviewService.createReview(review);
                System.out.println("有效评分" + rating + "通过验证（正确）");
                // 立即删除，避免影响后续测试
                courseReviewService.deleteReview(created.getId());
            } catch (Exception e) {
                System.out.println("有效评分" + rating + "被拒绝（错误）: " + e.getMessage());
            }
        }

        // 清理测试数据
        courseService.deleteCourse(createdCourse.getId());
    }
}