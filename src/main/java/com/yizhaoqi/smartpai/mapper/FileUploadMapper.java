package com.yizhaoqi.smartpai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhaoqi.smartpai.model.FileUpload;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件上传表 file_upload 的 MyBatis-Plus Mapper
 * 提供按 fileMd5、userId、组织标签等查询/删除，详见 FileUploadMapper.xml
 */
@Mapper
public interface FileUploadMapper extends BaseMapper<FileUpload> {

    FileUpload selectByFileMd5(@Param("fileMd5") String fileMd5);

    FileUpload selectByFileMd5AndUserId(@Param("fileMd5") String fileMd5, @Param("userId") String userId);

    long countByFileMd5(@Param("fileMd5") String fileMd5);

    int deleteByFileMd5(@Param("fileMd5") String fileMd5);

    int deleteByFileMd5AndUserId(@Param("fileMd5") String fileMd5, @Param("userId") String userId);

    List<FileUpload> selectByUserIdOrIsPublicTrue(@Param("userId") String userId);

    List<FileUpload> selectAccessibleFilesWithTags(@Param("userId") String userId, @Param("orgTagList") List<String> orgTagList);

    List<FileUpload> selectByUserId(@Param("userId") String userId);

    List<FileUpload> selectByFileMd5In(@Param("md5List") List<String> md5List);
}
