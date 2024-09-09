package com.solocongee.presentationgen_back_end.utils;

import com.solocongee.presentationgen_back_end.pojo.MarkdownData;
import com.solocongee.presentationgen_back_end.pojo.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {
    /**
     * @param filePath
     * @return
     * @throws IOException
     * @MarkdownData类包含: parseMarkdownText解析后的ppt结构化文本
     * parseMarkdownFrameWork 解析后的ppt结构
     * parseMarkdownPic解析后的ppt图片链接
     */
    public static MarkdownData parseMarkdownFile(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        ArrayList<Pair> text = new ArrayList<>();
        String pic = "";

        /*
          网址正则表达式
         */
        String regex = "!\\(([^)]+)\\)!";
        Pattern pattern = Pattern.compile(regex);

        Pair currentEntry = null;
        ArrayList<String> currentList = null;

        StringBuilder paragraph = new StringBuilder();

        for (String line : lines) {
            if (line.startsWith("###")) {
                if (currentList != null && !paragraph.isEmpty()) {
                    String tpParagraph = paragraph.toString();
                    currentList.add(tpParagraph.substring(0, tpParagraph.length() - 1));
                    paragraph.setLength(0);
                }
                if (currentList != null) {
                    currentList.add(line.substring(3).trim());
                }
            } else if (line.startsWith("##")) {
                if (currentList != null && !paragraph.isEmpty()) {
                    String tpParagraph = paragraph.toString();
                    currentList.add(tpParagraph.substring(0, tpParagraph.length() - 1));
                    paragraph.setLength(0);

                }
                if (currentEntry != null) {
                    text.add(currentEntry);
                }
                String[] parts = line.substring(2).split("：", 2);
                currentList = new ArrayList<>();
                currentList.add(parts.length > 1 ? parts[1].trim() : "");
                currentEntry = new Pair(parts[0].trim(), currentList);

            } else if (line.startsWith("#")) {
                if (currentEntry != null) {
                    text.add(currentEntry);
                }
                String[] parts = line.substring(1).split("：", 2);
                currentList = new ArrayList<>();
                currentList.add(parts.length > 1 ? parts[1].trim() : "");
                currentEntry = new Pair(parts[0].trim(), currentList);
            } else if (line.startsWith("-")) {
                if (currentList != null && !currentList.isEmpty()) {
                    if (pic.isEmpty()) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            pic = matcher.group(1);
                        }
                    } else {
                        paragraph.append(line.substring(1)).append("\n");
                    }
                }
            }
        }
        if (currentList != null && !paragraph.isEmpty()) {
            String tpParagraph = paragraph.toString();
            currentList.add(tpParagraph.substring(0, tpParagraph.length() - 1));
            paragraph.setLength(0);

        }

        if (currentEntry != null) {
            text.add(currentEntry);
        }
        return new MarkdownData(text,pic);
    }

}
