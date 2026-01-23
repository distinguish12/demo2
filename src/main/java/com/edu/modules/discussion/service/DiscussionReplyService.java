package com.edu.modules.discussion.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.discussion.entity.DiscussionReply;

import java.util.List;

/**
 * 讨论回复服务接口
 */
public interface DiscussionReplyService extends IService<DiscussionReply> {

    /**
     * 创建回复
     */
    DiscussionReply createReply(DiscussionReply reply);

    /**
     * 删除回复
     */
    boolean deleteReply(Long replyId);

    /**
     * 根据讨论ID获取回复列表
     */
    List<DiscussionReply> getRepliesByDiscussionId(Long discussionId);

    /**
     * 点赞/取消点赞
     */
    boolean toggleLike(Long replyId, Long userId);

    /**
     * 获取回复的点赞数
     */
    int getLikeCount(Long replyId);

    /**
     * 检查用户是否已点赞
     */
    boolean hasUserLiked(Long replyId, Long userId);
}