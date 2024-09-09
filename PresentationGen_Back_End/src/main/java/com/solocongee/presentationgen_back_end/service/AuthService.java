package com.solocongee.presentationgen_back_end.service;

import com.solocongee.presentationgen_back_end.mapper.AuthMapper;
import com.solocongee.presentationgen_back_end.pojo.User;
import com.solocongee.presentationgen_back_end.utils.AliOSSUtils;
import com.solocongee.presentationgen_back_end.utils.JwtUtils;
import com.solocongee.presentationgen_back_end.utils.SmsUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 用于验证用户身份以及管理用户的类
 */
@Service
@Slf4j
public class AuthService {

    @Value("${JWTSetting.signingKey}")
    private String signingKey; //用于生成JWT签名的密钥

    private final AuthMapper authMapper;
    private final AliOSSUtils aliOSSUtils;

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    public AuthService(AuthMapper authMapper, AliOSSUtils aliOSSUtils) {
        this.authMapper = authMapper;
        this.aliOSSUtils = aliOSSUtils;
    }

    public HashMap<String, Object> checkPwd(String userName, String passWord) { //查询用户名并检查密码是否正确，正确则返回jwt令牌，否则返回null
        List<User> users = authMapper.selectUserByName(userName);
        if (users.isEmpty()) {
            return null;
        } else if (users.size() > 1) {
            System.out.println("用户名'" + userName + "'存在重名！");
            return null;
        } else if (passWord.equals(users.get(0).getPassword())) {  //核验密码
            HashMap<String, Object> map = new HashMap<>();
            map.put("token", jwtUtils.getJWTString(users.get(0)));
            return map;
        }
        return null;
    }

    /**
     * 根据phone发送验证码短信
     *
     * @return verifyCode 正确的验证码
     */
    public String sendLoginOtpSms(String phone) throws Exception {
        String code = genRandStr(4); //生成验证码
        System.out.println(code);
        smsUtils.sendSmsVerifyCode(phone, code);  //调用api，发送验证码短信
        return code;
    }

    public String getOtpJwtString(String phone, String correctCode) {
        return jwtUtils.getOtpJwtString(phone, correctCode);
    }

    /**
     * 校验电话号码和验证码
     *
     * @param code  提交的验证码
     * @param jwtString jwt
     */
    public Map<String, Object> checkLoginOtp(String code, String jwtString) {
        Map<String, Object> response = new HashMap<>();
        try {
            //尝试校验jwt
            String phone = jwtUtils.getJwtPayloadPhone(jwtString, code);
            //校验成功后：
            List<User> users = authMapper.selectUserByPhone(phone);
            if (users.isEmpty()) { //查找不到对应用户，则创建新用户
                authMapper.createUserByPhone(phone, LocalDateTime.now());
                users = authMapper.selectUserByPhone(phone);
                response.put("token", jwtUtils.getJWTString(users.get(0)));
                response.put("new_account", true);
            } else if (users.size() > 1) { //存在查找到多个用户的冲突
                response.put("error", "verify-login-otp fail, multiply accounts !");
                return response;
            } else { //正常登录
                response.put("token", jwtUtils.getJWTString(users.get(0)));
                response.put("new_account", false);
            }
            return response;
        }catch (java.lang.Exception e){
            response.put("error", "verify-login-otp fail");
            return response;
        }
    }


    /**
     * 根据uid查询号码并发送重设验证码短信
     * @param uid 用户的uid
     * @return OtpJwtString
     */
    public String sendResetOtpSms(int uid) throws Exception {
        User user = authMapper.selectUserByUid(uid);
        String code = genRandStr(4); //生成验证码
        String userPhoneNumber = user.getPhoneNumber();
        if(userPhoneNumber == null || userPhoneNumber.isEmpty()){
            throw new Exception("用户电话号码为空!");
        }
        smsUtils.sendSmsVerifyCode(userPhoneNumber, code);  //调用api，发送验证码短信
        log.info("发送验证码：" + userPhoneNumber + ", " + code);
        return jwtUtils.getOtpJwtString(user.getPhoneNumber(), code); //返回OtpJwt
    }

    /**
     * 校验重设验证码,并更改密码
     *
     * @param code        提交的验证码
     * @param otpJwtToken otpToken
     * @param newPassword 新密码
     * @return 校验成功则返回true
     */
    public boolean verifyResetPwd(String code, String otpJwtToken, String newPassword, int uid) {
        try{
            jwtUtils.getJwtPayloadPhone(otpJwtToken, code);
            authMapper.updateUserPassword(uid, newPassword); //更新密码
        }catch(Exception e){
            log.info(e.getMessage());
            return false;
        }
        return true;
    }


//    //将bean转换为HashMap<String, Object>，用于生成JWT
//    private HashMap<String, Object> convertBeanToHashMap(Object bean) {
//        HashMap<String, Object> map = new HashMap<>();
//        for (Field field : bean.getClass().getDeclaredFields()) {
//            field.setAccessible(true);
//            try {
//                map.put(field.getName(), field.get(bean));
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        return map;
//    }

    private String genRandStr(int len) {
        final String rangeSet = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < len; i++) {
            sb.append(rangeSet.charAt(rand.nextInt(rangeSet.length())));
        }
        return sb.toString();
    }


    /**
     * 校验令牌并解析出负载
     *
     * @param jwtString JWT令牌
     * @return 成功则返回令牌解析出的负载(以Map形式)，否则返回null
     */
    public Map<String, Object> checkParseJwt(String jwtString) {
        Claims claims;
        Map<String, Object> map;
        try {
            claims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(jwtString)
                    .getBody();
            map = claims;
        } catch (ExpiredJwtException e) {
            //发生错误，比如令牌过期了
            //e.printStackTrace();
            return null;
        }
        return map;
    }


    /**
     * 通过用户名和密码创建用户
     */
    public boolean registerByPwd(String name, String pwd) {
        List<User> exists_users = authMapper.selectUserByName(name); //查找是否已有重名用户
        if (exists_users.isEmpty()) {
            authMapper.createUserByPwd(name, pwd, LocalDateTime.now()); //创建用户，创建时间为当前时间
            return true;
        }
        return false;
    }

    /**
     * 上传用户头像，返回图片url
     */
    public String uploadProfilePicture(int uid, MultipartFile photo) throws Exception {
        log.info("获取到图片文件,原名：{}", photo.getOriginalFilename());
        String originalFileName = photo.getOriginalFilename();
        assert originalFileName != null;
        String suffix = originalFileName.substring(originalFileName.lastIndexOf('.'));
        Path file = Files.createTempFile(photo.getOriginalFilename(), suffix);
        photo.transferTo(file);

        //从OSS删除原本的头像
        String oldProfile = authMapper.selectProfilePictureByID(uid);
        if(oldProfile != null){
            aliOSSUtils.deleteFile(oldProfile);
        }
        //上传到OSS并返回URL
        String onlineUrl = aliOSSUtils.upload(file);
        authMapper.updateUserProfilePicture(uid, onlineUrl);

        return onlineUrl;
    }

    public User queryUserInfo(Integer uid) {
        log.info("查询id为{}的用户信息", uid);
        User selectedUser = authMapper.selectUserByUid(uid);
        selectedUser.setPassword(null); //删除密码信息
        return selectedUser;
    }

    /**
     * 更改已有账号的手机号。要求该手机号未绑定过。
     * @param uid uid
     * @param newPhone 新的电话号码
     */
    public boolean updateUserPhone(int uid, String newPhone){
        List<User> users = authMapper.selectUserByPhone(newPhone);
        if(!users.isEmpty()){
            return false;
        }
        try {
            authMapper.updateUserPhone(uid, newPhone);
        }catch(Exception e){
            log.error(e.getMessage());
            return false;
        }
        return true;
    }
}
