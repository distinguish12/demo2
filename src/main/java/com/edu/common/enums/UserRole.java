package com.edu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
@AllArgsConstructor
public enum UserRole {
    STUDENT(1, "学生"),
    INSTRUCTOR(2, "讲师"),
    ADMIN(3, "管理员");

    private final Integer code;
    private final String desc;

    public static UserRole getByCode(Integer code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }
}