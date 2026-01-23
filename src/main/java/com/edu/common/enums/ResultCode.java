package com.edu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    VALIDATION_ERROR(400, "参数验证失败");

    private final Integer code;
    private final String message;
}