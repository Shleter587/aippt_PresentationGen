package com.solocongee.presentationgen_back_end.intercept;

import com.alibaba.fastjson2.JSON;
import com.solocongee.presentationgen_back_end.pojo.Result;
import com.solocongee.presentationgen_back_end.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;

@CrossOrigin("*")
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {
    final
    JwtUtils jwtUtils;

    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String jwtToken = request.getHeader("token"); //获取jwt token
        log.info("开始校验token:{}", jwtToken);
        int uid;
        try {
            uid = jwtUtils.getJwtPayloadUid(jwtToken);
        } catch (Exception e) {
            log.info("Interceptor校验jwt失败");
            log.error(e.getMessage());
            response.setContentType("application/json");
            response.getWriter().write(JSON.toJSONString(Result.error("invalid jwt token!")));
            return false;
        }
        log.info("Interceptor校验jwt成功");
        request.setAttribute("uid", uid);
        return true; //放行
    }
}
