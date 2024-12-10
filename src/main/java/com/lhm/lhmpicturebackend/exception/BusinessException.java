package com.lhm.lhmpicturebackend.exception;

import lombok.Getter;

/**
 * 自定义业务异常类，继承自RuntimeException
 * 用于处理业务逻辑中的异常情况，提供了错误码和错误消息
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码，用于标识具体的错误类型
     */
    private final int code;

    /**
     * 构造函数，根据错误码和错误消息创建业务异常对象
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数，根据错误码对象创建业务异常对象
     * 错误码对象包含了错误码和错误消息
     *
     * @param errorCode 错误码对象
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 构造函数，根据错误码对象和自定义错误消息创建业务异常对象
     * 错误码对象提供了错误码，而错误消息可以是具体的错误详情或自定义消息
     *
     * @param errorCode 错误码对象
     * @param message   自定义错误消息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }


}


