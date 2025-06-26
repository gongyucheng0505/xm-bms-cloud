package com.xm.bms.common.web;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResult<T> {
    private int code;       // 例如：200 表示成功
    private String message; // 提示信息
    private T data;         // 返回数据

    public CommonResult(int code, String message) {
        this.code = code;
        this.message = message;
    }



    // 成功快捷构造方法
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "Success", data);
    }

    public static <T> CommonResult<T> success(String message) {
        return new CommonResult<>(200, message);
    }

    // 失败快捷构造方法
    public static <T> CommonResult<T> fail(int code, String message) {
        return new CommonResult<>(code, message);
    }
}
