package com.lhm.lhmpicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;
/**
 * 用户添加请求类
 * 用于封装用户添加请求的相关信息，实现用户信息的传输和处理
 * 实现了Serializable接口，以支持对象的序列化和反序列化，便于网络传输或存储
 */
@Data
public class UserAddRequest implements Serializable {
    // 序列化ID，用于版本控制和兼容性校验
    private static final long serialVersionUID = 1L;

    // 用户账号，唯一标识用户的身份
    private String userAccount;

    // 用户名称，展示用户的昵称或真实姓名
    private String userName;

    // 用户头像的URL地址，用于展示用户头像
    private String userAvatar;

    // 用户角色，标识用户的权限级别，如管理员、普通用户等
    private String userRole;

    // 用户简介，简短描述用户的相关信息或兴趣爱好
    private String userProfile;
}
