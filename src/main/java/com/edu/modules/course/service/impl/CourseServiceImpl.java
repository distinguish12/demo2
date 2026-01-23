package com.edu.modules.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.course.entity.Course;
import com.edu.modules.course.mapper.CourseMapper;
import com.edu.modules.course.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 课程服务实现
 */
@Slf4j
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Override
    public Course createCourse(Course course) {
        // 参数校验
        if (course == null || !StringUtils.hasText(course.getTitle())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "课程标题不能为空");
        }
        if (course.getInstructorId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "讲师ID不能为空");
        }

        // 设置默认值
        if (course.getPrice() == null) {
            course.setPrice(java.math.BigDecimal.ZERO);
        }
        if (course.getLevel() == null) {
            course.setLevel(1);
        }
        if (course.getStatus() == null) {
            course.setStatus(0); // 默认草稿状态
        }
        if (course.getStudentCount() == null) {
            course.setStudentCount(0);
        }
        if (course.getRating() == null) {
            course.setRating(java.math.BigDecimal.ZERO);
        }
        if (course.getReviewCount() == null) {
            course.setReviewCount(0);
        }

        boolean success = save(course);
        if (!success) {
            throw new BusinessException("创建课程失败");
        }

        log.info("课程创建成功: title={}, id={}", course.getTitle(), course.getId());
        return course;
    }

    @Override
    public boolean updateCourse(Course course) {
        if (course == null || course.getId() == null) {
            return false;
        }

        boolean success = updateById(course);
        if (success) {
            log.info("课程更新成功: id={}", course.getId());
        }
        return success;
    }

    @Override
    public boolean deleteCourse(Long courseId) {
        if (courseId == null) {
            return false;
        }

        boolean success = removeById(courseId);
        if (success) {
            log.info("课程删除成功: id={}", courseId);
        }
        return success;
    }

    @Override
    public List<Course> getCoursesByInstructor(Long instructorId) {
        if (instructorId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getInstructorId, instructorId)
               .orderByDesc(Course::getCreateTime);

        return list(wrapper);
    }

    @Override
    public List<Course> getCoursesByCategory(Long categoryId) {
        if (categoryId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCategoryId, categoryId)
               .eq(Course::getStatus, 1) // 只查询发布状态的课程
               .orderByDesc(Course::getCreateTime);

        return list(wrapper);
    }

    @Override
    public boolean publishCourse(Long courseId) {
        if (courseId == null) {
            return false;
        }

        LambdaUpdateWrapper<Course> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Course::getId, courseId)
               .set(Course::getStatus, 1); // 发布状态

        boolean success = update(wrapper);
        if (success) {
            log.info("课程发布成功: id={}", courseId);
        }
        return success;
    }

    @Override
    public boolean unpublishCourse(Long courseId) {
        if (courseId == null) {
            return false;
        }

        LambdaUpdateWrapper<Course> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Course::getId, courseId)
               .set(Course::getStatus, 2); // 下架状态

        boolean success = update(wrapper);
        if (success) {
            log.info("课程下架成功: id={}", courseId);
        }
        return success;
    }

    @Override
    public List<Course> getHotCourses(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, 1) // 只查询发布状态的课程
               .orderByDesc(Course::getStudentCount) // 按学员数量排序
               .last("limit " + limit);

        return list(wrapper);
    }

    @Override
    public List<Course> searchCourses(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return List.of();
        }

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, 1) // 只查询发布状态的课程
               .like(Course::getTitle, keyword)
               .or()
               .like(Course::getDescription, keyword)
               .orderByDesc(Course::getCreateTime);

        return list(wrapper);
    }
}