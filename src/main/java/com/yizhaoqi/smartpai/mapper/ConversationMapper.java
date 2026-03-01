package com.yizhaoqi.smartpai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhaoqi.smartpai.model.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话记录表 conversations 的 MyBatis-Plus Mapper
 * 提供按 userId、时间范围查询等，详见 ConversationMapper.xml
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

    List<Conversation> selectByUserIdAndTimestampBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Conversation> selectByUserId(@Param("userId") Long userId);

    List<Conversation> selectByTimestampBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
