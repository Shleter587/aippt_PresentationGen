package com.solocongee.presentationgen_back_end.controller;

import com.solocongee.presentationgen_back_end.pojo.Result;
import com.solocongee.presentationgen_back_end.pojo.User;
import com.solocongee.presentationgen_back_end.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/*
 * 用于处理登录和注册的controller
 */
//@CrossOrigin("http://localhost:8081")
@CrossOrigin("*")
@Slf4j
@RestController
public class AuthController {
    final private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     *  密码登录逻辑（试行）
     */
    @PostMapping("/api/auth/login")
    public Result loginCheckPwd(@RequestBody User user) {
        HashMap<String, Object> map;
        if (user.getUsername() == null || user.getPassword() == null) {
            log.error("接受登录的参数不能为空!");
            return Result.error("接受登录的参数不能为空!");
        }
        map = authService.checkPwd(user.getUsername(), user.getPassword());
        if (map != null) { //判断登录是否成功
            return Result.success("login success", map); //登录成功
        } else {
            return Result.error("用户名或密码错误!");
        }
    }

    /**
     * 请求发送登录验证码短信
     */
    @PostMapping("/api/auth/send-login-otp")
    public Result sendLoginOtp(@RequestBody Map<String, Object> map) {
        String phone = (String) map.get("phone_number");
        try {
            String verifyCode = authService.sendLoginOtpSms(phone);
            String otpToken = authService.getOtpJwtString(phone, verifyCode);
            HashMap<String, Object> retMap = new HashMap<>();
            retMap.put("otpToken", otpToken);
            return Result.success("OTP sent successfully", retMap);
        } catch (Exception e) {
            log.error(e.getMessage());
            //发送失败
            return Result.error("OTP sent fail!");
        }
    }

    /**
     * 核验登录验证码。需要前端header里加上otpToken
     *
     * @param map 请求体JSON应包otp
     */
    @PostMapping("/api/auth/verify-login-otp")
    public Result checkLoginOtp(@RequestBody Map<String, Object> map, HttpServletRequest req) {
        String code = (String) map.get("otp");
        String otpToken = req.getHeader("otpToken");
        log.info("登录收到的验证码token为"+otpToken);
        Map<String, Object> serviceResponse = authService.checkLoginOtp(code, otpToken);
        if (serviceResponse.containsKey("error")) {
            return Result.error((String) serviceResponse.get("error"));
        }
        return Result.success("OTP verification successful", serviceResponse);
    }

    /**
     * 通过用户名和密码创建用户（暂行）
     */
    @PostMapping("/api/auth/register")
    public Result createUserByPwd(@RequestBody User user) {
        if (authService.registerByPwd(user.getUsername(), user.getPassword()))
            return Result.success("Register successful", null);
        return Result.error("Register fail:Username existed");
    }

    /**
     * 请求发送用于重置密码的验证码
     *
     * @return Result
     */
    @PostMapping("/api/auth/send-reset-otp")
    public Result sendResetOtp(HttpServletRequest req) {
        try {
            int uid = (int) req.getAttribute("uid");
            String otpToken = authService.sendResetOtpSms(uid); //尝试查询对应号码并发送短信
            HashMap<String, Object> retMap = new HashMap<>();
            retMap.put("otpToken", otpToken);
            return Result.success("OTP sent successfully", retMap);
        } catch (Exception e) {
            return Result.error("OTP sent fail");
        }
    }

    /**
     * 校验验证码，若成功则更改新密码。需要前端header里加上otpToken
     *
     * @return Result
     */
    @PostMapping("/api/auth/verify-reset-pwd")
    public Result verifyResetPwd(@RequestBody Map<String, Object> map, HttpServletRequest req) {
        String newPassword = (String) map.get("new_password"); //新密码
        String code = (String) map.get("otp"); //用户填写的验证码
        String otpJwt = req.getHeader("otpToken");
        log.info("重置密码收到的验证码token为"+otpJwt);
        int uid = (int) req.getAttribute("uid");
        if (authService.verifyResetPwd(code, otpJwt, newPassword, uid))
            return Result.success("Reset successful", null);
        return Result.error("Reset fail");
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/api/auth/upload-profile-picture")
    public Result uploadProfilePicture(@RequestParam("photo") MultipartFile photo, HttpServletRequest req) {
        int uid = (int) req.getAttribute("uid");
        try {
            String url = authService.uploadProfilePicture(uid, photo);
            HashMap<String, Object> data = new HashMap<>();
            data.put("picture_url", url);
            return Result.success("", data);
        } catch (Exception e) {
            log.warn("上传文件处理失败！");
            log.warn(e.getMessage());
            return Result.error("上传文件失败");
        }
    }

    /**
     * 获取用户信息
     *
     * @param req 请求http
     * @return Result
     */
    @PostMapping("/api/auth/info")
    public Result queryUserInfo(HttpServletRequest req) {
        Integer uid = (Integer) req.getAttribute("uid");
        return Result.success(authService.queryUserInfo(uid));
    }

    /**
     * 判断jwt有效状态
     *
     */
    @PostMapping("/api/auth/check")
    public Result checkLoginStatus() {
        return Result.success("valid jwt token",null);
    }
    /**
     * 更改已有账号的手机号。要求该手机号未绑定过。
     */
    @PostMapping("/api/auth/set-phone")
    public Result updateUserPhone(@RequestBody Map<String, Object> map, HttpServletRequest req) {
        String newPhone = (String) map.get("new_phone");
        int uid = (int) req.getAttribute("uid");
        if(authService.updateUserPhone(uid, newPhone)) { //如果设置成功
            return Result.success("Phone have been set successfully", null);
        }
        return Result.error("Phone have been set by another user");
    }
}
