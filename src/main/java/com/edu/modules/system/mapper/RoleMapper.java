package com.edu.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.system.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}