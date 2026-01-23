package com.edu.modules.file.controller;

import com.edu.common.result.Result;
import com.edu.modules.file.entity.FileInfo;
import com.edu.modules.file.service.FileService;
import com.edu.modules.file.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Slf4j
@Api(tags = "文件上传管理")
@RestController
@RequestMapping("/api/upload")
@Validated
public class FileUploadController {

    @Autowired
    private FileService fileService;

    @Autowired(required = false)
    private OssService ossService;

    @ApiOperation("上传单个文件")
    @PostMapping("/file")
    public Result<FileInfo> uploadFile(
            @ApiParam("文件") @RequestParam("file") MultipartFile file,
            @ApiParam("文件分类：1-头像，2-课程封面，3-课时视频，4-练习附件，5-其他")
            @RequestParam(defaultValue = "5") Integer category,
            @ApiParam("关联ID") @RequestParam(required = false) Long relationId) {

        FileInfo fileInfo = fileService.uploadFile(file, category, relationId);
        return Result.success("文件上传成功", fileInfo);
    }

    @ApiOperation("上传头像")
    @PostMapping("/avatar")
    public Result<FileInfo> uploadAvatar(@ApiParam("头像文件") @RequestParam("file") MultipartFile file) {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        FileInfo fileInfo = fileService.uploadFile(file, 1, userId); // 1-头像
        return Result.success("头像上传成功", fileInfo);
    }

    @ApiOperation("上传课程封面")
    @PostMapping("/course-cover")
    public Result<FileInfo> uploadCourseCover(
            @ApiParam("封面文件") @RequestParam("file") MultipartFile file,
            @ApiParam("课程ID") @NotNull(message = "课程ID不能为空") @RequestParam Long courseId) {

        FileInfo fileInfo = fileService.uploadFile(file, 2, courseId); // 2-课程封面
        return Result.success("课程封面上传成功", fileInfo);
    }

    @ApiOperation("上传课时视频")
    @PostMapping("/lesson-video")
    public Result<FileInfo> uploadLessonVideo(
            @ApiParam("视频文件") @RequestParam("file") MultipartFile file,
            @ApiParam("课时ID") @NotNull(message = "课时ID不能为空") @RequestParam Long lessonId) {

        FileInfo fileInfo = fileService.uploadFile(file, 3, lessonId); // 3-课时视频
        return Result.success("课时视频上传成功", fileInfo);
    }

    @ApiOperation("上传练习附件")
    @PostMapping("/exercise-attachment")
    public Result<FileInfo> uploadExerciseAttachment(
            @ApiParam("附件文件") @RequestParam("file") MultipartFile file,
            @ApiParam("练习ID") @NotNull(message = "练习ID不能为空") @RequestParam Long exerciseId) {

        FileInfo fileInfo = fileService.uploadFile(file, 4, exerciseId); // 4-练习附件
        return Result.success("练习附件上传成功", fileInfo);
    }

    @ApiOperation("获取OSS上传签名")
    @GetMapping("/signature")
    public Result<Map<String, Object>> getUploadSignature(
            @ApiParam("文件名") @RequestParam String fileName) {

        if (ossService == null) {
            return Result.fail("OSS服务未启用");
        }

        OssService.OssSignature signature = ossService.generateUploadSignature(fileName);

        if (signature == null) {
            return Result.fail("签名生成失败");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("accessId", signature.getAccessId());
        result.put("policy", signature.getPolicy());
        result.put("signature", signature.getSignature());
        result.put("dir", signature.getDir());
        result.put("host", signature.getHost());
        result.put("expire", signature.getExpire());
        result.put("callback", signature.getCallback());

        return Result.success("签名生成成功", result);
    }

    @ApiOperation("删除文件")
    @DeleteMapping("/file")
    public Result<String> deleteFile(@ApiParam("文件URL") @RequestParam String fileUrl) {
        boolean success = fileService.deleteFile(fileUrl);
        return success ? Result.success("文件删除成功") : Result.fail("文件删除失败");
    }

    @ApiOperation("批量删除文件")
    @DeleteMapping("/files")
    public Result<String> deleteFiles(@ApiParam("文件URL列表") @RequestBody List<String> fileUrls) {
        boolean success = fileService.deleteFiles(fileUrls);
        return success ? Result.success("批量删除成功") : Result.fail("批量删除失败");
    }

    @ApiOperation("获取文件信息")
    @GetMapping("/file-info")
    public Result<FileInfo> getFileInfo(@ApiParam("文件URL") @RequestParam String fileUrl) {
        FileInfo fileInfo = fileService.getFileInfo(fileUrl);
        return fileInfo != null ? Result.success(fileInfo) : Result.fail("文件不存在");
    }

    @ApiOperation("获取关联文件列表")
    @GetMapping("/files")
    public Result<List<FileInfo>> getFilesByRelationId(
            @ApiParam("关联ID") @RequestParam Long relationId,
            @ApiParam("文件分类") @RequestParam(required = false) Integer category) {

        List<FileInfo> files = fileService.getFilesByRelationId(relationId, category);
        return Result.success(files);
    }

    @ApiOperation("获取文件访问URL")
    @GetMapping("/file-url")
    public Result<String> getFileUrl(@ApiParam("文件路径") @RequestParam String filePath) {
        String fileUrl = fileService.getFileUrl(filePath);
        return Result.success(fileUrl);
    }
}