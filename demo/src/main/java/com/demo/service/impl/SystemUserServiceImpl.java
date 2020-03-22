package com.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.entity.SystemUser;
import com.demo.dao.SystemUserMapper;
import com.demo.service.SystemUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 后台系统用户表 服务实现类
 * </p>
 *
 * @author ZhaoHang
 * @since 2020-03-12
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Override
    public IPage<SystemUser> selectUserInfoList(Page<SystemUser> page, Map<?, ?> params) {
        return systemUserMapper.selectUserInfoList(page, params);
    }
}
