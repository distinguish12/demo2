package com.edu.modules.discussion.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.discussion.entity.DiscussionReply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 讨论回复Mapper
 */
@Mapper
public interface DiscussionReplyMapper extends BaseMapper<DiscussionReply> {
}