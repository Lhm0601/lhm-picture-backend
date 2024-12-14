package com.lhm.lhmpicturebackend.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhm.lhmpicturebackend.annotation.AuthCheck;
import com.lhm.lhmpicturebackend.common.BaseResponse;
import com.lhm.lhmpicturebackend.common.DeleteRequest;
import com.lhm.lhmpicturebackend.common.ResultUtils;
import com.lhm.lhmpicturebackend.constant.UserConstant;
import com.lhm.lhmpicturebackend.exception.BusinessException;
import com.lhm.lhmpicturebackend.exception.ErrorCode;
import com.lhm.lhmpicturebackend.exception.ThrowUtils;
import com.lhm.lhmpicturebackend.model.dto.user.*;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.lhm.lhmpicturebackend.model.vo.LoginUserVO;
import com.lhm.lhmpicturebackend.model.vo.UserVO;
import com.lhm.lhmpicturebackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

// 用户控制器，处理与用户相关的HTTP请求
@RestController
@RequestMapping("/user")
public class UserController {

    // 注入用户服务，用于处理用户相关的业务逻辑
    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     */
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

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取用户输入的账号和密码
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 调用服务层方法实现用户登录，并返回登录用户信息
        LoginUserVO loginUserVo = userService.userLogin(userAccount, userPassword, request);
        // 返回成功响应，包含用户登录信息
        return ResultUtils.success(loginUserVo);
    }

    // 获取用户登录态
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getCurrentUser(HttpServletRequest request) {
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

    // 创建用户(管理员)
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.USER_ROLE_ADMIN)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        // 参数校验
        // 检查入参是否为空，为空则抛出异常，这确保了数据的完整性
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 调用服务层方法实现用户添加
        // 将请求参数转换为User对象，以便于后续操作
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user, true);

        //默认密码
        // 定义默认密码，用于新用户首次登录使用
        final String DEFAULT_PASSWORD = "12345678";

        //加密加盐
        // 对默认密码进行加密处理，增强安全性
        String encryptPassword = userService.encryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);

        // 保存用户信息
        // 将用户信息保存到数据库中，并检查保存结果
        boolean result = userService.save(user);
        // 如果保存失败，则抛出异常
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回成功响应，包含用户ID
        // 返回新添加用户的ID，表示操作成功
        return ResultUtils.success(user.getId());
    }
    /**
     * 删除用户接口
     * 该方法接收一个DeleteRequest对象作为请求体，其中包含了需要删除的用户ID
     * 主要业务逻辑包括参数校验和调用userService的removeById方法来删除指定用户
     *
     * @param deleteRequest 包含待删除用户ID的请求对象
     * @return 返回一个BaseResponse对象，其中包含删除操作是否成功的布尔值
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.USER_ROLE_ADMIN)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        // 参数校验，确保deleteRequest不为空且所含ID为有效值
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        // 调用用户服务的删除方法，传入用户ID
        boolean b = userService.removeById(deleteRequest.getId());
        // 返回删除成功的响应结果
        return ResultUtils.success(b);
    }
    /**
     * 处理用户更新请求的接口
     * 该接口用于更新用户信息，需要管理员权限
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.USER_ROLE_ADMIN)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        // 参数校验
        ThrowUtils.throwIf(userUpdateRequest == null || userUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR);

        // 将请求参数转换为User对象
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user, true);

        // 调用服务层方法更新用户信息
        boolean result = userService.updateById(user);

        // 根据更新结果返回响应
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.USER_ROLE_ADMIN)
    public BaseResponse<User> getUserById(long id) {
        // 检查传入的id是否有效
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 根据id获取用户信息
        User user = userService.getById(id);
        // 如果用户不存在，则抛出异常
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        // 返回成功响应，包含用户信息
        return ResultUtils.success(user);
    }
    // 根据用户ID获取用户视图对象
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVoById(long id) {
        // 调用服务层方法，根据ID获取用户实体
        BaseResponse<User> response = getUserById(id);
        // 从响应中提取用户实体数据
        User user = response.getData();
        // 将用户实体转换为用户视图对象，并返回成功响应
        return ResultUtils.success(userService.getUserVO(user));
    }
    /**
     * 处理用户VO列表的分页请求
     * 该方法使用POST方式请求，并且要求用户具有管理员角色
     *
     * @param userQueryRequest 包含用户查询请求参数的对象
     * @return 返回一个包含用户VO分页信息的响应对象
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.USER_ROLE_ADMIN)
    public BaseResponse<Page<UserVO>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest) {
        // 获取当前页码和页面大小
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 执行用户分页查询
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        // 初始化用户VO分页对象
        Page<UserVO> userVoPage = new Page<>(current, size, userPage.getTotal());
        // 将用户实体列表转换为用户VO列表
        List<UserVO> userVOList = userService.getUserVoList(userPage.getRecords());
        userVoPage.setRecords(userVOList);
        // 返回成功响应，包含用户VO分页信息
        return ResultUtils.success(userVoPage);
    }

}
