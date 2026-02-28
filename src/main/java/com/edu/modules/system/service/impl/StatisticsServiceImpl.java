package com.edu.modules.system.service.impl;

import com.edu.modules.system.service.StatisticsService;
import com.edu.modules.user.mapper.UserMapper;
import com.edu.modules.course.mapper.CourseMapper;
import com.edu.modules.learning.mapper.CourseEnrollmentMapper;
import com.edu.modules.course.mapper.CourseLessonMapper;
import com.edu.modules.learning.mapper.LearningProgressMapper;
import com.edu.modules.system.mapper.OperationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据统计服务实现
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseEnrollmentMapper courseEnrollmentMapper;

    @Autowired
    private CourseLessonMapper courseLessonMapper;

    @Autowired
    private LearningProgressMapper learningProgressMapper;

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public SystemOverviewStats getSystemOverview() {
        long totalUsers = userMapper.selectCount(null);
        long totalCourses = courseMapper.selectCount(null);
        long totalEnrollments = courseEnrollmentMapper.selectCount(null);
        long totalViews = operationLogMapper.selectCount(null);
        return new SystemOverviewStats(totalUsers, totalCourses, totalEnrollments, totalViews);
    }

    @Override
    public UserStats getUserStats() {
        long totalUsers = userMapper.selectCount(null);
        // 简单实现：活跃用户 = 总用户数，新增用户暂时返回0
        return new UserStats(totalUsers, totalUsers, 0, 0, 0);
    }

    @Override
    public CourseStats getCourseStats() {
        long totalCourses = courseMapper.selectCount(null);
        long totalEnrollments = courseEnrollmentMapper.selectCount(null);
        long totalLessons = courseLessonMapper.selectCount(null);
        return new CourseStats(totalCourses, totalCourses, totalEnrollments, totalLessons, 0.0);
    }

    @Override
    public LearningStats getLearningStats() {
        long totalRecords = learningProgressMapper.selectCount(null);
        return new LearningStats(totalRecords, 0, 0.0, 0, 0);
    }

    @Override
    public AccessStats getAccessStats() {
        long totalVisits = operationLogMapper.selectCount(null);
        return new AccessStats(totalVisits, 0, 0, 0, 0);
    }
}
