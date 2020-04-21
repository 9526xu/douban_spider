package com.example.spider;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author andyXu xu9529@gmail.com
 * @date 2020/4/12
 */
@Mapper
public interface DouBanMapper {

    // 获取自增 id
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO douban_comment (user, star,comment_date_time,comment_text) VALUES (#{user}, #{star},#{comment_date_time},#{comment_text})")
    void insertBankCard(DouBanCommentDO douban);

    @Select("select user, star,comment_date_time,comment_text  from douban_comment")
    List<DouBanCommentDO> queryAll();
}
