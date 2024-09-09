package com.solocongee.presentationgen_back_end.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkdownData {
    /**
     * 解析后的ppt结构化文本
     */
    private ArrayList<Pair> parseMarkdownText;
    /**
     * 解析后ppt的中心对象
     */
    private String mainObject;

}
