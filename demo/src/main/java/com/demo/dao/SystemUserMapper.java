package com.demo.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.entity.SystemUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * 后台系统用户表 Mapper 接口
 * </p>
 *
 * @author ZhaoHang
 * @since 2020-03-12
 */
public interface SystemUserMapper extends BaseMapper<SystemUser> {

    /**
     * 分页查询用户
     *
     * @param page   page
     * @param params params
     * @return IPage
     */
    IPage<SystemUser> selectUserInfoList(Page<SystemUser> page, @Param("params") Map<?, ?> params);


}
