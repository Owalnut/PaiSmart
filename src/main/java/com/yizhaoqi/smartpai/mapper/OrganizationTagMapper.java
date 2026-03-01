package com.yizhaoqi.smartpai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhaoqi.smartpai.model.OrganizationTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 组织标签表 organization_tags 的 MyBatis-Plus Mapper
 * 提供按 tagId、parentTag 查询等，详见 OrganizationTagMapper.xml
 */
@Mapper
public interface OrganizationTagMapper extends BaseMapper<OrganizationTag> {

    OrganizationTag selectByTagId(@Param("tagId") String tagId);

    List<OrganizationTag> selectByParentTag(@Param("parentTag") String parentTag);

    int countByTagId(@Param("tagId") String tagId);
}
