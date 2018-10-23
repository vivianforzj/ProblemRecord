 安装 kibana 和 x-pack

kibana 要安装 x-pack 才能展示 es 监控数据，elasticsearch 也要安装并配置 x-pack 收集监控数据。我们使用 basic license，有效期是一年，且只能使用监控功能(无其他功能，如安全等)，但是如果只是为了监控集群也足够了。

### 安装配置 kibana

#### 安装
可安装到 es 的某个节点上，也可以安装在一个远程节点上。按照官方文档 https://www.elastic.co/guide/en/kibana/current/rpm.html 进行即可（启动时使用 init 方式）。

#### 配置 kibana
修改 /etc/kibana/kibana.yml，添加如下设置：
- server.host: "0.0.0.0"
- elasticsearch.url: "http://x.x.x.x:9200"

### 安装配置 x-pack

#### 安装 x-pack
- Kibana 安装 x-pack 插件：/usr/share/kibana/bin/kibana-plugin install x-pack
- Elasticsearch 安装 x-pack 插件：/usr/share/elasticsearch/bin/elasticsearch-plugin install x-pack  （es 集群中每个节点都要安装）

#### 配置
- Kibana 的 x-pack 配置，在 /etc/kibana/kibana.yml 添加如下配置：

        xpack.security.enabled: false`
此处因为要使用 basic x-pack，所以要将 x-pack 的安全功能关闭掉，basic 版没有

- Elasticsearch 的 x-pack 配置，在 /etc/elasticsearch/elasticsearch.yml 添加如下配置：

        action.auto_create_index: *  （本来是要设置为 .security,.monitoring*,.watches,.triggered_watches,.watcher-history*,.ml*，后来发现集群还要自动创建其他索引，所以直接设置成 * 了）
        xpack.monitoring.enabled: true
        xpack.security.enabled: false

重启 Kibana，ES。

### 关于 license
使用 basic 版本的 x-pack，是要看 Elastic Stack 版本来决定是否需要注册 license的，高于 6.3 不必注册，低于 6.3，则需要到 https://register.elastic.co/ 进行注册。注册会有 license 文件发送到注册邮箱中，将该文件下载到本地，然后按照 https://www.elastic.co/guide/en/x-pack/6.1/installing-license.html 中的更新 license 方式更新集群 license 为 basic。通过如下命令查看集群的 license 信息

```
curl -XGET "http://xxxx:9200/_xpack/license"
{
  "license" : {
    "status" : "active",
    "uid" : "b9defa2c-dfd5-4c98-b8a2-e3a40ce7ff9b",
    "type" : "basic",
    "issue_date" : "2018-09-07T00:00:00.000Z",
    "issue_date_in_millis" : 1536278400000,
    "expiry_date" : "2019-09-07T23:59:59.999Z",
    "expiry_date_in_millis" : 1567900799999,
    "max_nodes" : 100,
    "issued_to" : "myName",
    "issuer" : "Web Form",
    "start_date_in_millis" : 1536278400000
  }
}
```
