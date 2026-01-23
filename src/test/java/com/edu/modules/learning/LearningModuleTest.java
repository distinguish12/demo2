package com.edu.modules.learning;

import com.edu.modules.course.entity.Course;
import com.edu.modules.course.entity.CourseChapter;
import com.edu.modules.course.entity.CourseLesson;
import com.edu.modules.course.service.CourseService;
import com.edu.modules.course.service.CourseChapterService;
import com.edu.modules.course.service.CourseLessonService;
import com.edu.modules.learning.entity.CourseEnrollment;
import com.edu.modules.learning.entity.LearningProgress;
import com.edu.modules.learning.service.CourseEnrollmentService;
import com.edu.modules.learning.service.LearningProgressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

/**
 * 学习模块测试
 */
@SpringBootTest
public class LearningModuleTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseChapterService courseChapterService;

    @Autowired
    private CourseLessonService courseLessonService;

    @Autowired
    private CourseEnrollmentService courseEnrollmentService;

    @Autowired
    private LearningProgressService learningProgressService;

    /**
     * 测试选课功能
     */
    @Test
    public void testCourseEnrollment() {
        // 创建测试课程
        Course course = new Course();
        course.setTitle("测试课程");
        course.setDescription("用于测试的课程");
        course.setInstructorId(2L); // 假设讲师ID为2
        course.setPrice(BigDecimal.ZERO);
        course.setLevel(1);
        Course createdCourse = courseService.createCourse(course);

        // 测试选课
        Long userId = 1L; // 假设学生ID为1
        CourseEnrollment enrollment = courseEnrollmentService.enrollCourse(userId, createdCourse.getId());
        System.out.println("选课成功: " + enrollment.getId());

        // 检查选课状态
        boolean isEnrolled = courseEnrollmentService.isEnrolled(userId, createdCourse.getId());
        System.out.println("选课状态: " + isEnrolled);

        // 获取用户选课列表
        List<CourseEnrollment> enrollments = courseEnrollmentService.getUserEnrollments(userId);
        System.out.println("用户选课数量: " + enrollments.size());

        // 清理测试数据
        courseEnrollmentService.unenrollCourse(userId, createdCourse.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试学习进度功能
     */
    @Test
    public void testLearningProgress() {
        // 创建测试数据
        Course course = new Course();
        course.setTitle("进度测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(createdCourse.getId());
        chapter.setTitle("测试章节");
        CourseChapter createdChapter = courseChapterService.createChapter(chapter);

        CourseLesson lesson = new CourseLesson();
        lesson.setChapterId(createdChapter.getId());
        lesson.setTitle("测试课时");
        lesson.setVideoUrl("http://example.com/test.mp4");
        lesson.setDuration(600);
        CourseLesson createdLesson = courseLessonService.createLesson(lesson);

        Long userId = 1L;

        // 测试更新进度
        LearningProgress progress = learningProgressService.updateProgress(userId, createdLesson.getId(), 50, 300);
        System.out.println("进度更新成功: " + progress.getProgress() + "%");

        // 获取进度
        LearningProgress currentProgress = learningProgressService.getUserLessonProgress(userId, createdLesson.getId());
        System.out.println("当前进度: " + currentProgress.getProgress() + "%");

        // 完成课时
        boolean completed = learningProgressService.completeLesson(userId, createdLesson.getId());
        System.out.println("课时完成: " + completed);

        // 获取学习统计
        LearningProgressService.LearningProgressStats stats = learningProgressService.getUserLearningStats(userId);
        System.out.println("学习统计 - 总课时: " + stats.getTotalLessons() +
                          ", 已完成: " + stats.getCompletedLessons() +
                          ", 完成率: " + String.format("%.2f", stats.getCompletionRate() * 100) + "%");

        // 清理测试数据
        courseLessonService.deleteLesson(createdLesson.getId());
        courseChapterService.deleteChapter(createdChapter.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试完整的学习流程
     */
    @Test
    public void testCompleteLearningFlow() {
        // 1. 创建课程
        Course course = new Course();
        course.setTitle("完整学习流程测试");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        // 2. 创建章节
        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(createdCourse.getId());
        chapter.setTitle("第一章：基础知识");
        CourseChapter createdChapter = courseChapterService.createChapter(chapter);

        // 3. 创建课时
        CourseLesson lesson1 = new CourseLesson();
        lesson1.setChapterId(createdChapter.getId());
        lesson1.setTitle("课时1：入门介绍");
        lesson1.setVideoUrl("http://example.com/lesson1.mp4");
        lesson1.setDuration(300);
        CourseLesson createdLesson1 = courseLessonService.createLesson(lesson1);

        CourseLesson lesson2 = new CourseLesson();
        lesson2.setChapterId(createdChapter.getId());
        lesson2.setTitle("课时2：核心概念");
        lesson2.setVideoUrl("http://example.com/lesson2.mp4");
        lesson2.setDuration(600);
        CourseLesson createdLesson2 = courseLessonService.createLesson(lesson2);

        Long userId = 1L;

        // 4. 选课
        courseEnrollmentService.enrollCourse(userId, createdCourse.getId());
        System.out.println("选课成功");

        // 5. 学习过程
        // 观看第一个课时到50%
        learningProgressService.updateProgress(userId, createdLesson1.getId(), 50, 150);
        System.out.println("课时1学习到50%");

        // 完成第一个课时
        learningProgressService.completeLesson(userId, createdLesson1.getId());
        System.out.println("课时1完成");

        // 观看第二个课时到80%
        learningProgressService.updateProgress(userId, createdLesson2.getId(), 80, 480);
        System.out.println("课时2学习到80%");

        // 6. 查看学习统计
        LearningProgressService.LearningProgressStats stats = learningProgressService.getUserLearningStats(userId);
        System.out.println("学习统计:");
        System.out.println("- 总课时数: " + stats.getTotalLessons());
        System.out.println("- 已完成课时: " + stats.getCompletedLessons());
        System.out.println("- 总观看时长: " + stats.getTotalWatchTime() + "秒");
        System.out.println("- 完成率: " + String.format("%.1f%%", stats.getCompletionRate() * 100));

        // 7. 清理测试数据
        courseLessonService.deleteLesson(createdLesson1.getId());
        courseLessonService.deleteLesson(createdLesson2.getId());
        courseChapterService.deleteChapter(createdChapter.getId());
        courseEnrollmentService.unenrollCourse(userId, createdCourse.getId());
        courseService.deleteCourse(createdCourse.getId());

        System.out.println("测试完成，数据清理完毕");
    }
}