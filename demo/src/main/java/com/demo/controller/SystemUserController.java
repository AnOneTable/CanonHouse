package com.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.entity.SystemUser;
import com.demo.service.SystemUserService;
import com.demo.utils.BaseResult;
import com.demo.utils.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author ZhaoHang
 * Create By Idea.2019.2.2
 * Create Time: 2020-03-12 10:57
 */
@RestController
@RequestMapping(value = "/user")
@Slf4j
public class SystemUserController {

    @Autowired
    private SystemUserService systemUserService;

    @GetMapping(value = "/list")
    public String list(PageInfo pageInfo, String userName, String mobilePhone) {

        var br = new BaseResult();
        var page = new Page<SystemUser>(pageInfo.getPageOn(), pageInfo.getPageSize());

        var map = new HashMap<>(16);
        map.put("userName", userName);
        map.put("mobilePhone", mobilePhone);

        var iPage = systemUserService.selectUserInfoList(page, map);

        pageInfo.setDataToPageInfo(iPage);

        br.put("pageInfo", pageInfo);

        return br.toJSONString();
    }

    @PutMapping(value = "/add")
    public String add(SystemUser systemUser) {
        var br = new BaseResult();
        systemUser.setCreateTime(LocalDateTime.now());
        systemUser.setCreateUser(1);
        systemUser.insert();
        br.success();
        return br.toJSONString();
    }

    @GetMapping(value = "/get/{id}")
    public String get(@PathVariable Integer id) {
        var br = new BaseResult();
        var systemUser = systemUserService.getById(id);
        br.put("userInfo", systemUser);
        br.success();
        return br.toJSONString();
    }

    @PostMapping(value = "/update")
    public String update(SystemUser systemUser) {
        var br = new BaseResult();

        var user = systemUserService.getById(systemUser);
        if (user != null) {

            systemUser.setUpdateTime(LocalDateTime.now());
            systemUser.setUpdateUser(1);
            systemUser.updateById();
            br.success();
        } else {
            log.error("查询的数据不存在，id= {}", systemUser.getId());
            br.setErrorMsg("数据不存在");
            br.setErrorCode("10000");
        }
        return br.toJSONString();
    }

    @DeleteMapping(value = "/delete/{id}")
    public String delete(@PathVariable Integer id) {

        var br = new BaseResult();

        systemUserService.removeById(id);

        br.success();
        return br.toJSONString();

    }


}
