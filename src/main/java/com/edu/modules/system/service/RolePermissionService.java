package com.edu.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.system.entity.Permission;
import com.edu.modules.system.entity.Role;
import com.edu.modules.system.entity.RolePermission;
import com.edu.modules.system.entity.UserRole;

import java.util.List;
import java.util.Set;

/**
 * 角色权限服务接口
 */
public interface RolePermissionService extends IService<Role> {

    // ========== 角色管理 ==========

    /**
     * 创建角色
     */
    Role createRole(Role role);

    /**
     * 更新角色
     */
    boolean updateRole(Role role);

    /**
     * 删除角色
     */
    boolean deleteRole(Long roleId);

    /**
     * 获取所有角色
     */
    List<Role> getAllRoles();

    /**
     * 根据编码获取角色
     */
    Role getRoleByCode(String roleCode);

    // ========== 权限管理 ==========

    /**
     * 创建权限
     */
    Permission createPermission(Permission permission);

    /**
     * 更新权限
     */
    boolean updatePermission(Permission permission);

    /**
     * 删除权限
     */
    boolean deletePermission(Long permissionId);

    /**
     * 获取所有权限
     */
    List<Permission> getAllPermissions();

    // ========== 用户角色关联 ==========

    /**
     * 为用户分配角色
     */
    boolean assignRoleToUser(Long userId, Long roleId);

    /**
     * 移除用户的角色
     */
    boolean removeRoleFromUser(Long userId, Long roleId);

    /**
     * 获取用户的角色列表
     */
    List<Role> getUserRoles(Long userId);

    /**
     * 获取角色的用户列表
     */
    List<Long> getRoleUsers(Long roleId);

    // ========== 角色权限关联 ==========

    /**
     * 为角色分配权限
     */
    boolean assignPermissionToRole(Long roleId, Long permissionId);

    /**
     * 移除角色的权限
     */
    boolean removePermissionFromRole(Long roleId, Long permissionId);

    /**
     * 批量为角色分配权限
     */
    boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色的权限列表
     */
    List<Permission> getRolePermissions(Long roleId);

    /**
     * 获取用户的权限列表
     */
    List<Permission> getUserPermissions(Long userId);

    /**
     * 检查用户是否有权限
     */
    boolean hasPermission(Long userId, String permissionCode);

    /**
     * 检查用户是否有角色
     */
    boolean hasRole(Long userId, String roleCode);
}