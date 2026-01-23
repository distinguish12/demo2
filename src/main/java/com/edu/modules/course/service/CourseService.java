package com.edu.modules.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.course.entity.Course;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<Course> {

    /**
     * 创建课程
     */
    Course createCourse(Course course);

    /**
     * 更新课程
     */
    boolean updateCourse(Course course);

    /**
     * 删除课程
     */
    boolean deleteCourse(Long courseId);

    /**
     * 根据讲师ID获取课程列表
     */
    List<Course> getCoursesByInstructor(Long instructorId);

    /**
     * 根据分类获取课程列表
     */
    List<Course> getCoursesByCategory(Long categoryId);

    /**
     * 发布课程
     */
    boolean publishCourse(Long courseId);

    /**
     * 下架课程
     */
    boolean unpublishCourse(Long courseId);

    /**
     * 获取热门课程
     */
    List<Course> getHotCourses(Integer limit);

    /**
     * 搜索课程
     */
    List<Course> searchCourses(String keyword);
}