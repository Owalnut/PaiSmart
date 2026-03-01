package com.yizhaoqi.smartpai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhaoqi.smartpai.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表 users 的 MyBatis-Plus Mapper
 * 提供按用户名查询等，详见 UserMapper.xml
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByUsername(@Param("username") String username);
}
