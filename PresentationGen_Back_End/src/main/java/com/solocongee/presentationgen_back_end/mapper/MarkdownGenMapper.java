package com.solocongee.presentationgen_back_end.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;

@CrossOrigin("*")
@Mapper
public interface MarkdownGenMapper {

    /**
     * 插入md文件记录
     *
     * @param streamID
     * @param uid
     */
    @Insert("insert pgen.markdown_table (filename, uid, stream_id, create_time) value (#{filename},#{uid},#{streamID},#{createTime}) ")
    void insertMarkdown(String filename, Integer uid, String streamID, LocalDateTime createTime);

    @Select("select filename from pgen.markdown_table where stream_id=#{streamID}")
    String getMarkdownPath(String streamID);

}
