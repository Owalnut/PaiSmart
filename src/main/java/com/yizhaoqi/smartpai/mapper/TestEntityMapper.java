package com.yizhaoqi.smartpai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhaoqi.smartpai.test.TestEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测试实体表 test_entity 的 MyBatis-Plus Mapper（用于事务等测试）
 */
@Mapper
public interface TestEntityMapper extends BaseMapper<TestEntity> {
}
