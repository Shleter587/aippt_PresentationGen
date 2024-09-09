package com.solocongee.presentationgen_back_end.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ChapTemplateMapper {
    @Insert("INSERT INTO pgen.chapter_template_search_table(id, filename, chapter, section) VALUES (#{id}, #{filename}, #{chapter}, #{section})")
    void insertChapterTemplate(int id, String filename, short chapter, short section);

    @Insert("INSERT INTO pgen.chapter_template_show_table(point, has_picture, image, colour) VALUES (#{point}, #{hasPicture}, #{image},#{colour})")
    void insertChapterShow(int point, boolean hasPicture, String image, String colour);

    @Select("select id from pgen.chapter_template_show_table where image=#{image}")
    int getShowID(String image);
}

