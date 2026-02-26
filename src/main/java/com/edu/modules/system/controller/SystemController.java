package com.edu.modules.system.controller;

import com.edu.common.result.Result;
import com.edu.modules.system.entity.OperationLog;
import com.edu.modules.system.entity.Permission;
import com.edu.modules.system.entity.Role;
import com.edu.modules.system.service.OperationLogService;
import com.edu.modules.system.service.RolePermissionService;
import com.edu.modules.system.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 系统管理控制器
 */
@Slf4j
@Api(tags = "系统管理")
@RestController
@RequestMapping("/api/system")
@Validated
public class SystemController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private RolePermissionService rolePermissionService;

    // ========== 系统统计 ==========

    @ApiOperation("获取系统概览统计")
    @GetMapping("/overview")
    public Result<StatisticsService.SystemOverviewStats> getSystemOverview() {
        StatisticsService.SystemOverviewStats stats = statisticsService.getSystemOverview();
        return Result.success(stats);
    }

    @ApiOperation("获取用户统计")
    @GetMapping("/user-stats")
    public Result<StatisticsService.UserStats> getUserStats() {
        StatisticsService.UserStats stats = statisticsService.getUserStats();
        return Result.success(stats);
    }

    @ApiOperation("获取课程统计")
    @GetMapping("/course-stats")
    public Result<StatisticsService.CourseStats> getCourseStats() {
        StatisticsService.CourseStats stats = statisticsService.getCourseStats();
        return Result.success(stats);
    }

    @ApiOperation("获取学习统计")
    @GetMapping("/learning-stats")
    public Result<StatisticsService.LearningStats> getLearningStats() {
        StatisticsService.LearningStats stats = statisticsService.getLearningStats();
        return Result.success(stats);
    }

    @ApiOperation("获取访问统计")
    @GetMapping("/access-stats")
    public Result<StatisticsService.AccessStats> getAccessStats() {
        StatisticsService.AccessStats stats = statisticsService.getAccessStats();
        return Result.success(stats);
    }

    // ========== 操作日志 ==========

    @ApiOperation("获取操作日志列表")
    @GetMapping("/logs")
    public Result<List<OperationLog>> getLogs(
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("操作类型") @RequestParam(required = false) String operation,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "20") Integer size) {

        List<OperationLog> logs;
        if (userId != null) {
            logs = operationLogService.getLogsByUser(userId);
        } else if (operation != null && !operation.isEmpty()) {
            logs = operationLogService.getLogsByOperation(operation);
        } else {
            logs = operationLogService.list();
        }
        return Result.success(logs);
    }

    @ApiOperation("清理过期日志")
    @DeleteMapping("/logs")
    public Result<String> cleanLogs(
            @ApiParam("保留天数") @RequestParam(defaultValue = "30") Integer days) {
        int count = operationLogService.cleanExpiredLogs(days);
        return Result.success("清理完成，共删除 " + count + " 条日志");
    }

    // ========== 角色管理 ==========

    @ApiOperation("获取角色列表")
    @GetMapping("/roles")
    public Result<List<Role>> getRoles() {
        List<Role> roles = rolePermissionService.getAllRoles();
        return Result.success(roles);
    }

    @ApiOperation("创建角色")
    @PostMapping("/roles")
    public Result<Role> createRole(
            @ApiParam("角色名称") @NotBlank(message = "角色名称不能为空") @RequestParam String roleName,
            @ApiParam("角色编码") @NotBlank(message = "角色编码不能为空") @RequestParam String roleCode,
            @ApiParam("角色描述") @RequestParam(required = false) String description) {

        Role role = new Role();
        role.setRoleName(roleName);
        role.setRoleCode(roleCode);
        role.setDescription(description);
        role.setStatus(1);

        Role result = rolePermissionService.createRole(role);
        return Result.success("角色创建成功", result);
    }

    @ApiOperation("更新角色")
    @PutMapping("/roles/{id}")
    public Result<String> updateRole(
            @ApiParam("角色ID") @PathVariable Long id,
            @ApiParam("角色名称") @RequestParam(required = false) String roleName,
            @ApiParam("角色描述") @RequestParam(required = false) String description,
            @ApiParam("状态：0-禁用，1-启用") @RequestParam(required = false) Integer status) {

        Role role = new Role();
        role.setId(id);
        role.setRoleName(roleName);
        role.setDescription(description);
        role.setStatus(status);

        boolean success = rolePermissionService.updateRole(role);
        return success ? Result.success("角色更新成功") : Result.fail("角色更新失败");
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/roles/{id}")
    public Result<String> deleteRole(@ApiParam("角色ID") @PathVariable Long id) {
        boolean success = rolePermissionService.deleteRole(id);
        return success ? Result.success("角色删除成功") : Result.fail("角色删除失败");
    }

    @ApiOperation("获取用户的角色列表")
    @GetMapping("/users/{userId}/roles")
    public Result<List<Role>> getUserRoles(@ApiParam("用户ID") @PathVariable Long userId) {
        List<Role> roles = rolePermissionService.getUserRoles(userId);
        return Result.success(roles);
    }

    @ApiOperation("为用户分配角色")
    @PostMapping("/users/{userId}/roles")
    public Result<String> assignRoleToUser(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("角色ID") @NotNull(message = "角色ID不能为空") @RequestParam Long roleId) {

        boolean success = rolePermissionService.assignRoleToUser(userId, roleId);
        return success ? Result.success("角色分配成功") : Result.fail("角色分配失败");
    }

    @ApiOperation("移除用户的角色")
    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public Result<String> removeRoleFromUser(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("角色ID") @PathVariable Long roleId) {

        boolean success = rolePermissionService.removeRoleFromUser(userId, roleId);
        return success ? Result.success("角色移除成功") : Result.fail("角色移除失败");
    }

    // ========== 权限管理 ==========

    @ApiOperation("获取权限列表")
    @GetMapping("/permissions")
    public Result<List<Permission>> getPermissions() {
        List<Permission> permissions = rolePermissionService.getAllPermissions();
        return Result.success(permissions);
    }

    @ApiOperation("创建权限")
    @PostMapping("/permissions")
    public Result<Permission> createPermission(
            @ApiParam("权限名称") @NotBlank(message = "权限名称不能为空") @RequestParam String permName,
            @ApiParam("权限编码") @NotBlank(message = "权限编码不能为空") @RequestParam String permCode,
            @ApiParam("资源路径") @RequestParam(required = false) String resource,
            @ApiParam("请求方法") @RequestParam(required = false) String method,
            @ApiParam("权限描述") @RequestParam(required = false) String description) {

        Permission permission = new Permission();
        permission.setPermName(permName);
        permission.setPermCode(permCode);
        permission.setResource(resource);
        permission.setMethod(method);
        permission.setDescription(description);
        permission.setStatus(1);

        Permission result = rolePermissionService.createPermission(permission);
        return Result.success("权限创建成功", result);
    }

    @ApiOperation("更新权限")
    @PutMapping("/permissions/{id}")
    public Result<String> updatePermission(
            @ApiParam("权限ID") @PathVariable Long id,
            @ApiParam("权限名称") @RequestParam(required = false) String permName,
            @ApiParam("权限描述") @RequestParam(required = false) String description,
            @ApiParam("状态：0-禁用，1-启用") @RequestParam(required = false) Integer status) {

        Permission permission = new Permission();
        permission.setId(id);
        permission.setPermName(permName);
        permission.setDescription(description);
        permission.setStatus(status);

        boolean success = rolePermissionService.updatePermission(permission);
        return success ? Result.success("权限更新成功") : Result.fail("权限更新失败");
    }

    @ApiOperation("删除权限")
    @DeleteMapping("/permissions/{id}")
    public Result<String> deletePermission(@ApiParam("权限ID") @PathVariable Long id) {
        boolean success = rolePermissionService.deletePermission(id);
        return success ? Result.success("权限删除成功") : Result.fail("权限删除失败");
    }

    @ApiOperation("获取角色的权限列表")
    @GetMapping("/roles/{roleId}/permissions")
    public Result<List<Permission>> getRolePermissions(@ApiParam("角色ID") @PathVariable Long roleId) {
        List<Permission> permissions = rolePermissionService.getRolePermissions(roleId);
        return Result.success(permissions);
    }

    @ApiOperation("为角色分配权限")
    @PostMapping("/roles/{roleId}/permissions")
    public Result<String> assignPermissionToRole(
            @ApiParam("角色ID") @PathVariable Long roleId,
            @ApiParam("权限ID") @NotNull(message = "权限ID不能为空") @RequestParam Long permissionId) {

        boolean success = rolePermissionService.assignPermissionToRole(roleId, permissionId);
        return success ? Result.success("权限分配成功") : Result.fail("权限分配失败");
    }

    @ApiOperation("批量为角色分配权限")
    @PostMapping("/roles/{roleId}/permissions/batch")
    public Result<String> assignPermissionsToRole(
            @ApiParam("角色ID") @PathVariable Long roleId,
            @ApiParam("权限ID列表") @RequestBody List<Long> permissionIds) {

        boolean success = rolePermissionService.assignPermissionsToRole(roleId, permissionIds);
        return success ? Result.success("权限批量分配成功") : Result.fail("权限批量分配失败");
    }

    @ApiOperation("移除角色的权限")
    @DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
    public Result<String> removePermissionFromRole(
            @ApiParam("角色ID") @PathVariable Long roleId,
            @ApiParam("权限ID") @PathVariable Long permissionId) {

        boolean success = rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return success ? Result.success("权限移除成功") : Result.fail("权限移除失败");
    }

    @ApiOperation("检查用户是否有权限")
    @GetMapping("/users/{userId}/has-permission")
    public Result<Boolean> hasPermission(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("权限编码") @NotBlank(message = "权限编码不能为空") @RequestParam String permissionCode) {

        boolean has = rolePermissionService.hasPermission(userId, permissionCode);
        return Result.success(has);
    }

    @ApiOperation("检查用户是否有角色")
    @GetMapping("/users/{userId}/has-role")
    public Result<Boolean> hasRole(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("角色编码") @NotBlank(message = "角色编码不能为空") @RequestParam String roleCode) {

        boolean has = rolePermissionService.hasRole(userId, roleCode);
        return Result.success(has);
    }
}
