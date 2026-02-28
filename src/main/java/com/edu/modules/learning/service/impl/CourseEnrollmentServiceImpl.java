package com.edu.modules.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.learning.entity.CourseEnrollment;
import com.edu.modules.learning.mapper.CourseEnrollmentMapper;
import com.edu.modules.learning.service.CourseEnrollmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 选课服务实现
 */
@Slf4j
@Service
public class CourseEnrollmentServiceImpl extends ServiceImpl<CourseEnrollmentMapper, CourseEnrollment>
        implements CourseEnrollmentService {

    @Override
    public CourseEnrollment enrollCourse(Long userId, Long courseId) {
        // 参数校验
        if (userId == null || courseId == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户ID和课程ID不能为空");
        }

        // 检查是否已选课
        if (isEnrolled(userId, courseId)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "您已选修该课程");
        }

        // 创建选课记录
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);
        enrollment.setEnrollTime(LocalDateTime.now());
        enrollment.setStatus(1); // 正常状态

        boolean success = save(enrollment);
        if (!success) {
            throw new BusinessException("选课失败");
        }

        log.info("用户选课成功: userId={}, courseId={}", userId, courseId);
        return enrollment;
    }

    @Override
    public boolean unenrollCourse(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            return false;
        }

        LambdaUpdateWrapper<CourseEnrollment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CourseEnrollment::getUserId, userId)
                .eq(CourseEnrollment::getCourseId, courseId)
                .set(CourseEnrollment::getStatus, 0); // 取消状态

        boolean success = update(wrapper);
        if (success) {
            log.info("用户退课成功: userId={}, courseId={}", userId, courseId);
        }
        return success;
    }

    @Override
    public boolean isEnrolled(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            return false;
        }

        LambdaQueryWrapper<CourseEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseEnrollment::getUserId, userId)
                .eq(CourseEnrollment::getCourseId, courseId)
                .eq(CourseEnrollment::getStatus, 1); // 正常状态

        return count(wrapper) > 0;
    }

    @Override
    public List<CourseEnrollment> getUserEnrollments(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<CourseEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseEnrollment::getUserId, userId)
                .eq(CourseEnrollment::getStatus, 1) // 正常状态
                .orderByDesc(CourseEnrollment::getEnrollTime);

        return list(wrapper);
    }

    @Override
    public List<CourseEnrollment> getCourseEnrollments(Long courseId) {
        if (courseId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<CourseEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseEnrollment::getCourseId, courseId)
                .eq(CourseEnrollment::getStatus, 1) // 正常状态
                .orderByDesc(CourseEnrollment::getEnrollTime);

        return list(wrapper);
    }

    @Override
    public int getEnrollmentCount(Long courseId) {
        if (courseId == null) {
            return 0;
        }

        LambdaQueryWrapper<CourseEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseEnrollment::getCourseId, courseId)
                .eq(CourseEnrollment::getStatus, 1); // 正常状态

        return (int) count(wrapper);
    }
}