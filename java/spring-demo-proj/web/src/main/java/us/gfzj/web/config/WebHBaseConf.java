package us.gfzj.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhiqiang Lin
 * @Description
 * @create 2019/2/22.
 */
@Configuration
public class WebHBaseConf {
    @Value("${hbase.get.batch.thread:1}")
    private Integer maxThreadCount;
    @Value("${hbase.get.batch.size:1}")
    private Integer minBatchSize;

    @Value("${qa.zookeeper.quorum.hosts}")
    private String qaZKHosts;
    @Value("${qa.hbase.clustername}")
    private String qaClustername;

    @Value("${qa.article.hbase.table.name}")
    private String qaTableName;
    @Value("${qa.article.hbase.column.name.mapping}")
    private String qaHBaseColumnNameMappingStr;
    @Value("${qa.article.schema.type.map}")
    private String qaSchemaTypeMap;

    public Integer getMaxThreadCount() {
        return maxThreadCount;
    }

    public Integer getMinBatchSize() {
        return minBatchSize;
    }

    public String getQaZKHosts() {
        return qaZKHosts;
    }

    public String getQaTableName() {
        return qaTableName;
    }

    public String getQaClustername() {
        return qaClustername;
    }

    public String getQaHBaseColumnNameMappingStr() {
        return qaHBaseColumnNameMappingStr;
    }

    public String getQaSchemaTypeMap() {
        return qaSchemaTypeMap;
    }
}
