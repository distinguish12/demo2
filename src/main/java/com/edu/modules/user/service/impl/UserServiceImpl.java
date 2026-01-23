package com.edu.modules.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.user.entity.User;
import com.edu.modules.user.mapper.UserMapper;
import com.edu.modules.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public User register(String username, String password, String nickname, Integer role) {
        // 参数校验
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户名和密码不能为空");
        }

        // 检查用户名是否已存在
        User existingUser = findByUsername(username);
        if (existingUser != null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setNickname(StringUtils.hasText(nickname) ? nickname : username);
        user.setRole(role != null ? role : 1); // 默认学生角色
        user.setStatus(1); // 默认正常状态

        boolean success = save(user);
        if (!success) {
            throw new BusinessException("注册失败");
        }

        log.info("用户注册成功: username={}, id={}", username, user.getId());
        return user;
    }

    @Override
    public User login(String username, String password) {
        // 参数校验
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户名和密码不能为空");
        }

        // 查找用户
        User user = findByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        // 验证密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "密码错误");
        }

        log.info("用户登录成功: username={}, id={}", username, user.getId());
        return user;
    }

    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        return updateById(user);
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return false;
        }

        User user = getById(userId);
        if (user == null) {
            return false;
        }

        // 验证旧密码
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "旧密码错误");
        }

        // 更新新密码
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        return updateById(user);
    }
}