package com.yizhaoqi.smartpai.config;

import com.yizhaoqi.smartpai.model.User;
import com.yizhaoqi.smartpai.mapper.UserMapper;
import com.yizhaoqi.smartpai.utils.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 管理员账号初始化器
 * 在应用启动时自动创建管理员账号（如果不存在）
 */
@Component
@Order(1) // 设置优先级，确保在其他初始化器之前运行
public class AdminUserInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);

    /** 用户表 Mapper，用于查询/插入管理员账号 */
    @Autowired
    private UserMapper userMapper;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        logger.info("检查管理员账号是否存在: {}", adminUsername);
        Optional<User> existingAdmin = Optional.ofNullable(userMapper.selectByUsername(adminUsername));

        if (existingAdmin.isPresent()) {
            logger.info("管理员账号 '{}' 已存在，跳过创建步骤", adminUsername);
            return;
        }

        try {
            logger.info("开始创建管理员账号: {}", adminUsername);
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(PasswordUtil.encode(adminPassword));
            adminUser.setRole(User.Role.ADMIN);
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setUpdatedAt(LocalDateTime.now());

            userMapper.insert(adminUser);
            logger.info("管理员账号 '{}' 创建成功", adminUsername);
        } catch (Exception e) {
            logger.error("创建管理员账号失败: {}", e.getMessage(), e);
            throw new RuntimeException("无法创建管理员账号", e);
        }
    }
} 