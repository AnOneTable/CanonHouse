package com.demo.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 后台系统用户表
 * </p>
 *
 * @author ZhaoHang
 * @since 2020-03-12
 */
@EqualsAndHashCode(callSuper = true)
@TableName("system_user")
@Data
public class SystemUser extends Model<SystemUser> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 登录手机号
     */
    private String userPhone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 密码
     */
    private String password;

    /**
     * 加密盐
     */
    private String salt;

    /**
     * 创建时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 创建用户ID
     */
    private Integer createUser;

    /**
     * 修改时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 修改用户ID
     */
    private Integer updateUser;

    /**
     * 是否锁定 0 正常 1 锁定
     */
    private Integer isLock;

    /**
     * 上次登录IP
     */
    private String lastLoginIp;

    /**
     * 上次登录时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    /**
     * 用户状态 0 正常 1 删除
     */
    private Integer userStatus;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
