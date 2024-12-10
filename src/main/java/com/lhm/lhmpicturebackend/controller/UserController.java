package com.lhm.lhmpicturebackend.controller;

import com.lhm.lhmpicturebackend.common.BaseResponse;
import com.lhm.lhmpicturebackend.common.ResultUtils;
import com.lhm.lhmpicturebackend.exception.ErrorCode;
import com.lhm.lhmpicturebackend.exception.ThrowUtils;
import com.lhm.lhmpicturebackend.model.dto.UserLoginRequest;
import com.lhm.lhmpicturebackend.model.dto.UserRegisterRequest;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.lhm.lhmpicturebackend.model.vo.LoginUserVo;
import com.lhm.lhmpicturebackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

// 用户控制器，处理与用户相关的HTTP请求
@RestController
@RequestMapping("/user")
public class UserController {

    // 注入用户服务，用于处理用户相关的业务逻辑
    @Resource
    private UserService userService;


     // 用户注册接口

    @PostMapping("/register")
    public BaseResponse<Long> UserRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 检查请求体是否为空，如果为空则抛出参数错误异常
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);

        // 从请求体中获取用户账户、密码和确认密码
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        // 调用用户服务的注册方法，传入用户账户、密码和确认密码，处理用户注册逻辑
        long result = userService.userRegister(userAccount, userPassword, checkPassword);

        // 返回成功响应，包含新注册用户的信息
       return ResultUtils.success(result);
    }
    // 用户登录接口
    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取用户输入的账号和密码
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 调用服务层方法实现用户登录，并返回登录用户信息
        LoginUserVo loginUserVo = userService.userLogin(userAccount, userPassword, request);
        // 返回成功响应，包含用户登录信息
        return ResultUtils.success(loginUserVo);
    }

    // 获取用户登录态
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVo> getCurrentUser(HttpServletRequest request) {
        // 从请求中获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 返回成功响应，包含安全用户信息
        return ResultUtils.success(userService.getSafetyUser(loginUser));
    }

    // 用户登出接口
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        // 调用服务层方法实现用户登出
        boolean result = userService.userLogout(request);
        // 返回成功响应，包含登出结果
        return ResultUtils.success(result);
    }
}

