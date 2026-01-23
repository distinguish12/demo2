package com.edu.modules.file.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.edu.common.config.FileConfig;
import com.edu.modules.file.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 阿里云OSS服务实现
 */
@Slf4j
@Service
public class AliyunOssServiceImpl implements OssService {

    @Autowired
    private FileConfig fileConfig;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        if (fileConfig.getOss().getEnabled()) {
            this.ossClient = new OSSClientBuilder().build(
                fileConfig.getOss().getEndpoint(),
                fileConfig.getOss().getAccessKeyId(),
                fileConfig.getOss().getAccessKeySecret()
            );
            log.info("阿里云OSS客户端初始化成功");
        }
    }

    @PreDestroy
    public void destroy() {
        if (this.ossClient != null) {
            this.ossClient.shutdown();
            log.info("阿里云OSS客户端已关闭");
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName) {
        if (!fileConfig.getOss().getEnabled() || ossClient == null) {
            throw new RuntimeException("OSS服务未启用或未初始化");
        }

        try {
            // 设置文件元信息
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(fileName));

            // 上传文件
            ossClient.putObject(fileConfig.getOss().getBucketName(), fileName, inputStream, metadata);

            // 生成文件访问URL
            String fileUrl = getFileUrl(fileName);

            log.info("文件上传到OSS成功: fileName={}, fileUrl={}", fileName, fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("文件上传到OSS失败: fileName={}", fileName, e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (!fileConfig.getOss().getEnabled() || ossClient == null) {
            return false;
        }

        try {
            // 从URL中提取文件路径
            String fileName = extractFileNameFromUrl(fileUrl);
            if (fileName == null) {
                return false;
            }

            // 删除文件
            ossClient.deleteObject(fileConfig.getOss().getBucketName(), fileName);

            log.info("OSS文件删除成功: fileUrl={}", fileUrl);
            return true;

        } catch (Exception e) {
            log.error("OSS文件删除失败: fileUrl={}", fileUrl, e);
            return false;
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        if (!fileConfig.getOss().getEnabled()) {
            return filePath;
        }

        try {
            // 生成带签名的URL
            Date expiration = new Date(System.currentTimeMillis() + fileConfig.getOss().getExpire() * 1000);
            URL url = ossClient.generatePresignedUrl(fileConfig.getOss().getBucketName(), filePath, expiration);
            return url.toString();

        } catch (Exception e) {
            log.warn("生成OSS文件URL失败，使用基础URL: filePath={}", filePath);
            return fileConfig.getOss().getDomain() + "/" + filePath;
        }
    }

    @Override
    public OssSignature generateUploadSignature(String fileName) {
        // 这里可以实现前端直传签名生成
        // 为了简化，暂时返回null
        log.info("生成OSS上传签名: fileName={}", fileName);
        return null;
    }

    /**
     * 获取文件Content-Type
     */
    private String getContentType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }

        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 从URL中提取文件名
     */
    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null) {
            return null;
        }

        try {
            String domain = fileConfig.getOss().getDomain();
            if (fileUrl.startsWith(domain)) {
                return fileUrl.substring(domain.length() + 1);
            }

            // 如果是带签名的URL，提取文件路径
            if (fileUrl.contains("?")) {
                String baseUrl = fileUrl.substring(0, fileUrl.indexOf('?'));
                if (baseUrl.startsWith(domain)) {
                    return baseUrl.substring(domain.length() + 1);
                }
            }

            return null;
        } catch (Exception e) {
            log.warn("从URL提取文件名失败: fileUrl={}", fileUrl);
            return null;
        }
    }
}