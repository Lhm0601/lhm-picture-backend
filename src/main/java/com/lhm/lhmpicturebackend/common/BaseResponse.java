package com.lhm.lhmpicturebackend.common;

import com.lhm.lhmpicturebackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 基础响应类，用于封装API响应数据
 * 实现了Serializable接口，以支持序列化和反序列化
 * 使用Lombok的@Data注解，自动生成getter和setter方法
 *
 * @param <T> 响应数据的类型
 */
@Data
public class BaseResponse<T> implements Serializable {

    // 响应码，表示响应的状态
    private int code;

    // 响应数据，泛型设计使其能够承载各种类型的响应数据
    private T data;

    // 响应消息，用于提供更详细的响应信息
    private String message;

    /**
     * 构造方法，用于创建包含响应码、数据和消息的BaseResponse对象
     *
     * @param code    响应码
     * @param data    响应数据
     * @param message 响应消息
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * 构造方法，用于创建只包含响应码和数据的BaseResponse对象，消息默认为空字符串
     *
     * @param code 响应码
     * @param data 响应数据
     */
    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    /**
     * 构造方法，用于根据ErrorCode枚举创建BaseResponse对象，数据为null
     *
     * @param errorCode 错误码枚举，包含响应码和消息
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
