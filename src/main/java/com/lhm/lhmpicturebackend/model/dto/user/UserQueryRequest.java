package com.lhm.lhmpicturebackend.model.dto.user;


import com.lhm.lhmpicturebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求类，用于封装用户查询相关的参数
 * 继承自PageRequest以包含分页信息，实现Serializable接口以支持序列化
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    // 序列化ID，用于版本控制
    private static final long serialVersionUID = 1L;

    // 用户ID
    private Long id;
    // 用户名
    private String userName;
    // 用户角色
    private String userRole;
    // 用户简介
    private String userProfile;
    // 用户头像URL
    private String userAvatar;
    // 用户账号
    private String userAccount;
}
