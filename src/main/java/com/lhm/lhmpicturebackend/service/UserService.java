package com.lhm.lhmpicturebackend.service;

import com.lhm.lhmpicturebackend.common.BaseResponse;
import com.lhm.lhmpicturebackend.model.dto.UserRegisterRequest;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lhm.lhmpicturebackend.model.vo.LoginUserVo;

import javax.servlet.http.HttpServletRequest;

/**
* @author 梁
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-12-10 14:01:20
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册方法
     *
     * @param userAccount 用户账号，应为唯一的字符串
     * @param userPassword 用户设置的密码
     * @param checkPassword 用户再次输入的密码，用于确认
     * @return 返回一个长整型数字，通常代表用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);
    /**
     * 用户登录方法
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request HTTP请求对象，用于获取用户登录信息
     * @return 返回一个LoginUserVo对象，包含用户登录信息
     */
    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);
    /**
     * 获取用户安全信息方法，用于隐藏用户敏感信息
     *
     * @param originUser 原始User对象，包含所有用户信息
     * @return 返回一个LoginUserVo对象，其中包含脱敏后的用户信息
     */
    LoginUserVo getSafetyUser(User originUser);
    /**
     * 获取当前登录用户信息方法
     *
     * @param request HTTP请求对象，用于获取用户登录状态
     * @return 返回一个User对象，代表当前登录的用户
     */
    User getLoginUser(HttpServletRequest request);
    /**
     * 退出登录方法
     * 删除用户登录状态，并返回一个BaseResponse对象，用于返回给客户端
     * @param request HTTP请求对象，用于获取用户登录状态
     *                删除用户登录状态
     */
    boolean userLogout(HttpServletRequest request);

}
