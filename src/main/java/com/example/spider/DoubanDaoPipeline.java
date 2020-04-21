package com.example.spider;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * @author andyXu xu9529@gmail.com
 * @date 2020/4/12
 */
@Component
public class DoubanDaoPipeline implements Pipeline {


    @Autowired
    DouBanMapper douBanMapper;


    @Override
    public void process(ResultItems resultItems, Task task) {
        List<DouBanCommentDO> douBanCommentDOS = resultItems.get("comment");
        if (CollectionUtils.isNotEmpty(douBanCommentDOS)) {
            for (DouBanCommentDO douBanCommentDO : douBanCommentDOS) {
                douBanMapper.insertBankCard(douBanCommentDO);
            }
        }

    }
}
