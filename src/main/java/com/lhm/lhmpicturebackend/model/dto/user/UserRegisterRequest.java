package com.lhm.lhmpicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;
/**
 * 用户注册请求类
 * 该类用于封装用户注册时提交的信息，包括用户账号和密码等
 * 实现了Serializable接口，以支持对象的序列化和反序列化，便于网络传输或存储
 */
@Data
public class UserRegisterRequest implements Serializable {
    /**
     * 序列化ID，用于标识类的版本
     * 当类的结构发生变化时，该值应被修改，以确保类的唯一性
     */
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账号
     * 唯一标识用户的身份，用于登录验证
     */
    private String userAccount;

    /**
     * 用户密码
     * 用于登录验证，确保用户信息安全
     */
    private String userPassword;

    /**
     * 确认密码
     * 用于在用户注册时确认密码的正确性，确保用户密码输入无误
     */
    private String checkPassword;

}
