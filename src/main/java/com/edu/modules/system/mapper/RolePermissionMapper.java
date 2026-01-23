package com.edu.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.system.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}