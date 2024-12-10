package com.lhm.lhmpicturebackend.constant;
/**
 * 用户相关的常量接口
 * 定义了用户登录状态和用户角色的常量
 */
public interface UserConstant {
    /**
     * 用户登录状态的常量
     * 用于在会话中标识用户是否已登录
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 管理员角色的常量
     * 用于标识用户角色为管理员
     */
    String USER_ROLE_ADMIN = "admin";

    /**
     * 普通用户角色的常量
     * 用于标识用户角色为普通用户
     */
    String USER_ROLE_USER = "user";
}
