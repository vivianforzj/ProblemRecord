package us.gfzj.web.config;

import us.gfzj.util.SpringContextUtils;
import us.gfzj.web.config.bean.WebElasticsearchBean;
import us.gfzj.web.config.bean.WebElasticsearchIndexFormatBean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoujing
 * Create on 19-2-1
 */
public class WebElasticsearchConf {
    private static WebElasticsearchIndexFormatBean indexFormatConfBean = SpringContextUtils.getBean(WebElasticsearchIndexFormatBean.class);
    private static WebElasticsearchBean webElasticsearchBean = SpringContextUtils.getBean(WebElasticsearchBean.class);

    private static Map<String, String> ELASTICSEARCH_CONF = new HashMap<>();

    static {
        try {
            for (Field field : indexFormatConfBean.getClass().getDeclaredFields()) {
                String name = field.getName();
                String keyFormat = "%s.article.es.index.name.format";
                String key = null;
                if (name.startsWith("qa")) {
                    key = String.format(keyFormat, "qa");
                } else if (name.startsWith("weibo")) {
                    key = String.format(keyFormat, "weibo");
                } else if (name.startsWith("weixin")) {
                    key = String.format(keyFormat, "weixin");
                }// TODO add more channel

                if (key != null) {
                    field.setAccessible(true);
                    ELASTICSEARCH_CONF.put(key, (String) field.get(indexFormatConfBean));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static String getElasticsearchConf(String key) {
        return ELASTICSEARCH_CONF.getOrDefault(key, null);
    }

    public static String getESMappingSubIdsAndSeedpkgIdsIndex() {
        return webElasticsearchBean.getEsMappingSubIdsAndSeedpkgIdsIndex();
    }
}
