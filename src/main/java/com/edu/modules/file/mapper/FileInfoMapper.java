package com.edu.modules.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.file.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件信息Mapper
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
}