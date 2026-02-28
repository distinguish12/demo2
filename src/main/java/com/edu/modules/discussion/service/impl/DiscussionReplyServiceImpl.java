package com.edu.modules.discussion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.discussion.entity.DiscussionReply;
import com.edu.modules.discussion.mapper.DiscussionReplyMapper;
import com.edu.modules.discussion.service.DiscussionReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 讨论回复服务实现
 */
@Slf4j
@Service
public class DiscussionReplyServiceImpl extends ServiceImpl<DiscussionReplyMapper, DiscussionReply>
        implements DiscussionReplyService {

    @Override
    public DiscussionReply createReply(DiscussionReply reply) {
        // 参数校验
        if (reply == null || !StringUtils.hasText(reply.getContent())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "回复内容不能为空");
        }
        if (reply.getDiscussionId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "讨论ID不能为空");
        }
        if (reply.getUserId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户ID不能为空");
        }

        // 设置默认值
        if (reply.getParentId() == null) {
            reply.setParentId(0L); // 默认为顶级回复
        }
        if (reply.getLikeCount() == null) {
            reply.setLikeCount(0);
        }
        if (reply.getStatus() == null) {
            reply.setStatus(1);
        }

        boolean success = save(reply);
        if (!success) {
            throw new BusinessException("创建回复失败");
        }

        log.info("回复创建成功: discussionId={}, userId={}", reply.getDiscussionId(), reply.getUserId());
        return reply;
    }

    @Override
    public boolean deleteReply(Long replyId) {
        if (replyId == null) {
            return false;
        }

        boolean success = removeById(replyId);
        if (success) {
            log.info("回复删除成功: id={}", replyId);
        }
        return success;
    }

    @Override
    public List<DiscussionReply> getRepliesByDiscussionId(Long discussionId) {
        if (discussionId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<DiscussionReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiscussionReply::getDiscussionId, discussionId)
               .eq(DiscussionReply::getStatus, 1) // 只查询显示状态的回复
               .orderByAsc(DiscussionReply::getCreateTime);

        return list(wrapper);
    }

    @Override
    public boolean toggleLike(Long replyId, Long userId) {
        // 这里简化实现，实际应该有专门的点赞表来记录用户点赞状态
        // 暂时直接增加点赞数
        if (replyId == null || userId == null) {
            return false;
        }

        LambdaUpdateWrapper<DiscussionReply> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(DiscussionReply::getId, replyId)
               .setSql("like_count = like_count + 1");

        boolean success = update(wrapper);
        if (success) {
            log.info("回复点赞成功: replyId={}, userId={}", replyId, userId);
        }
        return success;
    }

    @Override
    public int getLikeCount(Long replyId) {
        if (replyId == null) {
            return 0;
        }

        DiscussionReply reply = getById(replyId);
        return reply != null && reply.getLikeCount() != null ? reply.getLikeCount() : 0;
    }

    @Override
    public boolean hasUserLiked(Long replyId, Long userId) {
        // 这里简化实现，实际应该查询点赞记录表
        // 暂时返回false，表示未点赞
        return false;
    }
}