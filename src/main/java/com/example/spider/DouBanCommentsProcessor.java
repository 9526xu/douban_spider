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
        // 首先获取 短评的整个 div ，然后循环遍历获取里面的内容
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
        // 数据存储时将会通过这个 key 提取
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
        site.addHeader("Cookie", "your own cookie");
        return site;
    }

}
