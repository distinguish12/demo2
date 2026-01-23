package com.edu.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_exam_record")
public class ExamRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 考试ID
     */
    private Long examId;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 总分
     */
    private BigDecimal totalScore;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 状态：0-未完成，1-已完成，2-超时
     */
    private Integer status;

    /**
     * 答案（JSON格式）
     */
    private String answers;

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