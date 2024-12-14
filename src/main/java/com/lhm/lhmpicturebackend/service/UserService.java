package com.lhm.lhmpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lhm.lhmpicturebackend.model.dto.user.UserQueryRequest;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lhm.lhmpicturebackend.model.vo.LoginUserVO;
import com.lhm.lhmpicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用于构建用户查询条件的方法，不直接操作敏感信息，故不展开详细注释
     */

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取用户安全信息方法，用于隐藏用户敏感信息
     *
     * @param originUser 原始User对象，包含所有用户信息
     * @return 返回一个LoginUserVO对象，其中包含脱敏后的用户信息
     */
    LoginUserVO getSafetyUser(User originUser);

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

    /**
     *将User对象转换为UserVO对象的方法，不直接操作敏感信息，故不展开详细注释
     */
    UserVO getUserVO(User user);

    /**
     * 将UserVO对象转换为User对象的方法，不直接操作敏感信息，故不展开详细注释
     */
    List<UserVO> getUserVoList(List<User> userList);

    /**
     * 判断用户是否为管理员
     *
     * @param user 用户对象
     * @return 如果用户是管理员，返回true；否则返回false
     */
    boolean isAdmin(User user);

    /**
     * 加密密码方法
     *
     * @param password 原始密码字符串
     * @return 返回加密后的密码字符串
     */
    String encryptPassword(String password);
}
