package com.edu.modules.file;

import com.edu.modules.course.entity.Course;
import com.edu.modules.course.service.CourseService;
import com.edu.modules.file.entity.FileInfo;
import com.edu.modules.file.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * 文件服务模块测试
 */
@SpringBootTest
public class FileModuleTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private FileService fileService;

    /**
     * 测试文件上传功能
     */
    @Test
    public void testFileUpload() {
        // 创建测试课程
        Course course = new Course();
        course.setTitle("文件测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        // 创建模拟文件
        String content = "这是一个测试文件内容";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
        MockMultipartFile mockFile = new MockMultipartFile(
            "test.txt",
            "test.txt",
            "text/plain",
            inputStream
        );

        try {
            // 测试上传头像文件
            FileInfo avatarFile = fileService.uploadFile(mockFile, 1, 1L); // 1-头像, 用户ID为1
            System.out.println("头像文件上传成功: " + avatarFile.getFileUrl());

            // 测试上传课程封面
            FileInfo coverFile = fileService.uploadFile(mockFile, 2, createdCourse.getId()); // 2-课程封面
            System.out.println("课程封面上传成功: " + coverFile.getFileUrl());

            // 测试上传课时视频（模拟）
            FileInfo videoFile = fileService.uploadFile(mockFile, 3, 1L); // 3-课时视频
            System.out.println("课时视频上传成功: " + videoFile.getFileUrl());

            // 验证文件信息查询
            FileInfo queriedFile = fileService.getFileInfo(avatarFile.getFileUrl());
            System.out.println("查询文件信息: " + (queriedFile != null ? "成功" : "失败"));

            // 验证根据关联ID查询文件
            List<FileInfo> courseFiles = fileService.getFilesByRelationId(createdCourse.getId(), 2); // 课程封面
            System.out.println("查询课程文件数量: " + courseFiles.size());

            // 清理测试数据
            fileService.deleteFile(avatarFile.getFileUrl());
            fileService.deleteFile(coverFile.getFileUrl());
            fileService.deleteFile(videoFile.getFileUrl());
            courseService.deleteCourse(createdCourse.getId());

        } catch (Exception e) {
            System.out.println("文件上传测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试文件类型验证
     */
    @Test
    public void testFileTypeValidation() {
        // 测试有效文件类型
        String[] validExtensions = {"jpg", "png", "mp4", "pdf", "docx"};
        for (String ext : validExtensions) {
            boolean isValid = fileService.isValidFileType(ext);
            System.out.println("文件类型 " + ext + " 验证: " + (isValid ? "有效" : "无效"));
        }

        // 测试无效文件类型
        String[] invalidExtensions = {"exe", "bat", "com", "scr"};
        for (String ext : invalidExtensions) {
            boolean isValid = fileService.isValidFileType(ext);
            System.out.println("文件类型 " + ext + " 验证: " + (isValid ? "有效（错误）" : "无效（正确）"));
        }
    }

    /**
     * 测试文件大小验证
     */
    @Test
    public void testFileSizeValidation() {
        // 测试有效文件大小
        long[] validSizes = {1024, 1024 * 1024, 10 * 1024 * 1024, 50 * 1024 * 1024}; // 1KB, 1MB, 10MB, 50MB
        for (long size : validSizes) {
            boolean isValid = fileService.isValidFileSize(size);
            System.out.println("文件大小 " + size + " 字节验证: " + (isValid ? "有效" : "无效"));
        }

        // 测试无效文件大小
        long[] invalidSizes = {60 * 1024 * 1024, 100 * 1024 * 1024}; // 60MB, 100MB
        for (long size : invalidSizes) {
            boolean isValid = fileService.isValidFileSize(size);
            System.out.println("文件大小 " + size + " 字节验证: " + (isValid ? "有效（错误）" : "无效（正确）"));
        }
    }

    /**
     * 测试文件名生成和路径处理
     */
    @Test
    public void testFileNameAndPath() {
        // 测试唯一文件名生成
        String originalName = "test.jpg";
        String uniqueName1 = fileService.generateUniqueFileName(originalName);
        String uniqueName2 = fileService.generateUniqueFileName(originalName);

        System.out.println("原始文件名: " + originalName);
        System.out.println("生成唯一文件名1: " + uniqueName1);
        System.out.println("生成唯一文件名2: " + uniqueName2);
        System.out.println("文件名唯一性: " + (!uniqueName1.equals(uniqueName2) ? "通过" : "失败"));

        // 测试文件扩展名提取
        String[] testFiles = {"document.pdf", "image.jpg", "video.mp4", "noextension"};
        for (String file : testFiles) {
            String extension = fileService.getFileExtension(file);
            System.out.println("文件 " + file + " 的扩展名: " + extension);
        }

        // 测试分类路径生成
        Integer[] categories = {1, 2, 3, 4, 5};
        String[] expectedPaths = {"avatar/", "course/", "lesson/", "exercise/", "other/"};

        for (int i = 0; i < categories.length; i++) {
            String path = fileService.getCategoryPath(categories[i]);
            System.out.println("分类 " + categories[i] + " 的路径: " + path);
            System.out.println("路径正确性: " + (path.contains(expectedPaths[i]) ? "正确" : "错误"));
        }
    }

    /**
     * 测试批量文件操作
     */
    @Test
    public void testBatchFileOperations() {
        // 创建多个测试文件
        String content = "测试文件内容";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());

        MockMultipartFile mockFile1 = new MockMultipartFile("test1.txt", "test1.txt", "text/plain", inputStream);
        MockMultipartFile mockFile2 = new MockMultipartFile("test2.txt", "test2.txt", "text/plain", inputStream);

        try {
            // 上传多个文件
            FileInfo fileInfo1 = fileService.uploadFile(mockFile1, 5, 1L); // 5-其他
            FileInfo fileInfo2 = fileService.uploadFile(mockFile2, 5, 1L);

            System.out.println("上传文件1: " + fileInfo1.getFileUrl());
            System.out.println("上传文件2: " + fileInfo2.getFileUrl());

            // 批量删除文件
            List<String> fileUrls = List.of(fileInfo1.getFileUrl(), fileInfo2.getFileUrl());
            boolean batchDeleteResult = fileService.deleteFiles(fileUrls);
            System.out.println("批量删除结果: " + (batchDeleteResult ? "成功" : "失败"));

        } catch (Exception e) {
            System.out.println("批量文件操作测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试文件URL处理
     */
    @Test
    public void testFileUrlProcessing() {
        // 测试本地文件URL
        String localPath = "/uploads/avatar/2024-01-23/test.jpg";
        String processedUrl = fileService.getFileUrl(localPath);
        System.out.println("本地文件URL处理: " + localPath + " -> " + processedUrl);

        // 测试OSS文件URL
        String ossPath = "avatar/2024-01-23/test.jpg";
        processedUrl = fileService.getFileUrl(ossPath);
        System.out.println("OSS文件URL处理: " + ossPath + " -> " + processedUrl);
    }

    /**
     * 测试完整的文件服务流程
     */
    @Test
    public void testCompleteFileServiceFlow() {
        System.out.println("=== 开始完整的文件服务流程测试 ===");

        try {
            // 1. 创建测试课程
            Course course = new Course();
            course.setTitle("完整流程测试课程");
            course.setInstructorId(2L);
            Course createdCourse = courseService.createCourse(course);
            System.out.println("✓ 创建测试课程成功");

            // 2. 上传课程封面
            String coverContent = "这是课程封面图片内容";
            ByteArrayInputStream coverStream = new ByteArrayInputStream(coverContent.getBytes());
            MockMultipartFile coverFile = new MockMultipartFile(
                "cover.jpg", "cover.jpg", "image/jpeg", coverStream);

            FileInfo coverFileInfo = fileService.uploadFile(coverFile, 2, createdCourse.getId());
            System.out.println("✓ 上传课程封面成功: " + coverFileInfo.getFileUrl());

            // 3. 上传课时视频（模拟）
            String videoContent = "这是课时视频内容";
            ByteArrayInputStream videoStream = new ByteArrayInputStream(videoContent.getBytes());
            MockMultipartFile videoFile = new MockMultipartFile(
                "lesson.mp4", "lesson.mp4", "video/mp4", videoStream);

            FileInfo videoFileInfo = fileService.uploadFile(videoFile, 3, 1L); // 假设课时ID为1
            System.out.println("✓ 上传课时视频成功: " + videoFileInfo.getFileUrl());

            // 4. 查询课程相关文件
            List<FileInfo> courseFiles = fileService.getFilesByRelationId(createdCourse.getId(), null);
            System.out.println("✓ 查询到课程相关文件: " + courseFiles.size() + " 个");

            // 5. 验证文件信息
            for (FileInfo file : courseFiles) {
                System.out.println("  文件: " + file.getOriginalName() +
                                 ", 大小: " + file.getFileSize() + " 字节" +
                                 ", 类型: " + file.getFileType());
            }

            // 6. 获取文件访问URL
            for (FileInfo file : courseFiles) {
                String accessUrl = fileService.getFileUrl(file.getFilePath());
                System.out.println("✓ 文件访问URL: " + accessUrl);
            }

            // 7. 清理测试文件
            for (FileInfo file : courseFiles) {
                fileService.deleteFile(file.getFileUrl());
            }
            System.out.println("✓ 清理测试文件成功");

            // 8. 删除测试课程
            courseService.deleteCourse(createdCourse.getId());
            System.out.println("✓ 删除测试课程成功");

            System.out.println("=== 完整的文件服务流程测试通过 ===");

        } catch (Exception e) {
            System.out.println("=== 完整的文件服务流程测试失败 ===");
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
        }
    }
}