package us.gfzj.web.config.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhoujing
 * Create on 19-2-1
 */
@Component
public class SubQueryConfBean {
    @Value("${subQuery.max.num:10}")
    private int maxsubQueryNum;

    public int getMaxSubQueryNum() {
        return maxsubQueryNum;
    }

}
