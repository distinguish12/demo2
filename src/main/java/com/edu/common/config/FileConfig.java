package com.edu.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件服务配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileConfig {

    /**
     * 上传配置
     */
    private Upload upload = new Upload();

    /**
     * OSS配置
     */
    private Oss oss = new Oss();

    @Data
    public static class Upload {
        /**
         * 最大文件大小
         */
        private String maxSize = "50MB";

        /**
         * 允许的文件类型
         */
        private String[] allowedTypes = {
            "jpg", "jpeg", "png", "gif", "bmp",
            "mp4", "avi", "wmv", "flv", "webm",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"
        };

        /**
         * 本地存储路径
         */
        private String localPath = "uploads/";

        /**
         * 临时文件过期时间（毫秒）
         */
        private Long tempFileExpire = 24 * 60 * 60 * 1000L; // 24小时
    }

    @Data
    public static class Oss {
        /**
         * 是否启用OSS
         */
        private Boolean enabled = false;

        /**
         * OSS服务商：aliyun, tencent, qiniu
         */
        private String provider = "aliyun";

        /**
         * 终端节点
         */
        private String endpoint;

        /**
         * 访问密钥ID
         */
        private String accessKeyId;

        /**
         * 访问密钥Secret
         */
        private String accessKeySecret;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 文件访问域名
         */
        private String domain;

        /**
         * 文件过期时间（秒）
         */
        private Integer expire = 3600;
    }
}