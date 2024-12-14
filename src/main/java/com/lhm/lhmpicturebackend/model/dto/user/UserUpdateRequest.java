package com.lhm.lhmpicturebackend.model.dto.user;


import lombok.Data;

import java.io.Serializable;
/**
 * 用户更新请求类
 * 用于序列化用户更新操作的数据
 * 实现了Serializable接口以支持对象的序列化和反序列化
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * 用户ID
     * 用于标识用户，以便于更新操作
     */
    private Long id;

    /**
     * 用户名
     * 用户的登录名称
     */
    private String userName;

    /**
     * 用户头像URL
     * 指定用户的头像图片地址
     */
    private String userAvatar;

    /**
     * 用户简介
     * 描述用户的一些基本信息
     */
    private String userProfile;

    /**
     * 用户角色
     * 定义用户的权限级别
     */
    private String userRole;

    /**
     * 序列化版本ID
     * 用于序列化机制，确保类的版本在反序列化时一致
     */
    private static final long serialVersionUID = 1L;
}
