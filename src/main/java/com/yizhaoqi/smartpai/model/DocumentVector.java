package com.yizhaoqi.smartpai.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文档向量实体类（MyBatis-Plus）
 * 用于存储文本分块和相关元数据
 */
@Data
@TableName("document_vectors")
public class DocumentVector {
    @TableId(value = "vector_id", type = IdType.AUTO)
    private Long vectorId;

    private String fileMd5;
    private Integer chunkId;
    private String textContent;
    private String modelVersion;

    /**
     * 上传用户ID
     */
    private String userId;

    /**
     * 文件所属组织标签
     */
    private String orgTag;

    /**
     * 文件是否公开
     */
    private boolean isPublic = false;
}
