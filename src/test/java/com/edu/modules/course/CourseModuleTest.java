package com.edu.modules.course;

import com.edu.modules.course.entity.Course;
import com.edu.modules.course.entity.CourseCategory;
import com.edu.modules.course.entity.CourseChapter;
import com.edu.modules.course.entity.CourseLesson;
import com.edu.modules.course.service.CourseService;
import com.edu.modules.course.service.CourseChapterService;
import com.edu.modules.course.service.CourseLessonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课程管理模块测试
 */
@SpringBootTest
public class CourseModuleTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseChapterService courseChapterService;

    @Autowired
    private CourseLessonService courseLessonService;

    /**
     * 测试课程创建、更新、删除
     */
    @Test
    public void testCourseCRUD() {
        // 创建课程
        Course course = new Course();
        course.setTitle("Java编程基础");
        course.setDescription("Java编程入门课程");
        course.setInstructorId(2L); // 假设讲师ID为2
        course.setPrice(BigDecimal.valueOf(99.00));
        course.setLevel(1);

        Course createdCourse = courseService.createCourse(course);
        System.out.println("创建课程成功: " + createdCourse.getId());

        // 更新课程
        course.setId(createdCourse.getId());
        course.setDescription("Java编程入门课程 - 修订版");
        boolean updateResult = courseService.updateCourse(course);
        System.out.println("更新课程结果: " + updateResult);

        // 查询课程
        Course queriedCourse = courseService.getById(createdCourse.getId());
        System.out.println("查询课程: " + queriedCourse.getTitle());

        // 删除课程
        boolean deleteResult = courseService.deleteCourse(createdCourse.getId());
        System.out.println("删除课程结果: " + deleteResult);
    }

    /**
     * 测试章节管理
     */
    @Test
    public void testChapterManagement() {
        // 先创建课程
        Course course = new Course();
        course.setTitle("测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        // 创建章节
        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(createdCourse.getId());
        chapter.setTitle("第一章：基础概念");
        chapter.setDescription("学习基础概念");

        CourseChapter createdChapter = courseChapterService.createChapter(chapter);
        System.out.println("创建章节成功: " + createdChapter.getId());

        // 查询章节列表
        List<CourseChapter> chapters = courseChapterService.getChaptersByCourseId(createdCourse.getId());
        System.out.println("课程章节数量: " + chapters.size());

        // 清理测试数据
        courseChapterService.deleteChapter(createdChapter.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试课时管理
     */
    @Test
    public void testLessonManagement() {
        // 先创建课程和章节
        Course course = new Course();
        course.setTitle("测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(createdCourse.getId());
        chapter.setTitle("测试章节");
        CourseChapter createdChapter = courseChapterService.createChapter(chapter);

        // 创建课时
        CourseLesson lesson = new CourseLesson();
        lesson.setChapterId(createdChapter.getId());
        lesson.setTitle("第一节：介绍");
        lesson.setDescription("课程介绍");
        lesson.setVideoUrl("http://example.com/video1.mp4");
        lesson.setDuration(600); // 10分钟
        lesson.setIsFree(1); // 免费

        CourseLesson createdLesson = courseLessonService.createLesson(lesson);
        System.out.println("创建课时成功: " + createdLesson.getId());

        // 查询课时列表
        List<CourseLesson> lessons = courseLessonService.getLessonsByChapterId(createdChapter.getId());
        System.out.println("章节课时数量: " + lessons.size());

        // 清理测试数据
        courseLessonService.deleteLesson(createdLesson.getId());
        courseChapterService.deleteChapter(createdChapter.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试搜索功能
     */
    @Test
    public void testCourseSearch() {
        // 搜索课程
        List<Course> courses = courseService.searchCourses("Java");
        System.out.println("搜索到Java相关课程: " + courses.size());

        // 获取热门课程
        List<Course> hotCourses = courseService.getHotCourses(5);
        System.out.println("热门课程数量: " + hotCourses.size());
    }
}