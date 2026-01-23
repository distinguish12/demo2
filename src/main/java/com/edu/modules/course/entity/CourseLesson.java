package com.edu.modules.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 课程课时实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_course_lesson")
public class CourseLesson {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 章节ID
     */
    private Long chapterId;

    /**
     * 课时标题
     */
    private String title;

    /**
     * 课时描述
     */
    private String description;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否免费：0-收费，1-免费
     */
    private Integer isFree;

    /**
     * 状态：0-隐藏，1-显示
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}