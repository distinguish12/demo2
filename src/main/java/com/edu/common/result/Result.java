package com.edu.common.result;

import com.edu.common.enums.ResultCode;
import lombok.Data;

/**
 * 统一返回结果
 */
@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = ResultCode.SUCCESS.getCode();
        result.message = ResultCode.SUCCESS.getMessage();
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = ResultCode.SUCCESS.getCode();
        result.message = ResultCode.SUCCESS.getMessage();
        result.data = data;
        return result;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.code = ResultCode.SUCCESS.getCode();
        result.message = message;
        result.data = data;
        return result;
    }

    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.code = ResultCode.FAIL.getCode();
        result.message = ResultCode.FAIL.getMessage();
        return result;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.code = ResultCode.FAIL.getCode();
        result.message = message;
        return result;
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.code = resultCode.getCode();
        result.message = resultCode.getMessage();
        return result;
    }

    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        Result<T> result = new Result<>();
        result.code = resultCode.getCode();
        result.message = message;
        return result;
    }

    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }
}