package com.edu.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.config.FileConfig;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.file.entity.FileInfo;
import com.edu.modules.file.mapper.FileInfoMapper;
import com.edu.modules.file.service.FileService;
import com.edu.modules.file.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileService {

    @Autowired
    private FileConfig fileConfig;

    @Autowired(required = false)
    private OssService ossService;

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Override
    public FileInfo uploadFile(MultipartFile file, Integer category, Long relationId) {
        // 参数校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "文件不能为空");
        }

        // 验证文件类型
        String fileExtension = getFileExtension(file.getOriginalFilename());
        if (!isValidFileType(fileExtension)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "不支持的文件类型: " + fileExtension);
        }

        // 验证文件大小
        if (!isValidFileSize(file.getSize())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "文件大小超过限制");
        }

        try {
            return uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getSize(),
                            category, relationId);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public FileInfo uploadFile(InputStream inputStream, String originalFilename, Long fileSize,
                               Integer category, Long relationId) {
        // 生成唯一文件名
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFileName = generateUniqueFileName(originalFilename);

        // 获取当前用户ID
        Long userId = getCurrentUserId();

        String fileUrl;
        Integer storageType;

        // 判断使用哪种存储方式
        if (fileConfig.getOss().getEnabled() && ossService != null) {
            // 使用OSS存储
            String categoryPath = getCategoryPath(category);
            String ossFileName = categoryPath + uniqueFileName;
            fileUrl = ossService.uploadFile(inputStream, ossFileName);
            storageType = 2; // OSS存储
        } else {
            // 使用本地存储
            try {
                String categoryPath = getCategoryPath(category);
                Path localPath = Paths.get(fileConfig.getUpload().getLocalPath(), categoryPath);
                Files.createDirectories(localPath);

                Path filePath = localPath.resolve(uniqueFileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

                fileUrl = "/" + fileConfig.getUpload().getLocalPath() + categoryPath + uniqueFileName;
                storageType = 1; // 本地存储
            } catch (IOException e) {
                log.error("本地文件存储失败", e);
                throw new BusinessException("文件存储失败: " + e.getMessage());
            }
        }

        // 保存文件信息到数据库
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginalName(originalFilename);
        fileInfo.setFileName(uniqueFileName);
        fileInfo.setFileSize(fileSize);
        fileInfo.setFileType(getFileType(fileExtension));
        fileInfo.setFileExtension(fileExtension);
        fileInfo.setFilePath(fileUrl);
        fileInfo.setFileUrl(fileUrl);
        fileInfo.setCategory(category);
        fileInfo.setRelationId(relationId);
        fileInfo.setUploadUserId(userId);
        fileInfo.setStorageType(storageType);
        fileInfo.setStatus(1); // 正式文件

        boolean success = save(fileInfo);
        if (!success) {
            // 如果数据库保存失败，删除已上传的文件
            deleteFile(fileUrl);
            throw new BusinessException("文件信息保存失败");
        }

        log.info("文件上传成功: originalName={}, fileUrl={}, size={}", originalFilename, fileUrl, fileSize);
        return fileInfo;
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return false;
        }

        try {
            // 从数据库删除文件记录
            LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FileInfo::getFileUrl, fileUrl);
            FileInfo fileInfo = getOne(wrapper);

            if (fileInfo != null) {
                // 删除物理文件
                if (fileInfo.getStorageType() == 2 && ossService != null) {
                    // OSS文件删除
                    ossService.deleteFile(fileUrl);
                } else if (fileInfo.getStorageType() == 1) {
                    // 本地文件删除
                    try {
                        Path filePath = Paths.get(System.getProperty("user.dir"), fileUrl);
                        Files.deleteIfExists(filePath);
                    } catch (IOException e) {
                        log.warn("本地文件删除失败: {}", fileUrl, e);
                    }
                }

                // 删除数据库记录
                removeById(fileInfo.getId());
            }

            log.info("文件删除成功: fileUrl={}", fileUrl);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败: fileUrl={}", fileUrl, e);
            return false;
        }
    }

    @Override
    public boolean deleteFiles(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return true;
        }

        boolean allSuccess = true;
        for (String fileUrl : fileUrls) {
            boolean success = deleteFile(fileUrl);
            if (!success) {
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    @Override
    public FileInfo getFileInfo(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return null;
        }

        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getFileUrl, fileUrl);
        return getOne(wrapper);
    }

    @Override
    public List<FileInfo> getFilesByRelationId(Long relationId, Integer category) {
        if (relationId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getRelationId, relationId)
               .eq(category != null, FileInfo::getCategory, category)
               .eq(FileInfo::getStatus, 1) // 只查询正式文件
               .orderByDesc(FileInfo::getCreateTime);

        return list(wrapper);
    }

    @Override
    public String getFileUrl(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return null;
        }

        // 如果是OSS文件，直接返回完整URL
        if (filePath.startsWith("http")) {
            return filePath;
        }

        // 如果是本地文件，返回完整URL
        if (ossService != null && fileConfig.getOss().getEnabled()) {
            return ossService.getFileUrl(filePath);
        }

        return filePath;
    }

    @Override
    public boolean isValidFileType(String fileExtension) {
        if (!StringUtils.hasText(fileExtension)) {
            return false;
        }

        String[] allowedTypes = fileConfig.getUpload().getAllowedTypes();
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValidFileSize(long fileSize) {
        // 将配置的字符串转换为字节数
        String maxSize = fileConfig.getUpload().getMaxSize();
        long maxSizeBytes = parseFileSize(maxSize);
        return fileSize <= maxSizeBytes;
    }

    @Override
    public String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + (StringUtils.hasText(extension) ? "." + extension : "");
    }

    @Override
    public String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    @Override
    public String getCategoryPath(Integer category) {
        String categoryName;
        switch (category != null ? category : 0) {
            case 1: categoryName = "avatar/"; break;      // 头像
            case 2: categoryName = "course/"; break;      // 课程封面
            case 3: categoryName = "lesson/"; break;      // 课时视频
            case 4: categoryName = "exercise/"; break;    // 练习附件
            default: categoryName = "other/"; break;      // 其他
        }

        // 添加日期目录
        String datePath = LocalDateTime.now().toString().substring(0, 10).replace("-", "/") + "/";
        return categoryName + datePath;
    }

    /**
     * 获取文件类型
     */
    private String getFileType(String fileExtension) {
        if (!StringUtils.hasText(fileExtension)) {
            return "unknown";
        }

        switch (fileExtension.toLowerCase()) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                return "image";
            case "mp4":
            case "avi":
            case "wmv":
            case "flv":
            case "webm":
                return "video";
            case "pdf":
                return "document";
            case "doc":
            case "docx":
                return "word";
            case "xls":
            case "xlsx":
                return "excel";
            case "ppt":
            case "pptx":
                return "powerpoint";
            default:
                return "other";
        }
    }

    /**
     * 解析文件大小字符串为字节数
     */
    private long parseFileSize(String sizeStr) {
        if (!StringUtils.hasText(sizeStr)) {
            return 50 * 1024 * 1024; // 默认50MB
        }

        sizeStr = sizeStr.toUpperCase().trim();
        long multiplier = 1;

        if (sizeStr.endsWith("KB")) {
            multiplier = 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("MB")) {
            multiplier = 1024 * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("GB")) {
            multiplier = 1024 * 1024 * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        }

        try {
            long size = Long.parseLong(sizeStr.trim());
            return size * multiplier;
        } catch (NumberFormatException e) {
            return 50 * 1024 * 1024; // 默认50MB
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            return (Long) org.springframework.web.context.request.RequestContextHolder
                    .currentRequestAttributes().getAttribute("userId",
                    org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);
        } catch (Exception e) {
            return null;
        }
    }
}