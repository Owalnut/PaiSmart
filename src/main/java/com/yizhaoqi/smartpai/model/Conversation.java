package com.yizhaoqi.smartpai.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对话记录实体（MyBatis-Plus）
 * user_id 存用户主键，不再关联 User 对象
 */
@Data
@TableName("conversations")
public class Conversation {
    /**
     * 对话记录唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户，存 users.id
     */
    private Long userId;

    /**
     * 用户提问内容
     */
    private String question;

    /**
     * 系统回答内容
     */
    private String answer;

    /**
     * 对话时间戳
     */
    private LocalDateTime timestamp;
}
