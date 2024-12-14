package com.lhm.lhmpicturebackend.exception;

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
