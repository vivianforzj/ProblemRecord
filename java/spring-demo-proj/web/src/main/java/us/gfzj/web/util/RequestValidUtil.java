package us.gfzj.web.util;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author Zhiqiang Lin
 * @Description
 * @create 2019/2/25.
 */
public class RequestValidUtil {
    public static boolean validArticleQueryBean(ArticleQueryBean articleQueryBean) {
        if (articleQueryBean.getReference().equals("single")) {
            return true;
        } else if (articleQueryBean.getReference().equals("family")) {
            if (articleQueryBean.getIds().length > 1 || articleQueryBean.getIds()[0].contains("|")) {
                return false;
            }
            if (ObjectUtils.allNotNull(articleQueryBean.getRangeBy(), articleQueryBean.getRangeFrom(), articleQueryBean.getRangeTo(), articleQueryBean.getOrderBy(), articleQueryBean.getOrder())) {
                if (articleQueryBean.getRangeFrom() > articleQueryBean.getRangeTo()) {
                    return false;
                }
                return true;
            } else if (articleQueryBean.getRangeBy() == null && articleQueryBean.getRangeFrom() == null && articleQueryBean.getRangeTo() == null && articleQueryBean.getOrderBy() == null && articleQueryBean.getOrder() == null) {
                return true;
            }
        } else {
            return false;
        }
        return true;
    }
}
