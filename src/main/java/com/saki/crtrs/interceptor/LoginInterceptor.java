package com.saki.crtrs.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saki.crtrs.utils.JwtUtil;
import com.saki.crtrs.utils.ResultUtil;
import com.saki.crtrs.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 请求处理前执行：校验令牌
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 排除登录/注册接口（根据实际业务路径调整，支持模糊匹配）
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/user/login") || requestURI.contains("/user/register")) {
            return true; // 放行登录/注册接口
        }

        // 2. 从请求头获取token（前端需将token放在Authorization头中，格式：Bearer {token}）
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            // 无令牌，返回未登录响应
            returnErrorResponse(response, "未登录，请先登录");
            return false;
        }
        // 截取真正的token（去掉Bearer 前缀）
        token = token.substring(7);

        // 3. 验证token并解析用户信息
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            // 将用户信息存入ThreadLocal，供后续业务使用
            ThreadLocalUtil.set(claims);
            return true; // 令牌合法，放行
        } catch (JWTVerificationException e) {
            // token过期/签名错误等异常，返回未登录提示
            returnErrorResponse(response, "登录状态过期，请重新登录");
            return false;
        }
    }

    /**
     * 请求处理完成后执行：清除ThreadLocal，防止内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }

    /**
     * 构建错误响应并返回JSON格式
     */
    private void returnErrorResponse(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        // 构建统一返回结果
        ResultUtil<Object> result = ResultUtil.error(message);
        // 将结果转为JSON字符串写入响应
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        response.getWriter().write(json);
    }
}
