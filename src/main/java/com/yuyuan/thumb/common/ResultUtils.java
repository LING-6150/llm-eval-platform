package com.yuyuan.thumb.common;

/**
 * 返回工具类
 *
 * @author pine
 */
public class ResultUtils {

    /**
     * 成功
     *
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }


    /**
     * 失败
     *
     */
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }

}
