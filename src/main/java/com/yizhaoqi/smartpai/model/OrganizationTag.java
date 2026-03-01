package com.yizhaoqi.smartpai.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 组织标签实体（MyBatis-Plus）
 * created_by 存创建者用户主键，不再关联 User 对象
 */
@Data
@TableName("organization_tags")
public class OrganizationTag {
    /**
     * 标签唯一标识
     */
    @TableId("tag_id")
    private String tagId;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 父标签ID
     */
    private String parentTag;

    /**
     * 创建者，关联 users.id
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
