package com.example.spider;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.ColorPalette;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import us.codecraft.webmagic.Spider;

import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class SpiderApplicationTests {

    @Autowired
    DouBanMapper douBanMapper;

    @Autowired
    DoubanDaoPipeline doubanDaoPipeline;

    @Autowired
    DouBanCommentsProcessor processor;

    @Test
    void contextLoads() {
        DouBanCommentDO douBanCommentDO = new DouBanCommentDO();
        douBanCommentDO.setUser("\uD83C\uDF08直须折");
        douBanCommentDO.setStar("1");
        douBanCommentDO.setComment_date_time(new Date());
        douBanCommentDO.setComment_text("asdas");
        douBanMapper.insertBankCard(douBanCommentDO);
        Assertions.assertNotNull(douBanCommentDO.getId());
    }

    @Test
    public void testCrawl() {
        String startURL = "https://movie.douban.com/subject/30488569/comments?limit=20&sort=new_score&status=P";
        String test_ur = "https://movie.douban.com/subject/30488569/comments?start=220&limit=20&sort=new_score&status=P";

        Spider.create(processor)
                .addUrl(startURL)
                .addPipeline(doubanDaoPipeline)
                //开启5个线程抓取
                .thread(1)
                //启动爬虫
                .run();
    }

    @SneakyThrows
    @Test
    public void worldCloudTest() {

        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        // 设置词频
        frequencyAnalyzer.setWordFrequenciesToReturn(600);
        frequencyAnalyzer.setMinWordLength(2);
        // 中文分词
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
        // 过滤一些预期助词等
        List stopWorlds = FileUtils.readLines(new File("stopworld/cn_stopwords.txt"));
        // 过滤词
        frequencyAnalyzer.setStopWords(stopWorlds);

        List<DouBanCommentDO> commentDOS = douBanMapper.queryAll();

        List<String> comments = commentDOS.stream().map(DouBanCommentDO::getComment_text).collect(Collectors.toList());

        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(comments);

        final Dimension dimension = new Dimension(500, 312);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        // 词语背景图
        wordCloud.setBackground(new PixelBoundryBackground("pic/whale_small.png"));
        // 设置颜色
        wordCloud.setColorPalette(new ColorPalette(new Color(0xC73939), new Color(0x10E71C), new Color(0x0984D5), new Color(0x73CB0E), new Color(0x40D3F1), new Color(0xFFC97915, true)));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile("worldCloud/test.png");
    }

    @Test
    public void queryAll() {
        List<DouBanCommentDO> commentDOS = douBanMapper.queryAll();
        Assertions.assertEquals(commentDOS.size(), 500);

    }

}
