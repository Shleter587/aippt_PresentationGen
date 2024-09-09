package com.solocongee.presentationgen_back_end.service;

import com.solocongee.presentationgen_back_end.mapper.ChapTemplateMapper;
import com.solocongee.presentationgen_back_end.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
public class ChapTempGenService {

    final
    ChapTemplateMapper chapTemplateMapper;

    final
    AliOSSUtils aliOSSUtils;

    public ChapTempGenService(AliOSSUtils aliOSSUtils, ChapTemplateMapper chapTemplateMapper) {
        this.aliOSSUtils = aliOSSUtils;
        this.chapTemplateMapper = chapTemplateMapper;
    }

    public void execute() throws Exception {
        Path dir = Paths.get("D:\\Download\\Java\\proj\\PresentationGen\\templates\\Download");
        try (Stream<Path> files = Files.list(dir)) {
            files.filter(file -> file.toString().endsWith(".pptx"))
                    .forEach(pptxFile -> {
                        String baseName = pptxFile.getFileName().toString().replaceFirst("[.][^.]+$", "");
                        log.info("basename:{}", baseName);
                        Path pngFile = dir.resolve(baseName + "_01.png");
                        if (Files.exists(pngFile)) {
                            try {
                                String url = aliOSSUtils.upload(pngFile);
                                modifyAndGenerateSlides(pptxFile, url);
                            } catch (Exception e) {
                                log.error("Error processing files: " + e.getMessage(), e);
                            }
                        }
                    });
        }
    }


    public void modifyAndGenerateSlides(Path inputPath, String url) throws Exception {
        String filename = inputPath.getFileName().toString();
        String colour = filename.substring(0, 2);
        String point = filename.substring(3, 4);
        String hasPic = filename.substring(5, 7);


        chapTemplateMapper.insertChapterShow(Integer.parseInt(point), (hasPic.equals("有图")), url, colour);
        int id = chapTemplateMapper.getShowID(url);

        int[] mValues = {1, 3, 5};
        if ("1".equals(point)) {
            mValues = new int[]{1, 2, 3, 4, 5, 6};
        }
        for (int n = 1; n <= 6; n++) {
            for (int m : mValues) {

                String outputFilename = id + colour + point + '点' + '第' + n + "章第" + m + "节" + hasPic + ".pptx";
                log.info(outputFilename);
                // 使用try-with-resources语句来确保文件输出流的正确关闭
                try (XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(String.valueOf(inputPath.toAbsolutePath())));
                     FileOutputStream out = new FileOutputStream("D:\\Download\\Java\\proj\\PresentationGen\\templates\\chap\\" + outputFilename)) {

                    // 删除首页和尾页
                    List<XSLFSlide> slides = ppt.getSlides();
                    if (ppt.getSlides().size() > 1)
                        ppt.removeSlide(0);
                    while (ppt.getSlides().size() > 1)
                        ppt.removeSlide(slides.size() - 1);

                    XSLFSlide newSlide = ppt.getSlides().get(0);
                    List<XSLFShape> shapes = newSlide.getShapes();
                    for (int i = 0; i < shapes.size(); i++) {
                        XSLFShape shape = shapes.get(i);

                        if (shape instanceof XSLFTextShape textShape) {

                            String originalText = textShape.getText();

                            String newText;
                            // 判断文本内容，如果匹配“子标题n”，则直接修改该文本框的内容
                            if (originalText.equals("子标题n")) {
                                textShape.getTextParagraphs().get(0).getTextRuns().get(0).setText("子标题" + n);
                            } else if (originalText.contains("子n子标题m内容")) {
                                // 构造新的文本内容
                                newText = "子" + n + "子标题" + m + "内容";
                                // 清空除第一个文本运行外的所有文本运行，并设置新的文本
                                clearAndSetText(textShape, newText);
                                // 创建对应的新标题文本框
                                createNewTitleBox(textShape, "子" + n + "子标题" + m, newSlide);
                            } else if (originalText.contains("子n子标题m+1内容")) {
                                // 构造新的文本内容
                                newText = "子" + n + "子标题" + (m + 1) + "内容";
                                // 清空除第一个文本运行外的所有文本运行，并设置新的文本
                                clearAndSetText(textShape, newText);
                                // 创建对应的新标题文本框
                                createNewTitleBox(textShape, "子" + n + "子标题" + (m + 1), newSlide);
                            }

                        }
                    }

                    // 写出到文件
                    ppt.write(out);
                    chapTemplateMapper.insertChapterTemplate(id, outputFilename, (short) n, (short) m);
                }
            }
        }
    }

    private static void createNewTitleBox(XSLFTextShape sourceBox, String title, XSLFSlide slide) {
        // 创建新的文本形状
        XSLFTextBox titleBox = slide.createTextBox();

        // 获取原文本框的位置
        Rectangle2D sourceAnchor = sourceBox.getAnchor();

        // 设置新文本框的位置，位于原文本框正上方，假设高度为30像素
        double newAnchorY = sourceAnchor.getY() - 30;  // 根据需要调整高度
        double newAnchorX = sourceAnchor.getX() - 8;  // 根据需要调整高度
        double newAnchorHeight = 30;  // 新文本框的高度，根据需要调整
        Rectangle2D newAnchor = new Rectangle2D.Double(newAnchorX, newAnchorY, sourceAnchor.getWidth(), newAnchorHeight);
        titleBox.setAnchor(newAnchor);

        // 清除现有的段落
        List<XSLFTextParagraph> paragraphs = titleBox.getTextParagraphs();
        for (int i = paragraphs.size() - 1; i >= 0; i--) {
            titleBox.removeTextParagraph(paragraphs.get(i));
        }

        XSLFTextRun originalTextRun = sourceBox.getTextParagraphs().get(0).getTextRuns().get(0);
        String fontFamily = originalTextRun.getFontFamily();

        double fontSize = originalTextRun.getFontSize();
        PaintStyle fontColor = originalTextRun.getFontColor();

        // 设置文本样式（根据需要调整）
        XSLFTextParagraph paragraph = titleBox.addNewTextParagraph();
        XSLFTextRun run = paragraph.addNewTextRun();
        run.setFontSize(fontSize + 2);  // 设置字号，根据需要调整
        run.setBold(true);  // 设置粗体
        run.setItalic(true);  // 设置斜体
        run.setFontColor(fontColor);  // 设置字体颜色
        run.setFontFamily(fontFamily); // 设置字体样式
        run.setText(title);
        // 设置文本框的背景颜色为透明
        titleBox.setFillColor(new Color(255, 255, 255, 0));
    }


    /**
     * Clears all text runs except the first one and sets new text in the first text run.
     *
     * @param textShape The text shape to modify.
     * @param newText   The new text to set.
     */
    private static void clearAndSetText(XSLFTextShape textShape, String newText) {
        XSLFTextParagraph paragraph = textShape.getTextParagraphs().get(0);
        List<XSLFTextRun> textRuns = paragraph.getTextRuns();
        // 遍历所有文本运行，移除除第一个之外的所有文本运行
        for (int j = textRuns.size() - 1; j > 0; j--) {
            paragraph.removeTextRun(textRuns.get(j));
        }
        // 设置第一个文本运行的新文本
        textRuns.get(0).setText(newText);
    }

}
