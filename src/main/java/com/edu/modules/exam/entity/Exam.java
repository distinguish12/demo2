package com.edu.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_exam")
public class Exam {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 考试标题
     */
    private String title;

    /**
     * 考试描述
     */
    private String description;

    /**
     * 考试时长（分钟）
     */
    private Integer duration;

    /**
     * 总分
     */
    private BigDecimal totalScore;

    /**
     * 及格分数
     */
    private BigDecimal passScore;

    /**
     * 题目数量
     */
    private Integer questionCount;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态：0-未开始，1-进行中，2-已结束
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