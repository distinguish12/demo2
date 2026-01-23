package com.edu.modules.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.course.entity.CourseChapter;

import java.util.List;

/**
 * 课程章节服务接口
 */
public interface CourseChapterService extends IService<CourseChapter> {

    /**
     * 创建章节
     */
    CourseChapter createChapter(CourseChapter chapter);

    /**
     * 更新章节
     */
    boolean updateChapter(CourseChapter chapter);

    /**
     * 删除章节
     */
    boolean deleteChapter(Long chapterId);

    /**
     * 根据课程ID获取章节列表
     */
    List<CourseChapter> getChaptersByCourseId(Long courseId);

    /**
     * 批量更新章节排序
     */
    boolean updateChapterSort(List<Long> chapterIds);
}