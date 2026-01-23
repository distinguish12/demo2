package com.edu.modules.auth.controller;

import com.edu.common.result.Result;
import com.edu.common.utils.JwtUtils;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @ApiParam("用户名") @NotBlank(message = "用户名不能为空") @RequestParam String username,
            @ApiParam("密码") @NotBlank(message = "密码不能为空") @RequestParam String password) {

        User user = userService.login(username, password);

        // 生成token
        String token = jwtUtils.generateToken(user.getUsername(), user.getId(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", jwtUtils.getTokenHead() + " " + token);
        data.put("user", user);

        return Result.success("登录成功", data);
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<User> register(
            @ApiParam("用户名") @NotBlank(message = "用户名不能为空") @RequestParam String username,
            @ApiParam("密码") @NotBlank(message = "密码不能为空") @RequestParam String password,
            @ApiParam("昵称") @RequestParam(required = false) String nickname,
            @ApiParam("角色：1-学生，2-讲师，3-管理员") @RequestParam(defaultValue = "1") Integer role) {

        User user = userService.register(username, password, nickname, role);
        return Result.success("注册成功", user);
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<String> logout() {
        // JWT是无状态的，客户端直接删除token即可
        // 这里可以进行一些清理工作，如将token加入黑名单
        return Result.success("登出成功");
    }

    @ApiOperation("刷新Token")
    @PostMapping("/refresh")
    public Result<Map<String, String>> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            String newToken = jwtUtils.refreshToken(token.replace(jwtUtils.getTokenHead() + " ", ""));
            Map<String, String> data = new HashMap<>();
            data.put("token", jwtUtils.getTokenHead() + " " + newToken);
            return Result.success("刷新成功", data);
        } catch (Exception e) {
            log.error("刷新Token失败", e);
            return Result.fail("Token无效或已过期");
        }
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getCurrentUser() {
        // 从请求属性中获取用户信息（由JWT过滤器设置）
        Long userId = (Long) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes().getAttribute("userId", org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);

        if (userId == null) {
            return Result.fail("用户未登录");
        }

        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        return Result.success(user);
    }
}