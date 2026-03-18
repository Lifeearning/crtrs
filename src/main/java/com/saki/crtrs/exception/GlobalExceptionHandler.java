package com.saki.crtrs.exception;

import com.saki.crtrs.utils.ResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalExceptionHandler {
//    处理所有未捕获的异常
    @ExceptionHandler(Exception.class)
    public ResultUtil<Void> handleException(Exception e){
        e.printStackTrace(); // 实际生产环境建议日志记录
        return ResultUtil.error("服务器内部错误："+e.getMessage());
    }
}
