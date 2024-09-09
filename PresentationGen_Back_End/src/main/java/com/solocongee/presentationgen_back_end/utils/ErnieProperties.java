package com.solocongee.presentationgen_back_end.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置类，绑定文心一言的常量值
 */
@Data
@Component
@ConfigurationProperties(prefix = "ernie")
public class ErnieProperties {
    private String accessTokenURL;
    private String appKey;
    private String secretKey;
}
