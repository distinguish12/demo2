package com.edu.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.modules.system.entity.OperationLog;
import com.edu.modules.system.mapper.OperationLogMapper;
import com.edu.modules.system.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志服务实现
 */
@Slf4j
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog>
        implements OperationLogService {

    @Override
    @Async
    public void saveAsync(OperationLog operationLog) {
        try {
            save(operationLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }

    @Override
    public List<OperationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(OperationLog::getCreateTime, startTime, endTime)
               .orderByDesc(OperationLog::getCreateTime);

        return list(wrapper);
    }

    @Override
    public List<OperationLog> getLogsByUser(Long userId) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getUserId, userId)
               .orderByDesc(OperationLog::getCreateTime);

        return list(wrapper);
    }

    @Override
    public List<OperationLog> getLogsByOperation(String operation) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getOperation, operation)
               .orderByDesc(OperationLog::getCreateTime);

        return list(wrapper);
    }

    @Override
    public int cleanExpiredLogs(int days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(OperationLog::getCreateTime, expireTime);

        boolean result = remove(wrapper);
        return result ? 1 : 0; // 简化处理，实际应该返回删除的数量
    }
}