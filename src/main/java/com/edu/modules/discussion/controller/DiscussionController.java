package com.edu.modules.discussion.controller;

import com.edu.common.result.Result;
import com.edu.modules.discussion.entity.Discussion;
import com.edu.modules.discussion.service.DiscussionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 讨论帖控制器
 */
@Slf4j
@Api(tags = "讨论帖管理")
@RestController
@RequestMapping("/api/discussions")
@Validated
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @ApiOperation("发布讨论帖")
    @PostMapping
    public Result<Discussion> createDiscussion(
            @ApiParam("课程ID") @NotNull(message = "课程ID不能为空") @RequestParam Long courseId,
            @ApiParam("标题") @NotBlank(message = "标题不能为空") @RequestParam String title,
            @ApiParam("内容") @NotBlank(message = "内容不能为空") @RequestParam String content) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        Discussion discussion = new Discussion();
        discussion.setCourseId(courseId);
        discussion.setUserId(userId);
        discussion.setTitle(title);
        discussion.setContent(content);

        Discussion result = discussionService.createDiscussion(discussion);
        return Result.success("讨论帖发布成功", result);
    }

    @ApiOperation("更新讨论帖")
    @PutMapping("/{id}")
    public Result<String> updateDiscussion(
            @ApiParam("讨论帖ID") @PathVariable Long id,
            @ApiParam("标题") @RequestParam(required = false) String title,
            @ApiParam("内容") @RequestParam(required = false) String content) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        // 检查权限：只有发帖人可以修改
        Discussion existing = discussionService.getById(id);
        if (existing == null) {
            return Result.fail("讨论帖不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            return Result.fail("无权限修改此讨论帖");
        }

        Discussion discussion = new Discussion();
        discussion.setId(id);
        discussion.setTitle(title);
        discussion.setContent(content);

        boolean success = discussionService.updateDiscussion(discussion);
        return success ? Result.success("讨论帖更新成功") : Result.fail("讨论帖更新失败");
    }

    @ApiOperation("删除讨论帖")
    @DeleteMapping("/{id}")
    public Result<String> deleteDiscussion(@ApiParam("讨论帖ID") @PathVariable Long id) {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        // 检查权限：只有发帖人可以删除
        Discussion existing = discussionService.getById(id);
        if (existing == null) {
            return Result.fail("讨论帖不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            return Result.fail("无权限删除此讨论帖");
        }

        boolean success = discussionService.deleteDiscussion(id);
        return success ? Result.success("讨论帖删除成功") : Result.fail("讨论帖删除失败");
    }

    @ApiOperation("获取讨论帖详情")
    @GetMapping("/{id}")
    public Result<Discussion> getDiscussion(@ApiParam("讨论帖ID") @PathVariable Long id) {
        Discussion discussion = discussionService.getById(id);
        if (discussion == null) {
            return Result.fail("讨论帖不存在");
        }

        // 增加查看数
        discussionService.incrementViewCount(id);

        return Result.success(discussion);
    }

    @ApiOperation("获取课程讨论帖列表")
    @GetMapping("/course/{courseId}")
    public Result<List<Discussion>> getCourseDiscussions(@ApiParam("课程ID") @PathVariable Long courseId) {
        List<Discussion> discussions = discussionService.getDiscussionsByCourseId(courseId);
        return Result.success(discussions);
    }

    @ApiOperation("获取热门讨论帖")
    @GetMapping("/course/{courseId}/hot")
    public Result<List<Discussion>> getHotDiscussions(
            @ApiParam("课程ID") @PathVariable Long courseId,
            @ApiParam("数量限制") @RequestParam(defaultValue = "10") Integer limit) {

        List<Discussion> discussions = discussionService.getHotDiscussions(courseId, limit);
        return Result.success(discussions);
    }

    @ApiOperation("置顶/取消置顶讨论帖")
    @PutMapping("/{id}/toggle-top")
    public Result<String> toggleTop(@ApiParam("讨论帖ID") @PathVariable Long id) {
        // 这里应该检查管理员权限，暂时跳过
        boolean success = discussionService.toggleTop(id);
        String message = success ? "操作成功" : "操作失败";
        return success ? Result.success(message) : Result.fail(message);
    }

    @ApiOperation("设为精华/取消精华")
    @PutMapping("/{id}/toggle-essence")
    public Result<String> toggleEssence(@ApiParam("讨论帖ID") @PathVariable Long id) {
        // 这里应该检查管理员权限，暂时跳过
        boolean success = discussionService.toggleEssence(id);
        String message = success ? "操作成功" : "操作失败";
        return success ? Result.success(message) : Result.fail(message);
    }
}