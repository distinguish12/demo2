package com.edu.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Pointcut("execution(* com.edu.modules.*.controller.*.*(..))")
    public void operationLog() {}

    @Before("operationLog()")
    public void doBefore(JoinPoint joinPoint) {



        // 这里可以添加前置处理逻辑



    }

    @AfterReturning(pointcut = "operationLog()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        saveOperationLog(joinPoint, null, result);
    }

    @AfterThrowing(pointcut = "operationLog()", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, Exception exception) {
        saveOperationLog(joinPoint, exception, null);
    }

    private void saveOperationLog(JoinPoint joinPoint, Exception exception, Object result) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();

            // 获取用户信息
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");

            if (username == null) {
                return; // 未登录用户不记录日志
            }

            // 记录操作日志（暂时只输出到控制台，后续完善）
            log.info("操作日志: 用户={}, 操作={}, 方法={}, 参数={}, IP={}, 结果={}",
                    username,
                    joinPoint.getSignature().getName(),
                    request.getMethod(),
                    Arrays.toString(joinPoint.getArgs()),
                    getClientIp(request),
                    exception == null ? "成功" : "失败: " + exception.getMessage());

        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}