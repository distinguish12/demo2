package com.edu.modules.file.service;

import com.edu.modules.file.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传文件
     */
    FileInfo uploadFile(MultipartFile file, Integer category, Long relationId);

    /**
     * 上传文件（通过输入流）
     */
    FileInfo uploadFile(InputStream inputStream, String originalFilename, Long fileSize,
                       Integer category, Long relationId);

    /**
     * 删除文件
     */
    boolean deleteFile(String fileUrl);

    /**
     * 批量删除文件
     */
    boolean deleteFiles(List<String> fileUrls);

    /**
     * 根据文件URL获取文件信息
     */
    FileInfo getFileInfo(String fileUrl);

    /**
     * 根据关联ID获取文件列表
     */
    List<FileInfo> getFilesByRelationId(Long relationId, Integer category);

    /**
     * 获取文件访问URL
     */
    String getFileUrl(String filePath);

    /**
     * 验证文件类型
     */
    boolean isValidFileType(String fileExtension);

    /**
     * 验证文件大小
     */
    boolean isValidFileSize(long fileSize);

    /**
     * 生成唯一文件名
     */
    String generateUniqueFileName(String originalFilename);

    /**
     * 获取文件扩展名
     */
    String getFileExtension(String filename);

    /**
     * 获取文件分类目录
     */
    String getCategoryPath(Integer category);
}