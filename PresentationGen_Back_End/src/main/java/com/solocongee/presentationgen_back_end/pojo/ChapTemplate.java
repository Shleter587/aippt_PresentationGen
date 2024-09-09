package com.solocongee.presentationgen_back_end.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChapTemplate {
    private Integer id;
    private Short point;
    private Boolean hasPicture;
    private String image;
    private String colour;
}
