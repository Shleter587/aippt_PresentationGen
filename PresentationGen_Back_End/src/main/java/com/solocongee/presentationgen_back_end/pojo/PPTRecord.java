package com.solocongee.presentationgen_back_end.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 用于恢复ppt的编辑状态
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PPTRecord {
    /**
     * PPT ID
     */
    private Integer id;
    /**
     * 用户编号uid
     */
    private Integer uid;
    /**
     * 主题
     */
    private String topic;
    /**
     * 保存图片url
     */
    private String imageURL;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 流编号，也是这个ppt的编号
     */
    private String streamID;
    /**
     * 保留大纲信息
     */
    private MarkdownData markdownData;
    /**
     * 保留模板信息
     */
    private ArrayList<Integer> templates;

}
