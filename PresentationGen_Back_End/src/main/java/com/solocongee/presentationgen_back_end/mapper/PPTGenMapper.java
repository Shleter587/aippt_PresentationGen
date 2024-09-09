package com.solocongee.presentationgen_back_end.mapper;


import com.solocongee.presentationgen_back_end.pojo.PPTRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PPTGenMapper {


    @Select("select filename from pgen.ppt_record_table where (id,uid)=(#{id},#{uid}) ")
    String getPPTRecordPathByID(Integer id,Integer uid);

    @Insert("insert into pgen.ppt_record_table " +
            "(uid, topic, image_URL, filename, update_time) " +
            "VALUE (#{uid},#{topic},#{url},#{filename},#{updateTime})")
    void initializePPT(Integer uid, String topic, String url, String filename, LocalDateTime updateTime);

    @Update("UPDATE pgen.ppt_record_table set topic=#{topic},image_URL=#{url},filename=#{filename},update_time=#{updateTime} where id=#{id}")
    Integer updatePPTRecord(Integer id, String topic, String url, String filename, LocalDateTime updateTime);

    @Select("select id from pgen.ppt_record_table where filename=#{filename}")
    Integer selectByFilename(String filename);

    @Select("select id, uid, topic, image_URL, update_time from ppt_record_table where uid=#{uid}  ORDER BY update_time DESC")
    List<PPTRecord> selectByUID(Integer uid);

    @Delete("delete from ppt_record_table where id=#{id}")
    Integer deleteByID(Integer id);
}
