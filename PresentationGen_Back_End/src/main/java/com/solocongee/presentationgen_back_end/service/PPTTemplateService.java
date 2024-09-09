package com.solocongee.presentationgen_back_end.service;

import com.solocongee.presentationgen_back_end.mapper.TemplateMapper;
import com.solocongee.presentationgen_back_end.pojo.ChapTemplate;
import com.solocongee.presentationgen_back_end.pojo.PPTTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PPTTemplateService {
    final TemplateMapper templateMapper;

    @Autowired
    public PPTTemplateService(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    /**
     * 查询PPT模板
     *
     * @param colour
     * @param tag
     * @return
     */
    public List<PPTTemplate> PPTQuery(String colour, String tag) {
        log.info("查询颜色为{},标签为{}的PPT模板", colour, tag);
        return templateMapper.PPTQuery(colour, tag);
    }


    public List<ChapTemplate> ChapQuery(String colour, Short point, Boolean hasPicture) {
        log.info("查询颜色为{},点数为{},图片为:{} 的PPT模板", colour, point, hasPicture);
        return templateMapper.ChapQuery(colour, point, hasPicture);
    }

}
