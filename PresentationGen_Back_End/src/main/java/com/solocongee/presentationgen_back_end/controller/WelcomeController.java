package com.solocongee.presentationgen_back_end.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于测试服务器连接
 */
//@CrossOrigin("http://localhost:8081")
@CrossOrigin("*")
@RestController
public class WelcomeController {
    @RequestMapping(value = "/")
    public String welcome() {
        return "welcome";
    }
}
