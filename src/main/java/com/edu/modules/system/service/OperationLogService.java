package com.edu.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.modules.system.entity.OperationLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志服务接口
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 异步保存操作日志
     */
    void saveAsync(OperationLog operationLog);

    /**
     * 根据时间范围查询操作日志
     */
    List<OperationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据用户查询操作日志
     */
    List<OperationLog> getLogsByUser(Long userId);

    /**
     * 根据操作类型查询日志
     */
    List<OperationLog> getLogsByOperation(String operation);

    /**
     * 清理过期日志
     */
    int cleanExpiredLogs(int days);
}