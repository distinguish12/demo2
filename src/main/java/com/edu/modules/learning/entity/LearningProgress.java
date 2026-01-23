package com.edu.modules.learning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 学习进度实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_learning_progress")
public class LearningProgress {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课时ID
     */
    private Long lessonId;

    /**
     * 进度百分比（0-100）
     */
    private Integer progress;

    /**
     * 最后播放位置（秒）
     */
    private Integer lastPosition;

    /**
     * 观看时长（秒）
     */
    private Integer duration;

    /**
     * 是否完成：0-未完成，1-已完成
     */
    private Integer completed;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessTime;

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
}