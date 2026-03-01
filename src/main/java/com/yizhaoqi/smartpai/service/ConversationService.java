package com.yizhaoqi.smartpai.service;

import com.yizhaoqi.smartpai.exception.CustomException;
import com.yizhaoqi.smartpai.model.Conversation;
import com.yizhaoqi.smartpai.model.User;
import com.yizhaoqi.smartpai.mapper.ConversationMapper;
import com.yizhaoqi.smartpai.mapper.UserMapper;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史服务
 * 记录与查询用户与系统的问答记录，依赖 ConversationMapper、UserMapper（MyBatis-Plus）
 */
@Service
public class ConversationService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 记录用户的对话历史。
     *
     * @param username 用户名
     * @param question 用户提问内容
     * @param answer 系统回答内容
     */
    public void recordConversation(String username, String question, String answer) {
        User user = Optional.ofNullable(userMapper.selectByUsername(username))
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        Conversation conversation = new Conversation();
        conversation.setUserId(user.getId());
        conversation.setQuestion(question);
        conversation.setAnswer(answer);
        conversation.setTimestamp(LocalDateTime.now());

        conversationMapper.insert(conversation);
    }

    /**
     * 查询用户的对话历史。
     *
     * @param username 用户名
     * @param startDate 起始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 符合条件的对话记录列表
     */
    public List<Conversation> getConversations(String username, LocalDateTime startDate, LocalDateTime endDate) {
        User user = Optional.ofNullable(userMapper.selectByUsername(username))
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (user.getRole() == User.Role.ADMIN && "all".equals(username)) {
            if (startDate != null && endDate != null) {
                return conversationMapper.selectByTimestampBetween(startDate, endDate);
            } else {
                return conversationMapper.selectList(null);
            }
        } else {
            if (startDate != null && endDate != null) {
                return conversationMapper.selectByUserIdAndTimestampBetween(user.getId(), startDate, endDate);
            } else {
                return conversationMapper.selectByUserId(user.getId());
            }
        }
    }
    
    /**
     * 管理员查询所有用户的对话历史。
     *
     * @param adminUsername 管理员用户名
     * @param targetUsername 目标用户名（可选，如果提供则只查询该用户的对话历史）
     * @param startDate 起始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 符合条件的对话记录列表
     */
    public List<Conversation> getAllConversations(String adminUsername, String targetUsername, 
                                                 LocalDateTime startDate, LocalDateTime endDate) {
        User admin = Optional.ofNullable(userMapper.selectByUsername(adminUsername))
                .orElseThrow(() -> new CustomException("Admin not found", HttpStatus.NOT_FOUND));

        if (admin.getRole() != User.Role.ADMIN) {
            throw new CustomException("Unauthorized access", HttpStatus.FORBIDDEN);
        }

        if (targetUsername != null && !targetUsername.isEmpty()) {
            User targetUser = Optional.ofNullable(userMapper.selectByUsername(targetUsername))
                    .orElseThrow(() -> new CustomException("Target user not found", HttpStatus.NOT_FOUND));

            if (startDate != null && endDate != null) {
                return conversationMapper.selectByUserIdAndTimestampBetween(targetUser.getId(), startDate, endDate);
            } else {
                return conversationMapper.selectByUserId(targetUser.getId());
            }
        } else {
            if (startDate != null && endDate != null) {
                return conversationMapper.selectByTimestampBetween(startDate, endDate);
            } else {
                return conversationMapper.selectList(null);
            }
        }
    }
}