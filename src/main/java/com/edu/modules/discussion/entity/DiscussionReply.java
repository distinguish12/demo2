package com.edu.modules.discussion.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 讨论回复实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_discussion_reply")
public class DiscussionReply {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 讨论ID
     */
    private Long discussionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 父回复ID
     */
    private Long parentId;

    /**
     * 内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

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