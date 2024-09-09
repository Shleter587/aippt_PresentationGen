package com.solocongee.presentationgen_back_end.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文心一言响应类，我们只关心其中三个字段,
 * result:返回的文段,is_end:是否是本次响应的末尾,is_truncated:本次响应是否在此处被截断
 *使用用@JsonProperty注解来指定JSON字段的名称，从而确保正确的映射，同时保持驼峰命名。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String result;

    @JsonProperty("is_end")
    private String isEnd;

    @JsonProperty("is_truncated")
    private String isTruncated;
}
