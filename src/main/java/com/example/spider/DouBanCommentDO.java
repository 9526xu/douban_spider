package com.example.spider;

import lombok.Data;

import java.util.Date;

/**
 * @author andyXu xu9529@gmail.com
 * @date 2020/4/11
 */
@Data
public class DouBanCommentDO {

    private Integer id;

    private String user;

    private String star;

    private Date comment_date_time;

    private String comment_text;
}
