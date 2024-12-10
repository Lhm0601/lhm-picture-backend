package com.lhm.lhmpicturebackend.service.impl;

import java.util.Date;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lhm.lhmpicturebackend.common.BaseResponse;
import com.lhm.lhmpicturebackend.constant.UserConstant;
import com.lhm.lhmpicturebackend.exception.BusinessException;
import com.lhm.lhmpicturebackend.exception.ErrorCode;
import com.lhm.lhmpicturebackend.exception.ThrowUtils;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.lhm.lhmpicturebackend.model.enums.UserRoleEnum;
import com.lhm.lhmpicturebackend.model.vo.LoginUserVo;
import com.lhm.lhmpicturebackend.service.UserService;
import com.lhm.lhmpicturebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 梁
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-12-10 14:01:20
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    /**
     * 用户注册方法
     * 该方法用于处理用户通过账号和密码进行注册的请求
     * 它会验证用户输入的信息是否合法，包括检查参数是否为空、账号和密码的长度是否符合要求、两次输入的密码是否一致
     * 之后会检查数据库中是否已存在相同的账号，如果不存在，则会加密用户密码并保存用户信息到数据库
     *
     * @param userAccount 用户账号，用于登录系统，不能重复
     * @param userPassword 用户密码，用于登录系统，需要加密后存储
     * @param checkPassword 用于确认用户再次输入的密码，确保密码输入无误
     * @return 返回新注册用户的ID，如果注册失败则抛出异常
     * @throws BusinessException 当输入参数不符合要求或数据库操作失败时抛出此异常
     */
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //账号长度小于4
        ThrowUtils.throwIf(userAccount.length() < 4, new BusinessException( ErrorCode.PARAMS_ERROR, "账号长度小于4"));
        //密码长度小于8
        ThrowUtils.throwIf(userPassword.length() < 8, new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8"));
        //确认密码长度小于8
        ThrowUtils.throwIf(checkPassword.length() < 8, ErrorCode.PARAMS_ERROR, "确认密码长度小于8");
        //两次输入不一致
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        //是否已存在数据库
        // 2. 检查是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(count > 0,new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复"));
        //2.加密
        String encryptPassword = encryptPassword(userPassword);
        //3.保存到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }

    /**
     * 用户登录方法
     *
     * @param userAccount 用户账号，用于登录验证
     * @param userPassword 用户密码，用于登录验证
     * @param request HTTP请求对象，用于存储登录状态
     * @return 登录成功的用户信息对象
     *
     * 该方法首先校验用户输入的账号和密码是否符合规范，然后查询数据库中是否存在匹配的用户信息
     * 如果用户信息匹配成功，则在当前请求的Session中记录用户登录状态，并返回安全处理后的用户信息
     */
    @Override
    public LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        // 校验用户账号和密码是否为空
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数为空");
        // 校验用户账号长度是否符合要求
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号长度小于4");
        // 校验用户密码长度是否符合要求
        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码长度小于8");

        //2.校验账号密码
        // 加密用户密码以进行安全验证
        String encryptPassword = encryptPassword(userPassword);
        // 构建查询条件以验证用户账号和密码
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        // 执行用户信息查询
        User user = this.baseMapper.selectOne(queryWrapper);
        // 校验用户信息是否不存在并打印日志
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            // 抛出业务异常，提示用户账号或密码错误
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号或密码错误");
        }

        // 在请求的Session中记录用户登录状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);

        //4.返回
        // 返回安全处理后的用户信息对象
        return this.getSafetyUser(user);
    }
    /**
     * 获取当前登录的用户信息
     *
     * @param request HTTP请求对象，用于获取登录用户的状态信息
     * @return 当前登录的用户对象如果用户未登录或登录状态无效，则抛出业务异常
     * @throws BusinessException 当用户未登录时，抛出此异常
     */
    public User getLoginUser(HttpServletRequest request) {
        // 判断用户是否已经登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        // 判断是否为空
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库获取最新的用户信息
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }
    /**
     * 用户退出登录功能
     *
     * 此方法通过检查用户会话中的登录状态属性来判断用户是否已登录如果未检测到登录状态，
     * 则抛出操作错误异常，提示用户未登录这是为了确保只有已登录的用户才能执行登出操作
     * 当确认用户已登录后，该方法将从会话中移除登录状态属性，以完成退出登录的操作
     *
     * @param request HTTP请求对象，用于访问会话属性
     * @return 总是返回true，表示退出登录操作执行完毕
     * @throws BusinessException 如果用户未登录就尝试退出登录，抛出此异常
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 判断是否登录
        if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }
    /**
     * 根据User对象获取安全的用户信息对象
     * 该方法主要用于用户数据脱敏，确保用户敏感信息不会被暴露或不当使用
     *
     * @param user User对象，包含用户的各种信息
     * @return LoginUserVo对象，包含复制的用户信息，用于展示或进一步处理
     */
    public LoginUserVo getSafetyUser(User user) {
        // 检查传入的User对象是否为null，如果为null，则直接返回null
        if (user == null){
            return null;
        }
        // 创建一个新的LoginUserVo对象，用于存储安全的用户信息
        LoginUserVo loginUserVo = new LoginUserVo();
        // 使用BeanUtil工具类复制User对象的属性到LoginUserVo对象，实现数据的快速转换
        BeanUtil.copyProperties(user, loginUserVo);
        // 返回填充好的LoginUserVo对象
        return loginUserVo;
    }

    /**
     * 加密用户密码，使用盐值增加安全性
     *
     * @param password 用户输入的原始密码
     * @return 加密后的密码
     */
    private String encryptPassword(String password) {
        // 定义盐值，用于增加密码安全性，防止彩虹表攻击
        final String salt = "ikun";

        // 将盐值和用户密码拼接后，使用MD5算法进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes());

        // 返回加密后的密码
        return encryptPassword;
    }
}




