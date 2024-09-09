package com.solocongee.presentationgen_back_end.controller;

import com.solocongee.presentationgen_back_end.pojo.MarkdownData;
import com.solocongee.presentationgen_back_end.pojo.PPTRecord;
import com.solocongee.presentationgen_back_end.pojo.Result;
import com.solocongee.presentationgen_back_end.service.PPTGenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

//@CrossOrigin("http://localhost:8081")
@CrossOrigin("*")
@Slf4j
@RestController
public class PPTGenController {

    private final PPTGenService pptGenService;

    public PPTGenController(PPTGenService pptGenService) {
        this.pptGenService = pptGenService;
    }

    /**
     * 查询用户ppt生成历史
     *
     * @param request
     * @return
     */
    @PostMapping("/api/ppt-record")
    public Result selectPPTByUID(HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");
        return Result.success(pptGenService.selectPPTByUID(uid));
    }

    /**
     * 初始化ppt，即解析md文件并做第一次保存
     *
     * @param pptRecord
     * @return
     */
    @PostMapping("/api/init-ppt")
    public Result initializePPT(@RequestBody PPTRecord pptRecord, HttpServletRequest request) throws IOException {
        Integer uid = (Integer) request.getAttribute("uid");
        pptRecord.setUid(uid);
        return Result.success(pptGenService.initializePPT(pptRecord));
    }

    /**
     * 保存序列化pptrecord对象到文件
     *
     * @param pptRecord
     * @return
     */
    @PostMapping("/api/save-ppt")
    public Result savePPT(@RequestBody PPTRecord pptRecord, HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");
        pptRecord.setUid(uid);
        if (pptGenService.savePPT(pptRecord)) {
            return Result.success("save succeed");
        } else {
            return Result.error("save failed");
        }
    }

    /**
     * 从文件中反序列化pptrecord
     *
     * @param pptRecord
     * @return
     */
    @PostMapping("/api/load-ppt")
    public Result loadPPT(@RequestBody PPTRecord pptRecord, HttpServletRequest request) {
        PPTRecord pptRecord1 = pptGenService.loadPPT(pptRecord.getId(), (Integer) request.getAttribute("uid"));
        return pptRecord1 == null ? Result.error("load failed") : Result.success(pptRecord1);
    }

    /**
     * 根据id删除ppt
     *
     * @param pptRecord
     * @return
     */
    @PostMapping("/api/delete-ppt")
    public Result deletePPTRecord(@RequestBody PPTRecord pptRecord, HttpServletRequest request) {
        if (pptGenService.deletePPT(pptRecord.getId(), (int) request.getAttribute("uid")) > 0) {
            return Result.success("delete succeed");
        } else {
            return Result.error("delete failed");
        }
    }

    @PostMapping("/api/gen-ppt")
    public Result generatePPTX(@RequestBody PPTRecord pptRecord) throws IOException {
        MarkdownData markdownData = pptRecord.getMarkdownData();
        String url = pptGenService.genPPT(markdownData, pptRecord.getTemplates());
        return Result.success(url);
    }
}





