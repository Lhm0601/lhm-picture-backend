package com.lhm.lhmpicturebackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 */
@Data
public class LoginUserVo implements Serializable {
    /**
     * 用户ID：唯一标识用户
     */
    private Long id;

    /**
     * 用户账号：用户的登录账号，需要脱敏以防止泄露
     */
    private String userAccount;

    /**
     * 用户名：用户选择的显示名称，可能经过脱敏处理
     */
    private String username;

    /**
     * 用户简介：简短介绍用户的信息，不涉及敏感信息
     */
    private String userProfile;

    /**
     * 用户头像：用户的头像链接，不涉及敏感信息
     */
    private String userAvatar;

    /**
     * 用户角色：标识用户的权限级别，例如管理员、普通用户等
     */
    private String userRole;

    /**
     * 创建时间：记录用户创建的时间，用于审计和追踪
     */
    private Date createTime;

    /**
     * 更新时间：记录最后一次修改用户信息的时间，用于审计和追踪
     */
    private Date updateTime;

    /**
     * 序列化ID：用于序列化和反序列化，确保类的版本控制
     */
    private static final long serialVersionUID = 1L;
}

