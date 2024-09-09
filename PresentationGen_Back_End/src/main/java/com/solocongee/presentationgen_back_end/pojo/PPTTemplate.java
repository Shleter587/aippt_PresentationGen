package com.solocongee.presentationgen_back_end.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PPTTemplate {
    private String id;
    private String templateName;
    private String colour;
    private String image;
    private LocalDateTime createdTime;
}
