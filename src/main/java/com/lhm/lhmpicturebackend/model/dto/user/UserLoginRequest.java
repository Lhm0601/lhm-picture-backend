package com.lhm.lhmpicturebackend.model.dto.user;

import lombok.Data;

/**
 * 用户登录请求类
 * 用于封装用户登录时需要提供的信息
 */
@Data
public class UserLoginRequest {
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账号
     * 用于标识用户的身份
     */
    private String userAccount;

    /**
     * 用户密码
     * 用于验证用户身份的密钥
     */
    private String userPassword;
}
