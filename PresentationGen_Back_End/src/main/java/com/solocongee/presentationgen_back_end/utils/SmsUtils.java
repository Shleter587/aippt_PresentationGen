package com.solocongee.presentationgen_back_end.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 用于发送验证码短信的工具类，
 * 使用阿里云的服务
 * ACCESS_KEY_ID和ACCESS_KEY_SECRET需配置进系统环境变量
 */
@Component
public class SmsUtils {
    @Value("${aliyun.sms.keyID}")
    private String keyID;

    @Value("${aliyun.sms.keySecret}")
    private String keySecret;

    @Value("${aliyun.sms.endpoint}")
    private String endpoint;
    /**
     * 使用AK&SK初始化账号Client
     * @return Client
     * @throws Exception
     */
    private com.aliyun.teaopenapi.Client createClient() throws Exception {
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考。
        // 建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html。
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId(keyID)
                // 必填，ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret(keySecret);
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint =endpoint;
        return new com.aliyun.teaopenapi.Client(config);
    }

    private com.aliyun.teaopenapi.models.Params createApiInfo() throws Exception {
        com.aliyun.teaopenapi.models.Params params = new com.aliyun.teaopenapi.models.Params()
                .setAction("SendSms") // 接口名称
                .setVersion("2017-05-25") // 接口版本
                .setProtocol("HTTPS") // 接口协议
                .setMethod("POST") // 接口 HTTP 方法
                .setAuthType("AK")
                .setStyle("RPC")
                .setPathname("/") // 接口 PATH
                .setReqBodyType("json") // 接口请求体内容格式
                .setBodyType("json"); // 接口响应体内容格式
        return params;
    }

    /**
     *
     * @param phoneNumber 电话号码。国内：+/+86/0086/86 或无任何前缀的11位手机号码，例如1390000****
     * @param verifyCode 数字字母组合，4-6位
     * @throws Exception
     */
    public void sendSmsVerifyCode(String phoneNumber, String verifyCode) throws Exception {
        com.aliyun.teaopenapi.Client client = createClient();
        com.aliyun.teaopenapi.models.Params params = createApiInfo();
        // query params
        HashMap<String, Object> queries = new java.util.HashMap<>();
        queries.put("PhoneNumbers", phoneNumber);
        queries.put("SignName", "PPTGen平台");
        queries.put("TemplateCode", "SMS_465921270");
        queries.put("TemplateParam", "{\"code\":\"" + verifyCode +"\"}");
        // runtime options
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest()
                .setQuery(com.aliyun.openapiutil.Client.query(queries));
        // 复制代码运行请自行打印 API 的返回值
        // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
        client.callApi(params, request, runtime);
    }


}