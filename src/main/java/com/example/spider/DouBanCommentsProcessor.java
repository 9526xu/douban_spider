package com.example.spider;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;

/**
 * @author andyXu xu9529@gmail.com
 * @date 2020/4/11
 */
@Component
@Slf4j
public class DouBanCommentsProcessor implements PageProcessor {

    // 设置出错之后重试次数，休眠时间一定要设置，防止频繁抓取，豆瓣封禁 IP
    private Site site = Site.me().setRetryTimes(3).setTimeOut(10000).setSleepTime(5000);


    @SneakyThrows
    @Override
    public void process(Page page) {
        List<String> commentList = page.getHtml().xpath("//div[@class=\"comment\"]").all();
        List<DouBanCommentDO> douBanCommentDTOS = new ArrayList<>();
        for (String comment : commentList) {
            Html commentHtml = Html.create(comment);
            // 用户名
            String user = commentHtml.xpath("//h3/span[2]/a/text()").get();
            // 分数
            String star = commentHtml.xpath("//h3/span[2]/span[2]/@class").get();
            // 评分时间
            String date_time = commentHtml.xpath("//h3/span[2]/span[3]/@title").get();
            String date = commentHtml.xpath("//h3/span[2]/span[3]").get();


            // 短评内容
            String comment_text = commentHtml.xpath("//p/span/text()").get();


            DouBanCommentDO douBanCommentDTO = new DouBanCommentDO();
            douBanCommentDTO.setUser(user);
            douBanCommentDTO.setComment_text(comment_text);
            if (StringUtils.isNotBlank(date_time)) {
                // 时间格式 2020-04-01 22:17:25
                douBanCommentDTO.setComment_date_time(DateUtils.parseDate(date_time, "yyyy-MM-dd HH:mm:ss"));
            } else if (StringUtils.isNotBlank(date)) {
                douBanCommentDTO.setComment_date_time(DateUtils.parseDate(date, "yyyy-MM-dd"));
            }
            douBanCommentDTO.setStar(star);
            douBanCommentDTOS.add(douBanCommentDTO);
        }
        page.putField("comment", douBanCommentDTOS);
        // 获取下一页的连接
        String nextPageUrl = page.getHtml().xpath("//div[@id='paginator']/a[@class='next']").links().get();
        log.info("下一页地址：{}", nextPageUrl);
        if (StringUtils.isNotBlank(nextPageUrl)) {
            // 将下一页地址存入 page 这样才会继续爬取
            page.addTargetRequest(nextPageUrl);
        }
    }

    @Override
    public Site getSite() {
        site.setUserAgent("User-Agent\": 'Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)");
        // String cookie = "bid=\\\"/RzD1uURnmI\\\"; ll=\\\"118172\\\"; __utmc=30149280; __utmz=30149280.1586610007.2.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); _vwo_uuid_v2=D836FC05A63D3DEBE716909286F8693D7|e0b022f2afc82be76e5dfe42a1b2c52e; push_noty_num=0; push_doumail_num=0; __utmv=30149280.13838; _pk_ref.100001.8cb4=%5B%22%22%2C%22%22%2C1586612639%2C%22https%3A%2F%2Fmovie.douban.com%2Fsubject%2F30488569%2Fcomments%3Fstatus%3DP%22%5D; _pk_id.100001.8cb4=4dcf0013477c9a9a.1586610284.2.1586612639.1586610292.; ap_v=0,6.0; __utmb=30149280.1.10.1586617300; dbcl2=\\\"138387248:ooYtD9xqqKg\\\"; gr_user_id=d18e1cb3-3a13-48bf-9ea1-fda387e17ab8; __utma=30149280.1312134237.1525245318.1525245318.1541227206.2; ck=yLyS; bid=opYwre3cNYg";
        //site.addHeader("Cookie", "bid=\"/RzD1uURnmI\"; ll=\"118172\"; __utmc=30149280; __utmz=30149280.1586610007.2.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); _vwo_uuid_v2=D836FC05A63D3DEBE716909286F8693D7|e0b022f2afc82be76e5dfe42a1b2c52e; push_noty_num=0; push_doumail_num=0; __utmv=30149280.13838; _pk_ref.100001.8cb4=%5B%22%22%2C%22%22%2C1586612639%2C%22https%3A%2F%2Fmovie.douban.com%2Fsubject%2F30488569%2Fcomments%3Fstatus%3DP%22%5D; _pk_id.100001.8cb4=4dcf0013477c9a9a.1586610284.2.1586612639.1586610292.; ap_v=0,6.0; __utmb=30149280.1.10.1586617300; dbcl2=\"138387248:ooYtD9xqqKg\"; gr_user_id=d18e1cb3-3a13-48bf-9ea1-fda387e17ab8; __utma=30149280.1312134237.1525245318.1525245318.1541227206.2; ck=yLyS; bid=opYwre3cNYg");
        site.addHeader("Cookie", "bid=\"/RzD1uURnmI\"; ll=\"118172\"; __utmc=30149280; __utmz=30149280.1586610007.2.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); __utmc=223695111; __utmz=223695111.1586610007.2.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); _vwo_uuid_v2=D836FC05A63D3DEBE716909286F8693D7|e0b022f2afc82be76e5dfe42a1b2c52e; push_noty_num=0; push_doumail_num=0; __utmv=30149280.13838; dbcl2=\"138387248:ooYtD9xqqKg\"; ck=yLyS; ap_v=0,6.0; __utma=30149280.1681532622.1586214292.1586652046.1586655137.6; __utmb=30149280.0.10.1586655137; __utma=223695111.1466079061.1586214292.1586652046.1586655137.6; __utmb=223695111.0.10.1586655137; _pk_ref.100001.4cf6=%5B%22%22%2C%22%22%2C1586655137%2C%22https%3A%2F%2Fwww.google.com%2F%22%5D; _pk_ses.100001.4cf6=*; _pk_id.100001.4cf6=617b6a48f75c0239.1586214292.6.1586655684.1586652100.");

        return site;
    }

    public static void main(String[] args) {

    }
}
