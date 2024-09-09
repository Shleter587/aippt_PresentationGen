package com.solocongee.presentationgen_back_end.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.solocongee.presentationgen_back_end.utils.ErnieProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AccessTokenService {
    private final ErnieProperties ernieProperties;
    private String accessToken = null;
    private String refreshToken = null;
    private LocalDateTime updateTime;// AccessToken创建时间
    private LocalDateTime expireTime;// AccessToken到期时间
    private final WebClient webClient;


    @Autowired
    public AccessTokenService(WebClient.Builder webClientBuilder, ErnieProperties ernieProperties) {
        this.webClient = webClientBuilder.baseUrl(ernieProperties.getAccessTokenURL()).build();
        log.info(ernieProperties.getAccessTokenURL());
        this.ernieProperties = ernieProperties;
    }

    /**
     * 获取有效的accessToken
     *
     * @return String 返回Srting类型的accessToken
     */
    public String getAccessToken() {
        if (accessToken == null || !LocalDateTime.now().isBefore(expireTime)) {
            flushAccessToken();
        }
        return accessToken;

    }

    /**
     * 刷新accessToken
     *
     * @return
     */
    public synchronized void flushAccessToken() {
        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("client_id", ernieProperties.getAppKey())
                        .queryParam("client_secret", ernieProperties.getSecretKey())
                        .queryParam("grant_type", "client_credentials")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> {
                    JSONObject jsonObject = JSON.parseObject(response);
                    accessToken = jsonObject.getString("access_token");
                    refreshToken = jsonObject.getString("refresh_token");
                    updateTime = LocalDateTime.now();
                    expireTime = updateTime.plusSeconds(Long.parseLong(jsonObject.getString("expires_in")));
                })
                .doOnError(error -> log.error("ACCESS_TOKEN获取失败", error))
                .block(); // 阻塞获取并处理响应
    }
}
