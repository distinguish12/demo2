package com.edu.modules.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.course.entity.CourseLesson;

import java.util.List;

/**
 * 课程课时服务接口
 */
public interface CourseLessonService extends IService<CourseLesson> {

    /**
     * 创建课时
     */
    CourseLesson createLesson(CourseLesson lesson);

    /**
     * 更新课时
     */
    boolean updateLesson(CourseLesson lesson);

    /**
     * 删除课时
     */
    boolean deleteLesson(Long lessonId);

    /**
     * 根据章节ID获取课时列表
     */
    List<CourseLesson> getLessonsByChapterId(Long chapterId);

    /**
     * 根据课程ID获取所有课时
     */
    List<CourseLesson> getLessonsByCourseId(Long courseId);

    /**
     * 批量更新课时排序
     */
    boolean updateLessonSort(List<Long> lessonIds);
}