package com.saki.crtrs.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultUtil<T> {
    private Integer code; //业务状态码 0-成功 1-失败
    private String message; //提示信息
    private T data; //响应数据

//    快速返回操作成功响应结果（带响应数据）
    public static <E> ResultUtil<E> success(E data){return new ResultUtil<>(0,"操作成功",data);}

//    快速返回操作成功响应结果
    public static <T> ResultUtil<T> success(){return new ResultUtil<>(0,"操作成功",null);}
    public static <T> ResultUtil<T> error(String message){return  new ResultUtil<>(1,message,null);}
}
