package com.edu.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.modules.system.entity.Permission;
import com.edu.modules.system.entity.Role;
import com.edu.modules.system.entity.RolePermission;
import com.edu.modules.system.entity.UserRole;
import com.edu.modules.system.mapper.PermissionMapper;
import com.edu.modules.system.mapper.RoleMapper;
import com.edu.modules.system.mapper.RolePermissionMapper;
import com.edu.modules.system.mapper.UserRoleMapper;
import com.edu.modules.system.service.RolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色权限服务实现
 */
@Slf4j
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RoleMapper, Role>
        implements RolePermissionService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    // ========== 角色管理 ==========

    @Override
    public Role createRole(Role role) {
        roleMapper.insert(role);
        log.info("角色创建成功: {}", role.getRoleName());
        return role;
    }

    @Override
    public boolean updateRole(Role role) {
        return roleMapper.updateById(role) > 0;
    }

    @Override
    public boolean deleteRole(Long roleId) {
        return roleMapper.deleteById(roleId) > 0;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleMapper.selectList(null);
    }

    @Override
    public Role getRoleByCode(String roleCode) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        return roleMapper.selectOne(wrapper);
    }

    // ========== 权限管理 ==========

    @Override
    public Permission createPermission(Permission permission) {
        permissionMapper.insert(permission);
        log.info("权限创建成功: {}", permission.getPermName());
        return permission;
    }

    @Override
    public boolean updatePermission(Permission permission) {
        return permissionMapper.updateById(permission) > 0;
    }

    @Override
    public boolean deletePermission(Long permissionId) {
        return permissionMapper.deleteById(permissionId) > 0;
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionMapper.selectList(null);
    }

    // ========== 用户角色关联 ==========

    @Override
    public boolean assignRoleToUser(Long userId, Long roleId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        return userRoleMapper.insert(userRole) > 0;
    }

    @Override
    public boolean removeRoleFromUser(Long userId, Long roleId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId)
                .eq(UserRole::getRoleId, roleId);
        return userRoleMapper.delete(wrapper) > 0;
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        return roleMapper.selectBatchIds(roleIds);
    }

    @Override
    public List<Long> getRoleUsers(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getRoleId, roleId);
        return userRoleMapper.selectList(wrapper).stream()
                .map(UserRole::getUserId)
                .collect(Collectors.toList());
    }

    // ========== 角色权限关联 ==========

    @Override
    public boolean assignPermissionToRole(Long roleId, Long permissionId) {
        RolePermission rp = new RolePermission();
        rp.setRoleId(roleId);
        rp.setPermissionId(permissionId);
        return rolePermissionMapper.insert(rp) > 0;
    }

    @Override
    public boolean removePermissionFromRole(Long roleId, Long permissionId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId)
                .eq(RolePermission::getPermissionId, permissionId);
        return rolePermissionMapper.delete(wrapper) > 0;
    }

    @Override
    public boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return false;
        }
        for (Long permissionId : permissionIds) {
            assignPermissionToRole(roleId, permissionId);
        }
        return true;
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rps = rolePermissionMapper.selectList(wrapper);
        if (rps.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> permissionIds = rps.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
        return permissionMapper.selectBatchIds(permissionIds);
    }

    @Override
    public List<Permission> getUserPermissions(Long userId) {
        List<Role> roles = getUserRoles(userId);
        if (roles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Permission> permissions = new ArrayList<>();
        for (Role role : roles) {
            permissions.addAll(getRolePermissions(role.getId()));
        }
        return permissions.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        List<Permission> permissions = getUserPermissions(userId);
        return permissions.stream()
                .anyMatch(p -> permissionCode.equals(p.getPermCode()));
    }

    @Override
    public boolean hasRole(Long userId, String roleCode) {
        List<Role> roles = getUserRoles(userId);
        return roles.stream()
                .anyMatch(r -> roleCode.equals(r.getRoleCode()));
    }
}
