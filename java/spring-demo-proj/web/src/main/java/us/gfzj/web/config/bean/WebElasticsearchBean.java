package us.gfzj.web.config.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhoujing
 * Create on 19-2-21
 */
@Component
public class WebElasticsearchBean {

    @Value("${es.mapping.subids.and.seedpkgids.index}")
    private String esMappingSubIdsAndSeedpkgIdsIndex;

    public String getEsMappingSubIdsAndSeedpkgIdsIndex() {
        return this.esMappingSubIdsAndSeedpkgIdsIndex;
    }
}
