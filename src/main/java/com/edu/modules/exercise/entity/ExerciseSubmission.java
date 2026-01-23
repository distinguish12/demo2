package com.edu.modules.exercise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 练习提交实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_exercise_submission")
public class ExerciseSubmission {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 练习ID
     */
    private Long exerciseId;

    /**
     * 答案
     */
    private String answer;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 是否正确
     */
    private Integer isCorrect;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

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