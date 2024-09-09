package com.solocongee.presentationgen_back_end.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * markdown大纲生成请求类
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MDGenRequest {
    private String topic;
    private String sup;
}
