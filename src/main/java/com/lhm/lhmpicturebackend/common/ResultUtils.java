package com.lhm.lhmpicturebackend.common;

import com.lhm.lhmpicturebackend.exception.ErrorCode;

/**
 * 结果工具类
 *
 * 提供了一系列工具方法，用于创建统一的响应结果
 */
public class ResultUtils {

    /**
     * 创建一个表示成功操作的响应对象
     *
     * @param data 成功操作后返回的数据
     * @param <T>  数据类型
     * @return 包含成功数据的响应对象
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 创建一个表示操作失败的响应对象
     *
     * @param errorCode 错误码，表示失败的原因
     * @return 包含错误信息的响应对象
     */
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 创建一个自定义错误码和错误信息的失败响应对象
     *
     * @param code    错误码
     * @param message 错误信息
     * @return 包含自定义错误信息的响应对象
     */
    public static BaseResponse<?> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 创建一个带有特定错误码和自定义错误信息的失败响应对象
     *
     * @param errorCode 错误码，表示失败的原因
     * @param message   自定义的错误信息
     * @return 包含特定错误码和自定义错误信息的响应对象
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}
