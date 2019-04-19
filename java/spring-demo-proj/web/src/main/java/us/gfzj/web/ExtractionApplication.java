package us.gfzj.web;

import com.gridsum.md.platform.core.service.ESService;
import com.gridsum.md.platform.core.service.HBaseService;
import us.gfzj.conf.ElasticsearchConf;
import us.gfzj.conf.LogConf;
import us.gfzj.dao.ESPlatformDocumentDao;
import us.gfzj.dao.HBasePlatformDocumentDao;
import us.gfzj.util.LogUtil;
import io.leopard.javahost.JavaHost;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author zhoujing
 * Create on 19-1-30
 */
@SpringBootApplication
@EntityScan(basePackages = {"com.gridsum.md.platform.model.bean"})
@ComponentScan(basePackages = {"com.gridsum.md.platform.core.service", "com.gridsum.md.platform.model", "com.gridsum.md.platform.web"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = HBasePlatformDocumentDao.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ESPlatformDocumentDao.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = HBaseService.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ESService.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ElasticsearchConf.class)
        })
@EnableJpaRepositories(basePackages = {"com.gridsum.md.platform.model.dao"})
public class ExtractionApplication extends SpringBootServletInitializer {
    //public class ExtractionApplication {
    static {
        JavaHost.updateVirtualDns("apollo.internal.gridsumdissector.com", "118.26.161.170");
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ExtractionApplication.class, args);

        LogConf logConfBean = context.getBean(LogConf.class);
        LogUtil.intLogger(logConfBean);
    }

}