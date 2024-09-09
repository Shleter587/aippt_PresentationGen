package com.solocongee.presentationgen_back_end.mapper;

import com.solocongee.presentationgen_back_end.pojo.ChapTemplate;
import com.solocongee.presentationgen_back_end.pojo.PPTTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TemplateMapper {

    List<PPTTemplate> PPTQuery(String colour, String tag);

    List<ChapTemplate> ChapQuery(String colour, Short point, Boolean hasPicture);

    @Select("select image from pgen.template_table where id=#{id}")
    String selectTemplateImage(Integer id);

    @Select("select template_name from pgen.template_table where id=#{id}")
    String selectPPTTemplateByID(Integer id);

    @Select("select filename from pgen.chapter_template_search_table where (id,chapter,section)=(#{id},#{chapter},#{section})")
    String selectChapTemplateByID(Integer id, Integer chapter, Integer section);

    @Update("update pgen.template_table set pgen.template_table.image=#{url} where pgen.template_table.template_name=#{name}")
    void updatePiatureByName(String name,String url);
}
