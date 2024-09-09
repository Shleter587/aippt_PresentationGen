package com.solocongee.presentationgen_back_end.service;

import com.alibaba.fastjson2.JSONObject;
import com.solocongee.presentationgen_back_end.mapper.MarkdownGenMapper;
import com.solocongee.presentationgen_back_end.pojo.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class MarkdownGenService {

    private final MarkdownGenMapper markdownGenMapper;
    private final WebClient webClient;
    final AccessTokenService accessTokenService;

    @Value("${ernie.botURL}")
    private String botURL;

    @Value("${path.markdown}")
    private String markdownPath;

    public MarkdownGenService(AccessTokenService accessTokenService, MarkdownGenMapper markdownGenMapper) {
        this.webClient = WebClient.builder()
                .build();
        this.accessTokenService = accessTokenService;
        this.markdownGenMapper = markdownGenMapper;
    }

    public Flux<String> sendRequest(String topic, String sup, Integer uid) {
        String streamID = "stream" + UUID.randomUUID().toString().replace("-", "");

        log.info("第一次请求发送成功");
        String accessToken = accessTokenService.getAccessToken();
        log.info("当前accesstoken为{}", accessToken);
        String requestBody = buildRequestBody(topic, sup);
        StringBuilder completeResponse = new StringBuilder();

        return this.webClient.post()
                .uri(botURL + "?access_token=" + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(ApiResponse.class)
                .flatMap(apiResponse -> {
                    // 累积当前响应结果
                    completeResponse.append(apiResponse.getResult());
                    // 检查是否结束并且被截断
                    if ("true".equals(apiResponse.getIsEnd())) {
                        if ("true".equals(apiResponse.getIsTruncated())) {
                            // 如果满足条件，记录日志并发起后续请求
                            log.info("Initial request is truncated, starting follow-up request...");
                            return Flux.concat(
                                    Flux.just(apiResponse.getResult()), // 确保包含当前结果
                                    sendFollowUpRequest(accessToken, topic, sup, completeResponse.toString(), uid, streamID)
                            );
                        } else {
                            log.info("第一次请求中结束流");
                            saveMarkdownToFile(completeResponse.toString(), topic, uid, streamID);
                            return Flux.just(apiResponse.getResult() + "\nstreamID:" + streamID);

                        }
                    } else {
                        // 否则，只返回当前结果
                        return Flux.just(apiResponse.getResult());
                    }
                });
    }


    private Flux<String> sendFollowUpRequest(String accessToken, String topic, String sup, String previousResponse, Integer uid, String streamID) {
        String requestBody = buildRequestBody(topic, sup, previousResponse);
        StringBuilder completeResponse = new StringBuilder(previousResponse);
        log.info("第二次请求发送成功");
        return this.webClient.post()
                .uri(botURL + "?access_token=" + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(ApiResponse.class)
                .flatMap(apiResponse -> {
                    completeResponse.append(apiResponse.getResult());
                    // 检查是否结束
                    if ("true".equals(apiResponse.getIsEnd())) {
                        log.info("第二次请求中结束流");
                        saveMarkdownToFile(completeResponse.toString(), topic, uid, streamID);
                    }
                    return Flux.just(apiResponse.getResult());
                })
                .concatWith(Mono.just("\nstreamID:" + streamID));
    }

    private String buildRequestBody(String topic, String sup) {
        JSONObject jsonObject = JSONObject.parseObject("{\"messages\":[" + getFirstRequest(topic, sup) + "],"
                + "\"temperature\":0.8,"
                + "\"top_p\":0.8,"
//                + "\"system\":\"你是一名资深的文章撰写专家，可以完成复杂的、长文本的生成工作。\""
                + "\"stream\":true"
                + "}");
        return jsonObject.toJSONString();
    }

    private String buildRequestBody(String topic, String sup, String previousResponse) {
        String firstRequest = getFirstRequest(topic, sup) + ",";
        String firstReponse = " {\"role\": \"assistant\",\"content\": \"" + previousResponse + "\"},";
        String secondRequest = " {\"role\": \"user\",\"content\": \"继续\"}";
        String total = "{\"messages\":[" + firstRequest + firstReponse + secondRequest + "],"
                + "\"temperature\":0.8,"
                + "\"top_p\":0.8,"
//                + "\"system\":\"你是一名资深的文章撰写专家，可以完成复杂的、长文本的生成工作。\""
                + "\"stream\":true"
                + "}";
        JSONObject jsonObject = JSONObject.parseObject(total);
        return jsonObject.toJSONString();
    }

    private String getFirstRequest(String topic, String sup) {
        return "{\"role\":\"user\",\"content\":\"指令:你是一名资深的文章撰写专家，可以完成复杂的、长文本的生成工作。现在请你，撰写一份标题为" + topic + "的文档。下面是用户提供的信息，你需要把信息融入到PPT内容中，要包含所有信息，不要自己杜撰\n" +
                "背景信息:用户提供的信息为：" + sup +
                "输出格式:输出内容样式必须采用以下template样式,不要包含除了文章以外任何文字，链接中不能出现中文，一定要翻译成英文！正文使用中文，除非用户有额外要求，正文使用中文，不需要出现结语。其中##为章节，输出内容中至少要有4个章节标题及下面内容!其中##为章节，输出内容中至少要有4个章节标题及下面内容!其中##为章节，输出内容中至少要有4个章节标题及下面内容!：\n" +
                "输出格式:输出内容样式必须采用以下template样式,不要包含除了文章以外任何文字，链接中不能出现中文，一定要翻译成英文！正文使用中文，除非用户有额外要求，正文使用中文，不需要出现结语。其中##为章节，输出内容中至少要有4个章节标题及下面内容!其中##为章节，输出内容中至少要有4个章节标题及下面内容!其中##为章节，输出内容中至少要有4个章节标题及下面内容!：\n" +
                "\n" +
                "template:\n" +
                "\n" +
                "# 演示标题(自拟)：演示副标题(自拟)\n" +
                "- !(将这个括号里的内容替换成本文的中心对象的英文，是一个简洁的名词，只要一个单词，不要其余形容词，一定要用用小括号和两个感叹号包围)!\n" +
                "## 1.章节标题(自拟)：章节副标题(自拟)。（每个##下面必须有4个###。每个##下面必须有4个###。每个##下面必须有4个###。）\n" +
                "以下是###的内容示例，有两种\n" +
                "### **(1)章节内容(自拟)**\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题。\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题。\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题（此点可有可无，由具体内容决定）。\n" +
                "### **(2)章节内容(自拟)**\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题。\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题。\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题（此点可有可无，由具体内容决定）。\n" +
                "### **(3)章节内容(自拟)**\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题。\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题。\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题。\n" +
                "### **(4)章节内容(自拟)**\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题。\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题。\n" +
                "- **内容标题(自拟)**: 内容详述，15个字到25个字。此-必须必须包含加粗的内容标题。\n" +
                "## 2.章节标题(自拟)：章节副标题(自拟)（下面的内容参照上面第一章的模板来输出。每个##下面必须有4个###。每个##下面必须有4个###。每个##下面必须有4个###。需要有章节标题及内容和链接!内容参照上面的模板!）" +
                "## 3.章节标题(自拟)：章节副标题(自拟)（下面的内容参照上面第一章的模板来输出。每个##下面必须有4个###。每个##下面必须有4个###。每个##下面必须有4个###。需要有章节标题及内容和链接!内容参照上面的模板!）" +
                "## 4.章节标题(自拟)：章节副标题(自拟)（下面的内容参照上面第一章的模板来输出。每个##下面必须有4个###。每个##下面必须有4个###。每个##下面必须有4个###。需要有章节标题及内容和链接!内容参照上面的模板!）" +
                "输出完四个章节后不需要任何输出，请注意。\"}";

    }
    private void saveMarkdownToFile(String markdownContent, String topic, Integer uid, String streamID) {
        Path filePath = Paths.get(markdownPath + streamID + ".md");
        try {
            Files.writeString(filePath, markdownContent);
            markdownGenMapper.insertMarkdown(streamID + ".md", uid, streamID, LocalDateTime.now());
            log.info("请求完成，md已保存");

        } catch (Exception e) {
            log.error("发生错误，用户{}的md文件保存失败:{}", uid, e.getStackTrace());
        }
    }
}
