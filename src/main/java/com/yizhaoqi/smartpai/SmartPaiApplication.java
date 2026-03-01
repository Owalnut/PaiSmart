package com.yizhaoqi.smartpai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 派聪明应用入口
 * 扫描 MyBatis-Plus Mapper 包：com.yizhaoqi.smartpai.mapper
 */
@SpringBootApplication
@MapperScan("com.yizhaoqi.smartpai.mapper")
public class SmartPaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartPaiApplication.class, args);
    }

}
