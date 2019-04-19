package us.gfzj.web.config.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhoujing
 *         Create on 19-1-9
 */
@Component
public class WebElasticsearchIndexFormatBean {

    @Value("${qa.article.es.index.name.format:qa.article.%d.%02d.%02d}")
    private String qaArticleESIndexFormat;

    @Value("${weibo.article.es.index.name.format:weibo.article.%d.%02d.%02d}")
    private String weiboArticleESIndexFormat;

}
