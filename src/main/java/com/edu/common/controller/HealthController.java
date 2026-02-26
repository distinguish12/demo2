package com.edu.common.controller;

import com.edu.common.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 用于服务监控和健康状态检测
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Value("${spring.application.name:edu-online}")
    private String applicationName;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 基础健康检查
     */
    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", applicationName);
        health.put("timestamp", LocalDateTime.now().format(FORMATTER));
        return Result.success(health);
    }

    /**
     * 详细健康状态
     */
    @GetMapping("/detail")
    public Result<Map<String, Object>> healthDetail() {
        Map<String, Object> detail = new HashMap<>();
        detail.put("status", "UP");
        detail.put("service", applicationName);

        // JVM 信息
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> jvm = new HashMap<>();
        jvm.put("maxMemory", formatBytes(runtime.maxMemory()));
        jvm.put("totalMemory", formatBytes(runtime.totalMemory()));
        jvm.put("freeMemory", formatBytes(runtime.freeMemory()));
        jvm.put("usedMemory", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
        jvm.put("availableProcessors", runtime.availableProcessors());
        detail.put("jvm", jvm);

        // 系统信息
        Map<String, Object> system = new HashMap<>();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        system.put("userDir", System.getProperty("user.dir"));
        detail.put("system", system);

        detail.put("timestamp", LocalDateTime.now().format(FORMATTER));
        return Result.success(detail);
    }

    /**
     * 就绪检查 - 用于K8s就绪探针
     */
    @GetMapping("/ready")
    public Result<Void> ready() {
        return Result.success();
    }

    /**
     * 存活检查 - 用于K8s存活探针
     */
    @GetMapping("/liveness")
    public Result<Void> liveness() {
        return Result.success();
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
}