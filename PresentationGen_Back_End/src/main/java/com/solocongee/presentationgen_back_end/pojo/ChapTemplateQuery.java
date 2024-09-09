package com.solocongee.presentationgen_back_end.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChapTemplateQuery {
    private String colour;
    private Short point;
    private Boolean hasPicture;
}
