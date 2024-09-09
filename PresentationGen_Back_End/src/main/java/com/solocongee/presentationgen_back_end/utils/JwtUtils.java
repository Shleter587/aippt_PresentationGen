package com.solocongee.presentationgen_back_end.utils;

import com.solocongee.presentationgen_back_end.pojo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
public class JwtUtils {

    @Value("${JWTSetting.signingKey}")
    private String signingKey; //用于生成JWT签名的密钥

    /**
     * 传入user，生成JWT
     *
     * @param user 用户类
     */
    public String getJWTString(User user) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getUid());  //JWT包含uid
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, signingKey) //设置签名算法与密钥
                .setClaims(claims) //设置载荷内容
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 60 * 60 * 24 * 1000)) //设置有效期
                .compact();
        return jwt;
    }

    /**
     * 校验令牌并解析出负载uid
     *
     * @param jwtString JWT令牌
     * @return 成功则返回解析出的uid，否则抛出异常（注意处理！）
     */
    public int getJwtPayloadUid(String jwtString) {
        Claims claims;
        claims = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(jwtString)
                .getBody();
        return (int) claims.get("uid");
    }


    /**
     * 生成包含手机号的jwt串用于验证码验证
     * @param phone       电话号码
     * @param correctCode 正确的验证码
     * @return otpToken 包含手机号的jwt串
     */
    public String getOtpJwtString(String phone, String correctCode) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("phone", phone);
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, correctCode + signingKey)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) //设置有效期15min
                .compact();
        return jwt;
    }

    /**
     * 校验验证码并解析出负载电话号码
     *
     * @param jwtString  JWT令牌
     * @param verifyCode 提交的短信验证码，若不符则无法解析
     * @return 校验验证码成功则返回解析出的phone，否则抛出异常（注意处理！）
     */
    public String getJwtPayloadPhone(String jwtString, String verifyCode) {
        Claims claims;
        claims = Jwts.parser()
                .setSigningKey(verifyCode + signingKey) //签名采用 验证码+signingKey
                .parseClaimsJws(jwtString)
                .getBody();
        return (String) claims.get("phone");
    }
}
