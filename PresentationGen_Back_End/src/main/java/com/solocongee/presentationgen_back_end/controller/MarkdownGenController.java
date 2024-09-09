package com.solocongee.presentationgen_back_end.controller;

import com.solocongee.presentationgen_back_end.pojo.MDGenRequest;
import com.solocongee.presentationgen_back_end.service.MarkdownGenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

//@CrossOrigin("http://localhost:8081")
@CrossOrigin("*")
@Slf4j
@RestController
public class MarkdownGenController {
    private final MarkdownGenService markdownGenService;

    @Autowired
    public MarkdownGenController(MarkdownGenService markdownGenService) {
        this.markdownGenService = markdownGenService;
    }

    @PostMapping("/api/send")
    public Flux<String> sendToApi(@RequestBody MDGenRequest mdGenRequest, HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");
        log.info("收到请求，请求参数为topic={},sup={},用户id为{}", mdGenRequest.getTopic(), mdGenRequest.getSup(), uid);
        return markdownGenService.sendRequest(mdGenRequest.getTopic(), mdGenRequest.getSup(), uid);
    }
}
