package com.yizhaoqi.smartpai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhaoqi.smartpai.model.DocumentVector;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档向量表 document_vectors 的 MyBatis-Plus Mapper
 * 提供按 fileMd5 查询/删除分块等，详见 DocumentVectorMapper.xml
 */
@Mapper
public interface DocumentVectorMapper extends BaseMapper<DocumentVector> {

    List<DocumentVector> selectByFileMd5(@Param("fileMd5") String fileMd5);

    int deleteByFileMd5(@Param("fileMd5") String fileMd5);
}
