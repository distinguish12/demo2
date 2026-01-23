package com.edu.modules.discussion.controller;

import com.edu.common.result.Result;
import com.edu.modules.discussion.entity.DiscussionReply;
import com.edu.modules.discussion.service.DiscussionReplyService;
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
 * 讨论回复控制器
 */
@Slf4j
@Api(tags = "讨论回复管理")
@RestController
@RequestMapping("/api/discussions/{discussionId}/replies")
@Validated
public class DiscussionReplyController {

    @Autowired
    private DiscussionReplyService discussionReplyService;

    @ApiOperation("发表回复")
    @PostMapping
    public Result<DiscussionReply> createReply(
            @ApiParam("讨论帖ID") @PathVariable Long discussionId,
            @ApiParam("回复内容") @NotBlank(message = "回复内容不能为空") @RequestParam String content,
            @ApiParam("父回复ID") @RequestParam(required = false, defaultValue = "0") Long parentId) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        DiscussionReply reply = new DiscussionReply();
        reply.setDiscussionId(discussionId);
        reply.setUserId(userId);
        reply.setContent(content);
        reply.setParentId(parentId);

        DiscussionReply result = discussionReplyService.createReply(reply);
        return Result.success("回复发表成功", result);
    }

    @ApiOperation("删除回复")
    @DeleteMapping("/{replyId}")
    public Result<String> deleteReply(
            @ApiParam("讨论帖ID") @PathVariable Long discussionId,
            @ApiParam("回复ID") @PathVariable Long replyId) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        // 检查权限：只有回复者可以删除自己的回复
        DiscussionReply existing = discussionReplyService.getById(replyId);
        if (existing == null) {
            return Result.fail("回复不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            return Result.fail("无权限删除此回复");
        }

        boolean success = discussionReplyService.deleteReply(replyId);
        return success ? Result.success("回复删除成功") : Result.fail("回复删除失败");
    }

    @ApiOperation("获取回复列表")
    @GetMapping
    public Result<List<DiscussionReply>> getReplies(@ApiParam("讨论帖ID") @PathVariable Long discussionId) {
        List<DiscussionReply> replies = discussionReplyService.getRepliesByDiscussionId(discussionId);
        return Result.success(replies);
    }

    @ApiOperation("获取回复详情")
    @GetMapping("/{replyId}")
    public Result<DiscussionReply> getReply(
            @ApiParam("讨论帖ID") @PathVariable Long discussionId,
            @ApiParam("回复ID") @PathVariable Long replyId) {

        DiscussionReply reply = discussionReplyService.getById(replyId);
        if (reply == null || !reply.getDiscussionId().equals(discussionId)) {
            return Result.fail("回复不存在");
        }
        return Result.success(reply);
    }

    @ApiOperation("点赞回复")
    @PostMapping("/{replyId}/like")
    public Result<String> likeReply(
            @ApiParam("讨论帖ID") @PathVariable Long discussionId,
            @ApiParam("回复ID") @PathVariable Long replyId) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        boolean success = discussionReplyService.toggleLike(replyId, userId);
        return success ? Result.success("点赞成功") : Result.fail("点赞失败");
    }

    @ApiOperation("获取点赞数")
    @GetMapping("/{replyId}/likes")
    public Result<Integer> getLikeCount(
            @ApiParam("讨论帖ID") @PathVariable Long discussionId,
            @ApiParam("回复ID") @PathVariable Long replyId) {

        int likeCount = discussionReplyService.getLikeCount(replyId);
        return Result.success(likeCount);
    }

    @ApiOperation("检查是否已点赞")
    @GetMapping("/{replyId}/liked")
    public Result<Boolean> checkLiked(
            @ApiParam("讨论帖ID") @PathVariable Long discussionId,
            @ApiParam("回复ID") @PathVariable Long replyId) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.success(false);
        }

        boolean liked = discussionReplyService.hasUserLiked(replyId, userId);
        return Result.success(liked);
    }
}