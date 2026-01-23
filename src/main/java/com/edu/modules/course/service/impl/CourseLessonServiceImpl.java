package com.edu.modules.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.course.entity.CourseLesson;
import com.edu.modules.course.mapper.CourseLessonMapper;
import com.edu.modules.course.service.CourseLessonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 课程课时服务实现
 */
@Slf4j
@Service
public class CourseLessonServiceImpl extends ServiceImpl<CourseLessonMapper, CourseLesson>
        implements CourseLessonService {

    @Override
    public CourseLesson createLesson(CourseLesson lesson) {
        // 参数校验
        if (lesson == null || !StringUtils.hasText(lesson.getTitle())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "课时标题不能为空");
        }
        if (lesson.getChapterId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "章节ID不能为空");
        }

        // 设置默认值
        if (lesson.getSortOrder() == null) {
            lesson.setSortOrder(0);
        }
        if (lesson.getIsFree() == null) {
            lesson.setIsFree(0); // 默认收费
        }
        if (lesson.getStatus() == null) {
            lesson.setStatus(1); // 默认显示
        }
        if (lesson.getDuration() == null) {
            lesson.setDuration(0);
        }

        boolean success = save(lesson);
        if (!success) {
            throw new BusinessException("创建课时失败");
        }

        log.info("课时创建成功: title={}, chapterId={}", lesson.getTitle(), lesson.getChapterId());
        return lesson;
    }

    @Override
    public boolean updateLesson(CourseLesson lesson) {
        if (lesson == null || lesson.getId() == null) {
            return false;
        }

        boolean success = updateById(lesson);
        if (success) {
            log.info("课时更新成功: id={}", lesson.getId());
        }
        return success;
    }

    @Override
    public boolean deleteLesson(Long lessonId) {
        if (lessonId == null) {
            return false;
        }

        boolean success = removeById(lessonId);
        if (success) {
            log.info("课时删除成功: id={}", lessonId);
        }
        return success;
    }

    @Override
    public List<CourseLesson> getLessonsByChapterId(Long chapterId) {
        if (chapterId == null) {
            return List.of();
        }

        LambdaQueryWrapper<CourseLesson> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseLesson::getChapterId, chapterId)
               .orderByAsc(CourseLesson::getSortOrder)
               .orderByAsc(CourseLesson::getCreateTime);

        return list(wrapper);
    }

    @Override
    public List<CourseLesson> getLessonsByCourseId(Long courseId) {
        if (courseId == null) {
            return List.of();
        }

        // 这里需要关联查询，先通过章节表获取课时
        // 暂时返回空列表，后续完善
        return List.of();
    }

    @Override
    public boolean updateLessonSort(List<Long> lessonIds) {
        if (lessonIds == null || lessonIds.isEmpty()) {
            return false;
        }

        boolean success = true;
        for (int i = 0; i < lessonIds.size(); i++) {
            CourseLesson lesson = new CourseLesson();
            lesson.setId(lessonIds.get(i));
            lesson.setSortOrder(i);
            success = success && updateById(lesson);
        }

        if (success) {
            log.info("课时排序更新成功");
        }
        return success;
    }
}