package com.solocongee.presentationgen_back_end.mapper;

import com.solocongee.presentationgen_back_end.pojo.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AuthMapper {
    @Select("select * from user_table where username = #{name}")
    List<User> selectUserByName(String name);

    @Select("select * from user_table where phone_number = #{phone}")
    List<User> selectUserByPhone(String phone);

    @Select("select * from user_table where uid = #{id}")
    User selectUserByUid(int uid);

    @Insert("insert into user_table(username, password, create_time) values(#{name},#{pwd},#{createTime})")
    void createUserByPwd(String name, String pwd, LocalDateTime createTime);

    @Insert("insert into user_table(username, phone_number, create_time) values(#{phone},#{phone},#{createTime})")
    void createUserByPhone(String phone, LocalDateTime createTime);

    @Update("update user_table set password = #{newPassword} where uid = #{uid}")
    void updateUserPassword(int uid, String newPassword);

    @Update("update user_table set profile_picture_addr = #{url} where uid = #{uid}")
    void updateUserProfilePicture(Integer uid, String url);

    @Select("SELECT profile_picture_addr FROM user_table WHERE uid = #{uid}")
    String selectProfilePictureByID(Integer uid);

    @Update("update pgen.user_table set phone_number = #{phone} where uid = #{uid}")
    void updateUserPhone(@Param("uid") int uid, @Param("phone") String newPhone);
}
