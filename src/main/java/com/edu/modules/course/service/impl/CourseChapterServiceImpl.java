package com.edu.modules.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.course.entity.CourseChapter;
import com.edu.modules.course.mapper.CourseChapterMapper;
import com.edu.modules.course.service.CourseChapterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 课程章节服务实现
 */
@Slf4j
@Service
public class CourseChapterServiceImpl extends ServiceImpl<CourseChapterMapper, CourseChapter>
        implements CourseChapterService {

    @Override
    public CourseChapter createChapter(CourseChapter chapter) {
        // 参数校验
        if (chapter == null || !StringUtils.hasText(chapter.getTitle())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "章节标题不能为空");
        }
        if (chapter.getCourseId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "课程ID不能为空");
        }

        // 设置默认值
        if (chapter.getSortOrder() == null) {
            chapter.setSortOrder(0);
        }
        if (chapter.getStatus() == null) {
            chapter.setStatus(1); // 默认显示
        }

        boolean success = save(chapter);
        if (!success) {
            throw new BusinessException("创建章节失败");
        }

        log.info("章节创建成功: title={}, courseId={}", chapter.getTitle(), chapter.getCourseId());
        return chapter;
    }

    @Override
    public boolean updateChapter(CourseChapter chapter) {
        if (chapter == null || chapter.getId() == null) {
            return false;
        }

        boolean success = updateById(chapter);
        if (success) {
            log.info("章节更新成功: id={}", chapter.getId());
        }
        return success;
    }

    @Override
    public boolean deleteChapter(Long chapterId) {
        if (chapterId == null) {
            return false;
        }

        boolean success = removeById(chapterId);
        if (success) {
            log.info("章节删除成功: id={}", chapterId);
        }
        return success;
    }

    @Override
    public List<CourseChapter> getChaptersByCourseId(Long courseId) {
        if (courseId == null) {
            return List.of();
        }

        LambdaQueryWrapper<CourseChapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseChapter::getCourseId, courseId)
               .orderByAsc(CourseChapter::getSortOrder)
               .orderByAsc(CourseChapter::getCreateTime);

        return list(wrapper);
    }

    @Override
    public boolean updateChapterSort(List<Long> chapterIds) {
        if (chapterIds == null || chapterIds.isEmpty()) {
            return false;
        }

        boolean success = true;
        for (int i = 0; i < chapterIds.size(); i++) {
            CourseChapter chapter = new CourseChapter();
            chapter.setId(chapterIds.get(i));
            chapter.setSortOrder(i);
            success = success && updateById(chapter);
        }

        if (success) {
            log.info("章节排序更新成功");
        }
        return success;
    }
}