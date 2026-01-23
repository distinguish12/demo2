package com.edu.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}