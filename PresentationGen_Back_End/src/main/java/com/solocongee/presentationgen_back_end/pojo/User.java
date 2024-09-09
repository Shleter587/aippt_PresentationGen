package com.solocongee.presentationgen_back_end.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//用户 JavaBean
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer uid;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private String profilePictureAddr;
    private String createTime;
}
