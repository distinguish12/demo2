package com.edu.modules.discussion.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.discussion.entity.Discussion;
import com.edu.modules.discussion.entity.DiscussionReply;

import java.util.List;

/**
 * 讨论帖服务接口
 */
public interface DiscussionService extends IService<Discussion> {

    /**
     * 创建讨论帖
     */
    Discussion createDiscussion(Discussion discussion);

    /**
     * 更新讨论帖
     */
    boolean updateDiscussion(Discussion discussion);

    /**
     * 删除讨论帖
     */
    boolean deleteDiscussion(Long discussionId);

    /**
     * 根据课程ID获取讨论帖列表
     */
    List<Discussion> getDiscussionsByCourseId(Long courseId);

    /**
     * 增加查看数
     */
    boolean incrementViewCount(Long discussionId);

    /**
     * 置顶/取消置顶
     */
    boolean toggleTop(Long discussionId);

    /**
     * 设为精华/取消精华
     */
    boolean toggleEssence(Long discussionId);

    /**
     * 获取热门讨论帖
     */
    List<Discussion> getHotDiscussions(Long courseId, Integer limit);
}