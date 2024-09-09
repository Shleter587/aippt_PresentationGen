package com.solocongee.presentationgen_back_end.config;

import com.solocongee.presentationgen_back_end.intercept.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")  // 先拦截所有/api/下的请求
                .excludePathPatterns("/api/ppt-template")
                .excludePathPatterns("/api/auth/**");  // 排除所有/api/auth/下的请求
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/auth/upload-profile-picture",
                        "/api/auth/info",
                        "/api/auth/send-reset-otp",
                        "/api/auth/set-phone",
                        "/api/auth/check",
                        "/api/auth/verify-reset-pwd");  // 明确指定需要拦截的/api/auth/下的路径
    }


}
