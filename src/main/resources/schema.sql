DROP TABLE IF EXISTS douban_comment;

CREATE TABLE douban_comment (
id int primary key auto_increment,
gmt_create timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
gmt_update timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
`user` varchar(256)  DEFAULT '' COMMENT '豆瓣用户名',
star varchar(256)  DEFAULT '' COMMENT '分数',
comment_date_time timestamp  COMMENT '评分时间',
comment_text varchar(256) DEFAULT '' COMMENT '短评内容'
);
