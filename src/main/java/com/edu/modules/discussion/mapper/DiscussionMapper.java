package com.edu.modules.discussion.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.discussion.entity.Discussion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 讨论帖Mapper
 */
@Mapper
public interface DiscussionMapper extends BaseMapper<Discussion> {
}