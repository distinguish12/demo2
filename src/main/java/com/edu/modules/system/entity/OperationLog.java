package com.edu.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_operation_log")
public class OperationLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作
     */
    private String operation;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 操作地点
     */
    private String location;

    /**
     * 执行时长(ms)
     */
    private Long duration;

    /**
     * 状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}