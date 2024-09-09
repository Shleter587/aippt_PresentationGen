package com.solocongee.presentationgen_back_end.utils;

import com.solocongee.presentationgen_back_end.pojo.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * PPTX文件修改器，用于修改pptx文件中内容
 *
 * @version: V1.0
 * @author: 黄文其
 * @date: 2024-04-17
 */
@Slf4j
public class PPTXDataModifier {

    private PPTXDataModifier() {
        // Prevent instantiation
    }

    /**
     * 对整个ppt进行文本填写
     *
     * @param ppt  要操作的ppt
     * @param text 要填写的文本
     */
    public static byte[] writeTextIntoPPTX(XMLSlideShow ppt, ArrayList<Pair> text) throws IOException {
        Map<String, String> dict = buildDict(text);

        ppt.getSlides().forEach(slide -> {
            slide.getShapes().forEach(shape -> {
                if (shape instanceof XSLFTextShape textShape && dict.containsKey(textShape.getText().trim()) && !(dict.get(textShape.getText().trim()).isEmpty())) {
                    updateTextShape(textShape, dict.get(textShape.getText().trim()));
                }
            });
        });
//        ppt.write(new FileOutputStream("D:\\Download\\Java\\proj\\PresentationGen\\data\\test.pptx"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ppt.write(baos);
        return baos.toByteArray();
    }

    /**
     * 更新文本框内容
     */
    private static void updateTextShape(XSLFTextShape textShape, String newText) {
        XSLFTextParagraph originalParagraph = textShape.getTextParagraphs().get(0);
        XSLFTextRun originalTextRun = originalParagraph.getTextRuns().get(0);

        double fontSize = originalTextRun.getFontSize();
        PaintStyle fontColor = originalTextRun.getFontColor();
        boolean isBold = originalTextRun.isBold();
        boolean isItalic = originalTextRun.isItalic();
        String fontFamily = originalTextRun.getFontFamily();

        String[] parts = newText.split("\\*\\*");
        for (int i = 0; i < parts.length; i++) {
            XSLFTextRun newTextRun = originalParagraph.addNewTextRun();
            newTextRun.setText(parts[i]);
            newTextRun.setFontColor(fontColor);
            newTextRun.setFontSize(fontSize);
            newTextRun.setBold(i % 2 == 1 || isBold);
            newTextRun.setItalic(isItalic);
            newTextRun.setFontFamily(fontFamily);
        }
        originalTextRun.setText("");
    }
    /**
     * 构建文本字典
     */
    private static Map<String, String> buildDict(ArrayList<Pair> text) {
        Map<String, String> dict = new HashMap<>();
        // 添加主标题部分
        dict.put("主标题", text.get(0).getKey());
        dict.put("主标题描述", text.get(0).getValues().get(0));

        // 循环添加子标题部分
        for (int i = 1; i < text.size(); i++) {
            StringBuilder tmp1 = new StringBuilder();
            tmp1.append("子标题").append(i);
            dict.put(String.valueOf(tmp1), text.get(i).getKey());
            tmp1.append("描述");
            dict.put(String.valueOf(tmp1), text.get(i).getValues().get(0));

            // 循环添加子子标题部分
            for (int j = 1; 2 * j < text.get(i).getValues().size(); j++) {
                StringBuilder tmp2 = new StringBuilder();
                tmp2.append("子").append(i).append("子标题").append(j);
                dict.put(String.valueOf(tmp2), text.get(i).getValues().get(2 * j - 1));
                tmp2.append("内容");
                dict.put(String.valueOf(tmp2), text.get(i).getValues().get(2 * j));
            }
        }
        return dict;
    }
}
