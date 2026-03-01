package com.yizhaoqi.smartpai.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件上传实体类（MyBatis-Plus）
 * 用于表示文件上传的相关信息
 */
@Data
@TableName("file_upload")
public class FileUpload {
    /**
     * 文件的唯一标识符，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件的MD5值，用于唯一确定一个文件
     */
    private String fileMd5;

    /**
     * 文件的原始名称，用于记录上传时文件的名称
     */
    private String fileName;

    /**
     * 文件的总大小，以字节为单位记录文件的大小
     */
    private long totalSize;

    /**
     * 文件上传的状态：0-上传中，1-已完成
     */
    private int status;

    /**
     * 上传文件的用户的标识符，用于记录哪个用户上传了文件
     */
    private String userId;

    /**
     * 文件所属组织标签，用于标识文件归属的组织，支持基于组织标签的权限控制
     */
    @TableField(value = "org_tag")
    private String orgTag;

    /**
     * 文件是否公开：true表示所有用户可访问，false表示仅组织内用户可访问
     */
    private boolean isPublic = false;

    /**
     * 文件上传的创建时间，自动记录文件上传开始的时间
     */
    private LocalDateTime createdAt;

    /**
     * 文件合并完成的时间，当文件上传状态为已完成时，自动记录完成的时间
     */
    private LocalDateTime mergedAt;
}
