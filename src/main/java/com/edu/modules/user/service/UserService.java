package com.edu.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 用户注册
     */
    User register(String username, String password, String nickname, Integer role);

    /**
     * 用户登录
     */
    User login(String username, String password);

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}