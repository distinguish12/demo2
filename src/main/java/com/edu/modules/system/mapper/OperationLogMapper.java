package com.edu.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.system.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}