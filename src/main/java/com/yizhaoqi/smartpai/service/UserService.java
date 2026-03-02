package com.yizhaoqi.smartpai.service;

import com.yizhaoqi.smartpai.exception.CustomException;
import com.yizhaoqi.smartpai.model.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yizhaoqi.smartpai.mapper.UserMapper;
import com.yizhaoqi.smartpai.utils.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

/**
 * UserService 类用于处理用户注册和认证相关的业务逻辑。
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserMapper userMapper;

    /**
     * 注册新用户。
     *
     * @param username 要注册的用户名
     * @param password 要注册的用户密码
     * @throws CustomException 如果用户名已存在，则抛出异常
     */
    @Transactional
    public void registerUser(String username, String password) {
        // 检查数据库中是否已存在该用户名
        if (Optional.ofNullable(userMapper.selectByUsername(username)).isPresent()) {
            // 若用户名已存在，抛出自定义异常，状态码为 400 Bad Request
            throw new CustomException("Username already exists", HttpStatus.BAD_REQUEST);
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encode(password));
        user.setRole(User.Role.USER);
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setUpdatedAt(java.time.LocalDateTime.now());
        userMapper.insert(user);
        logger.info("User registered successfully: {}", username);
    }
    
    /**
     * 如果系统中没有管理员用户，则创建一个系统管理员
     */
    private User createSystemAdminIfNotExists() {
        String systemAdminUsername = "system_admin";
        
        return Optional.ofNullable(userMapper.selectByUsername(systemAdminUsername))
                .orElseGet(() -> {
                    logger.info("Creating system admin user");
                    User systemAdmin = new User();
                    systemAdmin.setUsername(systemAdminUsername);
                    // 生成随机密码
                    String randomPassword = generateRandomPassword();
                    systemAdmin.setPassword(PasswordUtil.encode(randomPassword));
                    systemAdmin.setRole(User.Role.ADMIN);
                    systemAdmin.setCreatedAt(java.time.LocalDateTime.now());
                    systemAdmin.setUpdatedAt(java.time.LocalDateTime.now());

                    logger.info("System admin created with password: {}", randomPassword);
                    userMapper.insert(systemAdmin);
                    return systemAdmin;
                });
    }
    
    /**
     * 生成随机密码
     */
    private String generateRandomPassword() {
        // 生成16位随机密码
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 创建管理员用户。
     *
     * @param username 要注册的管理员用户名
     * @param password 要注册的管理员密码
     * @param creatorUsername 创建者的用户名（必须是已存在的管理员）
     * @throws CustomException 如果用户名已存在或创建者不是管理员，则抛出异常
     */
    public void createAdminUser(String username, String password, String creatorUsername) {
        // 验证创建者是否为管理员
        User creator = Optional.ofNullable(userMapper.selectByUsername(creatorUsername))
                .orElseThrow(() -> new CustomException("Creator not found", HttpStatus.NOT_FOUND));
        
        if (creator.getRole() != User.Role.ADMIN) {
            throw new CustomException("Only administrators can create admin accounts", HttpStatus.FORBIDDEN);
        }
        
        // 检查数据库中是否已存在该用户名
        if (Optional.ofNullable(userMapper.selectByUsername(username)).isPresent()) {
            throw new CustomException("Username already exists", HttpStatus.BAD_REQUEST);
        }
        
        User adminUser = new User();
        adminUser.setUsername(username);
        adminUser.setPassword(PasswordUtil.encode(password));
        adminUser.setRole(User.Role.ADMIN);
        userMapper.insert(adminUser);
    }

    /**
     * 对用户进行认证。
     *
     * @param username 要认证的用户名
     * @param password 要认证的用户密码
     * @return 认证成功后返回用户的用户名
     * @throws CustomException 如果用户名或密码无效，则抛出异常
     */
    public String authenticateUser(String username, String password) {
        User user = Optional.ofNullable(userMapper.selectByUsername(username))
                .orElseThrow(() -> new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED));
        // 比较输入的密码和数据库中存储的加密密码是否匹配
        if (!PasswordUtil.matches(password, user.getPassword())) {
            // 若不匹配，抛出自定义异常，状态码为 401 Unauthorized
            throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
        // 认证成功，返回用户的用户名
        return user.getUsername();
    }
    
    /**
     * 获取用户列表，支持分页和过滤（仅关键词、状态，不再按组织过滤）
     */
    public Map<String, Object> getUserList(String keyword, Integer status, int page, int size) {
        int pageIndex = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("createdAt").descending());
        IPage<User> mpPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex + 1, pageable.getPageSize());
        userMapper.selectPage(mpPage, null);
        List<User> filtered = mpPage.getRecords().stream()
                .filter(user -> {
                    if (keyword != null && !keyword.isEmpty() && !user.getUsername().contains(keyword)) {
                        return false;
                    }
                    if (status != null && user.getRole() != (status == 1 ? User.Role.USER : User.Role.ADMIN)) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<User> pageContent = start < end ? filtered.subList(start, end) : Collections.emptyList();
        Page<User> userPage = new PageImpl<>(pageContent, pageable, filtered.size());
        List<Map<String, Object>> userList = userPage.getContent().stream()
                .map(user -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("userId", user.getId());
                    m.put("username", user.getUsername());
                    m.put("status", user.getRole() == User.Role.USER ? 1 : 0);
                    m.put("createdAt", user.getCreatedAt());
                    return m;
                })
                .collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("content", userList);
        result.put("totalElements", userPage.getTotalElements());
        result.put("totalPages", userPage.getTotalPages());
        result.put("size", userPage.getSize());
        result.put("number", userPage.getNumber() + 1);
        return result;
    }
}

