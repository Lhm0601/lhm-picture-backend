package com.lhm.lhmpicturebackend.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.lhm.lhmpicturebackend.common.BaseResponse;
import com.lhm.lhmpicturebackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 * 用于统一处理项目中的异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理未登录异常的处理器方法
     * 当用户尝试访问需要登录才能访问的资源时，如果用户未登录，系统将抛出NotLoginException异常
     * 此方法用于捕获该异常并返回适当的错误响应
     *
     * @param e 未登录异常（NotLoginException）的实例，包含异常相关信息
     * @return 返回一个错误的响应，包含未登录的错误代码和异常信息
     */
    @ExceptionHandler(NotLoginException.class)
    public BaseResponse<?> notLoginException(NotLoginException e) {
        log.error("NotLoginException", e);
        return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR, e.getMessage());
    }

    /**
     * 处理无权限异常的处理器方法
     * 当用户尝试执行其没有权限的操作时，系统将抛出NotPermissionException异常
     * 此方法用于捕获该异常并返回适当的错误响应
     *
     * @param e 无权限异常（NotPermissionException）的实例，包含异常相关信息
     * @return 返回一个错误的响应，包含无权限的错误代码和异常信息
     */
    @ExceptionHandler(NotPermissionException.class)
    public BaseResponse<?> notPermissionExceptionHandler(NotPermissionException e) {
        log.error("NotPermissionException", e);
        return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, e.getMessage());
    }
    /**
     * 处理业务异常的处理器
     * 当抛出BusinessException时，此方法会被调用
     *
     * @param e 业务异常对象
     * @return 响应对象，包含错误信息
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        // 记录业务异常日志
        log.error("BusinessException", e);
        // 返回错误响应，包含异常的代码和消息
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理运行时异常的处理器
     * 当抛出RuntimeException时，此方法会被调用
     *
     * @param e 运行时异常对象
     * @return 响应对象，包含系统错误信息
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        // 记录运行时异常日志
        log.error("RuntimeException", e);
        // 返回系统错误响应
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
