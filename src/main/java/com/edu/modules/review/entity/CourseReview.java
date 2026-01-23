package com.edu.modules.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程评价实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_course_review")
public class CourseReview {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 评分（1-5星）
     */
    private BigDecimal rating;

    /**
     * 评价内容
     */
    private String comment;

    /**
     * 是否匿名：0-否，1-是
     */
    private Integer isAnonymous;

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