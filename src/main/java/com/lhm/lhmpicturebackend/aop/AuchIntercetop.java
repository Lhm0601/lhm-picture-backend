package com.lhm.lhmpicturebackend.aop;

import com.lhm.lhmpicturebackend.annotation.AuthCheck;
import com.lhm.lhmpicturebackend.exception.BusinessException;
import com.lhm.lhmpicturebackend.exception.ErrorCode;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.lhm.lhmpicturebackend.model.enums.UserRoleEnum;
import com.lhm.lhmpicturebackend.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
@Aspect
@Component
/**
 * 权限检查切面类，用于拦截需要进行权限验证的方法
 */
public class AuchIntercetop {
    @Resource
    UserService userService;

    /**
     * 环绕通知，用于在方法执行前后进行权限检查
     * @param joinPoint 切入点对象，提供了关于当前执行方法的信息
     * @param authCheck 注解对象，包含了需要进行权限检查的角色信息
     * @return 执行的结果对象
     * @throws Throwable 可能抛出的异常
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取方法上指定的角色
        String mustRole = authCheck.mustRole();

        // 获取当前请求的HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // 根据角色值获取对应的枚举对象
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);

        // 获取当前登录的用户
        User loginUser =userService.getLoginUser(request);

        // 如果指定的角色为空，则不进行权限检查，直接执行方法
        if (mustRoleEnum == null){
            return    joinPoint.proceed();
        }

        // 如果当前用户未登录，则抛出无权限异常
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 获取当前用户的角色枚举
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());

        // 如果方法需要管理员权限，而当前用户不是管理员，则抛出无权限异常
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum)&&!UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 执行方法
        return joinPoint.proceed();
    }
}



