package com.yizhaoqi.smartpai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** 组织标签初始化器（已禁用，仅个人/公开模式） */
@Component
@Order(2) // 设置优先级，确保在管理员账号初始化器之后运行
public class OrgTagInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(OrgTagInitializer.class);

    @Override
    public void run(String... args) throws Exception {
        // 已移除组织标签逻辑，仅保留个人/公开可见性
        logger.info("组织标签初始化已禁用（仅个人/公开模式）");
    }
} 