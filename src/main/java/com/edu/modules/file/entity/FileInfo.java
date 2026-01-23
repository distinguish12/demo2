package com.edu.modules.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件信息实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_file_info")
public class FileInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件原始名称
     */
    private String originalName;

    /**
     * 文件存储名称
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 文件分类：1-头像，2-课程封面，3-课时视频，4-练习附件，5-其他
     */
    private Integer category;

    /**
     * 关联ID（如课程ID、用户ID等）
     */
    private Long relationId;

    /**
     * 上传用户ID
     */
    private Long uploadUserId;

    /**
     * 存储类型：1-本地存储，2-OSS存储
     */
    private Integer storageType;

    /**
     * 文件状态：0-临时文件，1-正式文件，2-已删除
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