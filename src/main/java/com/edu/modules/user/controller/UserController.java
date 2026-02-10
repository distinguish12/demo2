package com.edu.modules.user.controller;

import com.edu.common.result.Result;
import com.edu.modules.user.entity.User;
import com.edu.modules.user.service.UserService;
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
 * 用户管理控制器
 */
@Slf4j
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("获取用户列表")
    @GetMapping
    public Result<List<User>> getUsers(
            @ApiParam("角色：1-学生，2-讲师，3-管理员") @RequestParam(required = false) Integer role,
            @ApiParam("状态：0-禁用，1-正常") @RequestParam(required = false) Integer status,
            @ApiParam("关键词（用户名/昵称）") @RequestParam(required = false) String keyword) {
        
        List<User> users;
        if (role != null) {
            users = userService.lambdaQuery().eq(User::getRole, role).list();
        } else if (status != null) {
            users = userService.lambdaQuery().eq(User::getStatus, status).list();
        } else if (keyword != null && !keyword.isEmpty()) {
            users = userService.lambdaQuery()
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword)
                    .list();
        } else {
            users = userService.list();
        }
        
        // 清空密码字段
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    public Result<User> getUser(@ApiParam("用户ID") @PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        // 清空密码字段
        user.setPassword(null);
        return Result.success(user);
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/{id}")
    public Result<String> updateUser(
            @ApiParam("用户ID") @PathVariable Long id,
            @ApiParam("昵称") @RequestParam(required = false) String nickname,
            @ApiParam("邮箱") @RequestParam(required = false) String email,
            @ApiParam("手机号") @RequestParam(required = false) String phone,
            @ApiParam("头像URL") @RequestParam(required = false) String avatar,
            @ApiParam("性别：0-未知，1-男，2-女") @RequestParam(required = false) Integer gender,
            @ApiParam("生日") @RequestParam(required = false) String birthday) {

        User user = new User();
        user.setId(id);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAvatar(avatar);
        user.setGender(gender);
        // birthday 解析为 LocalDateTime 在 Service 层处理

        boolean success = userService.updateUser(user);
        return success ? Result.success("用户信息更新成功") : Result.fail("用户信息更新失败");
    }

    @ApiOperation("修改密码")
    @PutMapping("/{id}/password")
    public Result<String> changePassword(
            @ApiParam("用户ID") @PathVariable Long id,
            @ApiParam("旧密码") @NotBlank(message = "旧密码不能为空") @RequestParam String oldPassword,
            @ApiParam("新密码") @NotBlank(message = "新密码不能为空") @RequestParam String newPassword) {

        boolean success = userService.changePassword(id, oldPassword, newPassword);
        return success ? Result.success("密码修改成功") : Result.fail("密码修改失败，请检查旧密码是否正确");
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@ApiParam("用户ID") @PathVariable Long id) {
        boolean success = userService.removeById(id);
        return success ? Result.success("用户删除成功") : Result.fail("用户删除失败");
    }

    @ApiOperation("更新用户状态")
    @PutMapping("/{id}/status")
    public Result<String> updateUserStatus(
            @ApiParam("用户ID") @PathVariable Long id,
            @ApiParam("状态：0-禁用，1-正常") @NotNull(message = "状态不能为空") @RequestParam Integer status) {

        User user = new User();
        user.setId(id);
        user.setStatus(status);
        
        boolean success = userService.updateById(user);
        return success ? Result.success("用户状态更新成功") : Result.fail("用户状态更新失败");
    }

    @ApiOperation("获取个人资料")
    @GetMapping("/profile")
    public Result<User> getProfile() {
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        // 清空密码字段
        user.setPassword(null);
        return Result.success(user);
    }

    @ApiOperation("更新个人资料")
    @PutMapping("/profile")
    public Result<String> updateProfile(
            @ApiParam("昵称") @RequestParam(required = false) String nickname,
            @ApiParam("邮箱") @RequestParam(required = false) String email,
            @ApiParam("手机号") @RequestParam(required = false) String phone,
            @ApiParam("头像URL") @RequestParam(required = false) String avatar,
            @ApiParam("性别：0-未知，1-男，2-女") @RequestParam(required = false) Integer gender,
            @ApiParam("生日") @RequestParam(required = false) String birthday) {

        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId",
                org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAvatar(avatar);
        user.setGender(gender);

        boolean success = userService.updateUser(user);
        return success ? Result.success("个人资料更新成功") : Result.fail("个人资料更新失败");
    }
}
