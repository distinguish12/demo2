package com.edu.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 权限实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_permission")
public class Permission {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     */
    private String permName;

    /**
     * 权限编码
     */
    private String permCode;

    /**
     * 资源路径
     */
    private String resource;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 状态：0-禁用，1-启用
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