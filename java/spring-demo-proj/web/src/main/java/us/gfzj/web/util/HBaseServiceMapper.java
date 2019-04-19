package us.gfzj.web.util;

import com.gridsum.md.platform.core.service.HBaseService;
import com.gridsum.md.platform.core.service.impl.HBaseServiceImpl;
import us.gfzj.conf.HBaseConf;
import us.gfzj.dao.impl.HBasePlatformDocumentDaoImpl;
import us.gfzj.enums.ChannelTypeEnum;
import us.gfzj.web.config.WebHBaseConf;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhiqiang Lin
 * @Description
 * @create 2019/2/22.
 */
@Component("hbaseServiceMapper")
public class HBaseServiceMapper {
    @Resource(name = "webHBaseConf")
    private WebHBaseConf webHBaseConf;
    private Map<ChannelTypeEnum, HBaseService> hbaseServiceMap = new HashMap<>();

    public HBaseService getHBaseService(ChannelTypeEnum channelTypeEnum) {
        HBaseService hbaseService = hbaseServiceMap.get(channelTypeEnum);
        if (hbaseService == null) {
            HBaseConf hbaseConf = new HBaseConf(webHBaseConf.getQaZKHosts(), webHBaseConf.getQaTableName(), webHBaseConf.getQaClustername(),
                    webHBaseConf.getMaxThreadCount(), webHBaseConf.getMinBatchSize(), webHBaseConf.getQaHBaseColumnNameMappingStr(),
                    webHBaseConf.getQaSchemaTypeMap());
            HBasePlatformDocumentDaoImpl hbasePlatformDocumentDaoImpl = new HBasePlatformDocumentDaoImpl(hbaseConf);
            hbasePlatformDocumentDaoImpl.init();
            hbaseService = new HBaseServiceImpl(hbasePlatformDocumentDaoImpl);
            hbaseServiceMap.put(channelTypeEnum, hbaseService);
        }
        return hbaseService;
    }
}
