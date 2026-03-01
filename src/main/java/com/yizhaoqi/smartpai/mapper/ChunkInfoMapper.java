package com.yizhaoqi.smartpai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhaoqi.smartpai.model.ChunkInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分片信息表 chunk_info 的 MyBatis-Plus Mapper
 * 提供按 fileMd5 查询有序分片列表等，详见 ChunkInfoMapper.xml
 */
@Mapper
public interface ChunkInfoMapper extends BaseMapper<ChunkInfo> {

    List<ChunkInfo> selectByFileMd5OrderByChunkIndexAsc(@Param("fileMd5") String fileMd5);
}
