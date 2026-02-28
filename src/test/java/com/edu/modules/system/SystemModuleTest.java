package com.edu.modules.system;

import com.edu.modules.system.entity.OperationLog;
import com.edu.modules.system.entity.Permission;
import com.edu.modules.system.entity.Role;
import com.edu.modules.system.service.OperationLogService;
import com.edu.modules.system.service.RolePermissionService;
import com.edu.modules.system.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 系统管理模块测试
 */
@SpringBootTest
public class SystemModuleTest {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 测试操作日志功能
     */
    @Test
    public void testOperationLog() {
        // 创建操作日志
        OperationLog log = new OperationLog();
        log.setUserId(1L);
        log.setUsername("admin");
        log.setOperation("登录");
        log.setMethod("POST");
        log.setParams("username=admin");
        log.setIp("127.0.0.1");
        log.setLocation("北京市");
        log.setDuration(100L);
        log.setStatus(1);

        boolean saveResult = operationLogService.save(log);
        System.out.println("操作日志保存结果: " + saveResult);

        // 查询操作日志
        List<OperationLog> logs = operationLogService.list();
        System.out.println("操作日志总数: " + logs.size());

        if (!logs.isEmpty()) {
            System.out.println("最新操作: " + logs.get(0).getOperation() + " by " + logs.get(0).getUsername());
        }

        // 按时间范围查询
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        List<OperationLog> timeRangeLogs = operationLogService.getLogsByTimeRange(startTime, endTime);
        System.out.println("24小时内操作日志数: " + timeRangeLogs.size());

        // 按用户查询
        List<OperationLog> userLogs = operationLogService.getLogsByUser(1L);
        System.out.println("用户1的操作日志数: " + userLogs.size());
    }

    /**
     * 测试角色权限管理
     */
    @Test
    public void testRolePermissionManagement() {
        // 创建角色
        Role role = new Role();
        role.setRoleName("测试角色");
        role.setRoleCode("TEST_ROLE");
        role.setDescription("用于测试的角色");
        role.setStatus(1);

        Role createdRole = rolePermissionService.createRole(role);
        System.out.println("角色创建成功: " + createdRole.getRoleName());

        // 创建权限
        Permission permission = new Permission();
        permission.setPermName("测试权限");
        permission.setPermCode("TEST_PERMISSION");
        permission.setResource("/api/test");
        permission.setMethod("GET");
        permission.setDescription("测试权限描述");
        permission.setStatus(1);

        Permission createdPermission = rolePermissionService.createPermission(permission);
        System.out.println("权限创建成功: " + createdPermission.getPermName());

        // 为角色分配权限
        boolean assignResult = rolePermissionService.assignPermissionToRole(createdRole.getId(),
                createdPermission.getId());
        System.out.println("权限分配结果: " + assignResult);

        // 查询角色的权限
        List<Permission> rolePermissions = rolePermissionService.getRolePermissions(createdRole.getId());
        System.out.println("角色权限数量: " + rolePermissions.size());

        // 为用户分配角色
        boolean assignRoleResult = rolePermissionService.assignRoleToUser(1L, createdRole.getId());
        System.out.println("用户角色分配结果: " + assignRoleResult);

        // 查询用户的权限
        List<Permission> userPermissions = rolePermissionService.getUserPermissions(1L);
        System.out.println("用户权限数量: " + userPermissions.size());

        // 检查用户权限
        boolean hasPermission = rolePermissionService.hasPermission(1L, "TEST_PERMISSION");
        System.out.println("用户是否有测试权限: " + hasPermission);

        // 检查用户角色
        boolean hasRole = rolePermissionService.hasRole(1L, "TEST_ROLE");
        System.out.println("用户是否有测试角色: " + hasRole);

        // 清理测试数据
        rolePermissionService.removePermissionFromRole(createdRole.getId(), createdPermission.getId());
        rolePermissionService.removeRoleFromUser(1L, createdRole.getId());
        rolePermissionService.deletePermission(createdPermission.getId());
        rolePermissionService.deleteRole(createdRole.getId());
    }

    /**
     * 测试数据统计功能
     */
    @Test
    public void testStatistics() {
        // 获取系统概览统计
        StatisticsService.SystemOverviewStats overview = statisticsService.getSystemOverview();
        System.out.println("=== 系统概览统计 ===");
        System.out.println("总用户数: " + overview.getTotalUsers());
        System.out.println("总课程数: " + overview.getTotalCourses());
        System.out.println("总选课数: " + overview.getTotalEnrollments());
        System.out.println("总访问量: " + overview.getTotalViews());

        // 获取用户统计
        StatisticsService.UserStats userStats = statisticsService.getUserStats();
        System.out.println("=== 用户统计 ===");
        System.out.println("总用户数: " + userStats.getTotalUsers());
        System.out.println("活跃用户数: " + userStats.getActiveUsers());
        System.out.println("今日新增用户: " + userStats.getNewUsersToday());
        System.out.println("本周新增用户: " + userStats.getNewUsersThisWeek());
        System.out.println("本月新增用户: " + userStats.getNewUsersThisMonth());

        // 获取课程统计
        StatisticsService.CourseStats courseStats = statisticsService.getCourseStats();
        System.out.println("=== 课程统计 ===");
        System.out.println("总课程数: " + courseStats.getTotalCourses());
        System.out.println("已发布课程数: " + courseStats.getPublishedCourses());
        System.out.println("总选课数: " + courseStats.getTotalEnrollments());
        System.out.println("总课时数: " + courseStats.getTotalLessons());
        System.out.println("平均评分: " + String.format("%.2f", courseStats.getAverageRating()));

        // 获取学习统计
        StatisticsService.LearningStats learningStats = statisticsService.getLearningStats();
        System.out.println("=== 学习统计 ===");
        System.out.println("总学习记录数: " + learningStats.getTotalLearningRecords());
        System.out.println("已完成课时数: " + learningStats.getCompletedLessons());
        System.out.println("完成率: " + String.format("%.2f%%", learningStats.getCompletionRate() * 100));
        System.out.println("总观看时长: " + learningStats.getTotalWatchTime() + "秒");
        System.out.println("平均观看时长: " + learningStats.getAverageWatchTime() + "秒");

        // 获取访问统计
        StatisticsService.AccessStats accessStats = statisticsService.getAccessStats();
        System.out.println("=== 访问统计 ===");
        System.out.println("总访问量: " + accessStats.getTotalVisits());
        System.out.println("今日访问量: " + accessStats.getTodayVisits());
        System.out.println("本周访问量: " + accessStats.getWeekVisits());
        System.out.println("本月访问量: " + accessStats.getMonthVisits());
        System.out.println("总页面浏览量: " + accessStats.getTotalPageViews());
    }

    /**
     * 测试权限检查功能
     */
    @Test
    public void testPermissionCheck() {
        // 检查管理员权限
        boolean adminHasPermission = rolePermissionService.hasPermission(1L, "ADMIN_ALL");
        System.out.println("管理员是否有全部权限: " + adminHasPermission);

        // 检查普通用户权限
        boolean userHasPermission = rolePermissionService.hasPermission(2L, "COURSE_VIEW");
        System.out.println("普通用户是否有查看课程权限: " + userHasPermission);

        // 检查角色
        boolean isAdmin = rolePermissionService.hasRole(1L, "ADMIN");
        boolean isStudent = rolePermissionService.hasRole(2L, "STUDENT");

        System.out.println("用户1是管理员: " + isAdmin);
        System.out.println("用户2是学生: " + isStudent);
    }

    /**
     * 测试批量权限分配
     */
    @Test
    public void testBatchPermissionAssignment() {
        // 创建测试角色
        Role role = new Role();
        role.setRoleName("批量测试角色");
        role.setRoleCode("BATCH_TEST_ROLE");
        role.setDescription("用于批量权限测试");
        Role createdRole = rolePermissionService.createRole(role);

        // 创建多个权限
        Permission perm1 = new Permission();
        perm1.setPermName("权限1");
        perm1.setPermCode("PERM_1");
        perm1.setResource("/api/test1");
        perm1.setMethod("GET");
        perm1.setStatus(1);
        Permission createdPerm1 = rolePermissionService.createPermission(perm1);

        Permission perm2 = new Permission();
        perm2.setPermName("权限2");
        perm2.setPermCode("PERM_2");
        perm2.setResource("/api/test2");
        perm2.setMethod("POST");
        perm2.setStatus(1);
        Permission createdPerm2 = rolePermissionService.createPermission(perm2);

        // 批量分配权限
        List<Long> permissionIds = Arrays.asList(createdPerm1.getId(), createdPerm2.getId());
        boolean batchAssignResult = rolePermissionService.assignPermissionsToRole(createdRole.getId(), permissionIds);
        System.out.println("批量权限分配结果: " + batchAssignResult);

        // 验证权限分配
        List<Permission> rolePermissions = rolePermissionService.getRolePermissions(createdRole.getId());
        System.out.println("角色拥有的权限数量: " + rolePermissions.size());

        for (Permission perm : rolePermissions) {
            System.out.println("  - " + perm.getPermName() + " (" + perm.getPermCode() + ")");
        }

        // 清理测试数据
        rolePermissionService.removePermissionFromRole(createdRole.getId(), createdPerm1.getId());
        rolePermissionService.removePermissionFromRole(createdRole.getId(), createdPerm2.getId());
        rolePermissionService.deletePermission(createdPerm1.getId());
        rolePermissionService.deletePermission(createdPerm2.getId());
        rolePermissionService.deleteRole(createdRole.getId());
    }
}