package com.edu.modules.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_course")
public class Course {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 讲师ID
     */
    private Long instructorId;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 难度级别：1-初级，2-中级，3-高级
     */
    private Integer level;

    /**
     * 总时长（分钟）
     */
    private Integer duration;

    /**
     * 课时数
     */
    private Integer lessonCount;

    /**
     * 学员数
     */
    private Integer studentCount;

    /**
     * 平均评分
     */
    private BigDecimal rating;

    /**
     * 评价数
     */
    private Integer reviewCount;

    /**
     * 状态：0-草稿，1-发布，2-下架
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