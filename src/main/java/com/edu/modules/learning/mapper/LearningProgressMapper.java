package com.edu.modules.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.learning.entity.LearningProgress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习进度Mapper
 */
@Mapper
public interface LearningProgressMapper extends BaseMapper<LearningProgress> {
}