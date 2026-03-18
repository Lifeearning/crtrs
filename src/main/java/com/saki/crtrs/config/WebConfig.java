package com.saki.crtrs.config;

import com.saki.crtrs.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除登录/注册接口（根据实际业务路径调整）
                .excludePathPatterns(
                        "/user/login/**",    // 登录接口（比如/post/login、/user/login）
                        "/user/register/**"  // 注册接口
                );
    }
}
