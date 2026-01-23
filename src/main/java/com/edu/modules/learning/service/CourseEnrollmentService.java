package com.edu.modules.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.learning.entity.CourseEnrollment;

import java.util.List;

/**
 * 选课服务接口
 */
public interface CourseEnrollmentService extends IService<CourseEnrollment> {

    /**
     * 用户选课
     */
    CourseEnrollment enrollCourse(Long userId, Long courseId);

    /**
     * 用户退课
     */
    boolean unenrollCourse(Long userId, Long courseId);

    /**
     * 检查用户是否已选课
     */
    boolean isEnrolled(Long userId, Long courseId);

    /**
     * 获取用户的选课列表
     */
    List<CourseEnrollment> getUserEnrollments(Long userId);

    /**
     * 获取课程的选课学生列表
     */
    List<CourseEnrollment> getCourseEnrollments(Long courseId);

    /**
     * 获取选课统计
     */
    int getEnrollmentCount(Long courseId);
}