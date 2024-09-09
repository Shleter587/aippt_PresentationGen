package com.solocongee.presentationgen_back_end.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code;//响应码，1 代表成功; 0 代表失败
    private String msg;  //响应信息 描述字符串
    private Object data; //返回的数据

    public static Result success() {
        return new Result(1, "success", null);
    }

    //查询 成功响应
    public static Result success(Object data) {
        return new Result(1, "success", data);
    }

    /**
     * 自定义消息和数据的成功结果构造函数
     *
     * @param message
     * @param data
     * @return
     */
    public static Result success(String message, Object data) {
        return new Result(1, message, data);
    }

    /**
     * 带信息失败响应
     * @param msg
     * @return
     */
    public static Result error(String msg) {
        return new Result(0, msg, null);
    }
    /**
     * 无信息失败响应
     * @return
     */
    public static Result error() {
        return new Result(0, "error!", null);
    }

    /**
     * 根据flag判断成功失败
     * @param flag
     * @return
     */
    public static Result judge(Boolean flag) {
        return flag?Result.success():Result.error();
    }
}
