package com.edu.modules.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 考试题目实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_exam_question")
public class ExamQuestion {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 考试ID
     */
    private Long examId;

    /**
     * 题目
     */
    private String title;

    /**
     * 类型：1-单选，2-多选，3-判断，4-填空
     */
    private Integer type;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 选项（JSON格式）
     */
    private String options;

    /**
     * 答案
     */
    private String answer;

    /**
     * 分数
     */
    private BigDecimal score;

    /**
     * 排序
     */
    private Integer sortOrder;
}