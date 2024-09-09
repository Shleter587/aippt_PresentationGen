package com.solocongee.presentationgen_back_end.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.solocongee.presentationgen_back_end.mapper.MarkdownGenMapper;
import com.solocongee.presentationgen_back_end.mapper.PPTGenMapper;
import com.solocongee.presentationgen_back_end.mapper.TemplateMapper;
import com.solocongee.presentationgen_back_end.pojo.MarkdownData;
import com.solocongee.presentationgen_back_end.pojo.PPTRecord;
import com.solocongee.presentationgen_back_end.pojo.Pair;
import com.solocongee.presentationgen_back_end.utils.AliOSSUtils;
import com.solocongee.presentationgen_back_end.utils.MarkdownParser;
import com.solocongee.presentationgen_back_end.utils.MergePPT;
import com.solocongee.presentationgen_back_end.utils.PPTXDataModifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PPTGenService {
    private final PPTGenMapper pptGenMapper;

    private final MarkdownGenMapper markdownGenMapper;

    private final TemplateMapper templateMapper;

    private final AliOSSUtils aliOSSUtils;

    @Value("${path.markdown}")
    private String markdownPath;

    @Value("${path.record}")
    private String recordPath;

    @Value("${path.templates.ppt}")
    private String pptPath;

    @Value("${path.templates.chap}")
    private String chapPath;

    public PPTGenService(PPTGenMapper pptGenMapper, TemplateMapper templateMapper, MarkdownGenMapper markdownGenMapper, AliOSSUtils aliOSSUtils) {
        this.pptGenMapper = pptGenMapper;
        this.markdownGenMapper = markdownGenMapper;
        this.templateMapper = templateMapper;
        this.aliOSSUtils = aliOSSUtils;
    }

    /**
     * 初始化ppt，即解析md文件并做第一次保存
     *
     * @param pptRecord
     * @return
     * @throws IOException
     */
    public PPTRecord initializePPT(PPTRecord pptRecord) throws IOException {
        String streamID = pptRecord.getStreamID();
        //解析md文件
        String markdownFile = markdownPath + markdownGenMapper.getMarkdownPath(streamID);
        MarkdownData markdownData;
        try {
            log.info(markdownFile);
            markdownData = MarkdownParser.parseMarkdownFile(markdownFile);
            // 解析文件后删除文件
            Files.deleteIfExists(Paths.get(markdownFile));
            log.info("解析完成删除Markdown文件: {}", markdownFile);
        } catch (IOException e) {
            log.error(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
        pptRecord.setMarkdownData(markdownData);
        savePPT(pptRecord);

        pptRecord.setId(pptGenMapper.selectByFilename(markdownData.getParseMarkdownText().get(0).getKey()
                + "_" + streamID));
        return pptRecord;
    }

    /**
     * 保存序列化pptrecord对象到文件
     *
     * @param pptRecord
     * @return
     */
    public boolean savePPT(PPTRecord pptRecord) {
        //获取主题
        String topic = pptRecord.getMarkdownData().getParseMarkdownText().get(0).getKey();
        //获取ppt封面
        String url = templateMapper.selectTemplateImage(pptRecord.getTemplates().get(0));

        //设置序列化文件名
        String filename = "";
        if (pptRecord.getStreamID() != null) {
            filename = topic + "_" + pptRecord.getStreamID();
            pptGenMapper.initializePPT(pptRecord.getUid(), topic, url, filename, LocalDateTime.now());
            pptRecord.setStreamID(null);
        } else {
            filename = topic + "_" + UUID.randomUUID();
            if (pptGenMapper.updatePPTRecord(pptRecord.getId(), topic, url, filename, LocalDateTime.now()) != 1) {
                return false;
            }
        }

        String jsonString = JSON.toJSONString(pptRecord, JSONWriter.Feature.PrettyFormat);
        // 将 JSON 字符串写入到文件
        Path path = Paths.get(recordPath + filename);
        try {
            Files.write(path, jsonString.getBytes());
        } catch (IOException e) {
            log.error(Arrays.toString(e.getStackTrace()));
            return false;
        }
        return true;
    }

    /**
     * 从文件中反序列化pptrecord
     *
     * @param id
     * @return
     */
    public PPTRecord loadPPT(Integer id, Integer uid) {
        try {
            Path path = Paths.get(recordPath + pptGenMapper.getPPTRecordPathByID(id, uid));
            String jsonString = new String(Files.readAllBytes(path));
            return JSON.parseObject(jsonString, PPTRecord.class);
        } catch (IOException e) {
            log.error("Error reading from file: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成ppt!!!
     *
     * @param markdownData
     * @param templates
     * @return
     * @throws IOException
     */
    public String genPPT(MarkdownData markdownData, ArrayList<Integer> templates) throws IOException {
        ArrayList<Path> templatePaths = new ArrayList<>();

        //表示templates数组已处理到第几位
        int pos = 0;
        String PPTTemplateName = templateMapper.selectPPTTemplateByID(templates.get(pos++));

        //添加封面页
        templatePaths.add(Paths.get(pptPath + PPTTemplateName + "_Topic.pptx"));

        for (int i = 1; i < markdownData.getParseMarkdownText().size(); i++) {
            //获取章节
            Pair chapter = markdownData.getParseMarkdownText().get(i);
            //添加章节封面
            templatePaths.add(Paths.get(pptPath + PPTTemplateName + "_Ch" + i + "_Cover.pptx"));
            int sectionCount = (chapter.getValues().size() - 1) / 2;
            //除了最后一张，全都用2点有图
            for (int j = 1; j <= sectionCount; j += 2) {
                try {
                    String chapterTemplateFileName = templateMapper.selectChapTemplateByID(templates.get(pos++), i, j);
                    log.info("查询chap为{},section为{},的章节模板结果为{}", i, j, chapterTemplateFileName);
                    templatePaths.add(Paths.get(chapPath + chapterTemplateFileName));
                } catch (Exception e) {
                    log.error(Arrays.toString(e.getStackTrace()));
                }
            }
        }
        //添加致谢页
        templatePaths.add(Paths.get(pptPath + PPTTemplateName + "_Thanks.pptx"));
        byte[] PPT = MergePPT.mergePPT(templatePaths, markdownData.getMainObject());
        ByteArrayInputStream bais = new ByteArrayInputStream(PPT);
        return aliOSSUtils.uploadPPT(PPTXDataModifier.writeTextIntoPPTX(new XMLSlideShow(bais), markdownData.getParseMarkdownText()));
    }

    /**
     * 根据id删除ppt记录
     *
     * @param id
     * @return
     */
    public Integer deletePPT(Integer id, Integer uid) {
        log.info("删除id为{}的ppt记录", id);
        File file = new File(recordPath + pptGenMapper.getPPTRecordPathByID(id, uid));
        if (file.exists()) {
            if (file.delete()) {
                log.info("文件已成功删除");
                return pptGenMapper.deleteByID(id);
            } else {
                log.error("文件无法删除");
                return 0;
            }
        } else {
            log.error("文件不存在");
            return 0;
        }
    }

    /**
     * 查询用户pptrecord
     *
     * @param uid
     * @return
     */
    public List<PPTRecord> selectPPTByUID(Integer uid) {
        log.info("查询uid为{}的用户的ppt记录", uid);
        return pptGenMapper.selectByUID(uid);
    }
}
