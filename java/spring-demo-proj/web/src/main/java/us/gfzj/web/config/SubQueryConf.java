package us.gfzj.web.config;

import us.gfzj.util.SpringContextUtils;
import us.gfzj.web.config.bean.SubQueryConfBean;

/**
 * @author zhoujing
 * Create on 19-2-1
 */
public class SubQueryConf {
    private static final SubQueryConfBean SUB_QUERY_CONF_BEAN = SpringContextUtils.getBean(SubQueryConfBean.class);

    public static final int MAX_SUB_QUERY_NUM = SUB_QUERY_CONF_BEAN.getMaxSubQueryNum();
}
