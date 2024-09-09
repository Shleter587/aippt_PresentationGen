package com.solocongee.presentationgen_back_end.controller;

import com.solocongee.presentationgen_back_end.pojo.ChapTemplateQuery;
import com.solocongee.presentationgen_back_end.pojo.PPTTemplateQuery;
import com.solocongee.presentationgen_back_end.pojo.Result;
import com.solocongee.presentationgen_back_end.service.PPTTemplateService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin("http://localhost:8081")
@CrossOrigin("*")
@RestController
public class TemplateController {
    final
    PPTTemplateService PPTTemplateService;

    public TemplateController(PPTTemplateService PPTTemplateService) {
        this.PPTTemplateService = PPTTemplateService;
    }

    @PostMapping("/api/ppt-template")
    public Result query(@RequestBody PPTTemplateQuery PPTTemplateQuery) {
        return Result.success(PPTTemplateService.PPTQuery(PPTTemplateQuery.getColour(), PPTTemplateQuery.getTag()));
    }

    @PostMapping("/api/chapter-template")
    public Result query(@RequestBody ChapTemplateQuery chapTemplateQuery) {
        return Result.success(PPTTemplateService.ChapQuery(chapTemplateQuery.getColour(), chapTemplateQuery.getPoint(), chapTemplateQuery.getHasPicture()));
    }
}
