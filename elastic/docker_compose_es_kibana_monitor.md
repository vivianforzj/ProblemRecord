背景：在一个宿主机上，通过使用 docker 启动多套 kibana-elasticsearch 监控不同的远程 ES 集群。每一套 kibana-elasticsearch 是通过 docker compose 启动的，负责监控指定的 ES 集群；所以多套 kibana-elasticsearch 之间要使用不同的宿主机端口，以便远程被监控的 ES 集群发送监控数据到对应的一套 kibana-elasticsearch。

### 一. 宿主机重要配置

1. **宿主机要设置 vm.max_map_count**

查询：`grep vm.max_map_count /etc/sysctl.conf`，如果没有该值
- 永久配置：
```
sudo vim /etc/sysctl.conf : 添加  vm.max_map_count=262144
sudo sysctl -p : 使上述配置生效
```
- 临时性设置：`sudo sysctl -w vm.max_map_count=262144`


### 二. 安装 docker-ce

https://docs.docker-cn.com/engine/installation/linux/docker-ce/centos/#%E4%BD%BF%E7%94%A8%E9%95%9C%E5%83%8F%E4%BB%93%E5%BA%93%E8%BF%9B%E8%A1%8C%E5%AE%89%E8%A3%85

```
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum makecache fast
sudo yum install docker-ce-18.06.1.ce-3.el7
sudo systemctl start docker    // 启动 docker daemon
```

### 三. 安装 docker compose
https://github.com/docker/compose/releases

```
sudo curl -L https://github.com/docker/compose/releases/download/1.23.2/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo /usr/local/bin/docker-compose --version
```

### 四. Docker-compose 相关配置

使用 docker-compose 同时启动 kibana 和 elasticsearch，需要使用 3 个配置文件，即 docker-compose.yml、elasticsearch.yml、kibana.yml，这三个文件在同一个目录下，在此目录路径下启动 docker-compose。

#### 1. docker-compose.yml

内容如下（假设为 test-es 集群配置监控）：
```
version: '2.2'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:6.4.3
    container_name: es-for-test-es           
    environment:
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    volumes:
      - /data/elasticsearch/esdata-test-es:/usr/share/elasticsearch/data      
      - ./elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml
    ports:
      - 9202:9200  // 宿主机 9202 端口号映射到 docker container 端口 9200，其他节点可使用 9202 访问此 es 实例
      - 9302:9300  // 宿主机 9302 端口号映射到 docker container 端口 9300
    networks:
      - esnet-for-test-es
  kibana:
    image: docker.elastic.co/kibana/kibana:6.4.3
    container_name: kibana-for-test-es  
    ports:
      - 5602:5601  // 宿主机 5602 端口号映射到 docker container 端口 5601，其他节点可使用 5602 访问此 kibana 实例
    volumes:
      - ./kibana.yml:/usr/share/kibana/config/kibana.yml
    networks:
      - esnet-for-test-es
volumes:
  esdata-for-test-es:
    driver: local

networks:
  esnet-for-test-es:
    driver: bridge
```

#### 2. elasticsearch.yml

内容如下：
```
cluster.name: monitoring-es     
node.name: node1
network.host: 0.0.0.0
discovery.zen.minimum_master_nodes: 1
discovery.type: single-node

bootstrap.memory_lock: true

xpack.monitoring.enabled: false
xpack.security.enabled: false
```

#### 3. kibana.yml

内容如下：
```
server.name: kibana
server.host: 0.0.0.0 

elasticsearch.url: http://x.x.x.x:9202  （x.x.x.x 为宿主机的 IP，尽量使用内网 IP；注意此处端口号就是之前配置的映射到9200的宿主机端口号9202)
```

### 五. 启动监控二人组

执行 `sudo /usr/local/bin/docker-compose up`。如果启动过程中遇到问题，检查一下目录 /data/elasticsearch/esdata-test-es 的权限，看看 docker es 是否对其有写权限。

启动成功后，在浏览器中输入 x.x.x.x:5602 即可访问 kibana

### 六. Monitored ES 集群配置

1. 被监控的 ES 集群配置

ES 集群每个节点的配置文件修改`sudo vim /etc/elasticsearch/elasticsearch.yml`，添加如下配置
```
xpack.monitoring.collection.enabled: true
xpack.security.enabled: false
xpack.monitoring.exporters:
  test-remote:
    type: http
    host: ["http://x.x.x.x:9202","http://y.y.y.y:9202"]    // host 一个或多个； 这里 ip 要看具体情况来决定是用内网 IP 还是外网 IP，只要保证集群中每个节点都能访问这个 IP 就行。
```

ES 集群的版本只要高于6.3，默认安装 x-pack，且开启 security，license 也是basic 版本。所以我们无需更改 license，也无需安装 x-pack，但是需要禁用 recurity（xpack.security.enabled: false），并开启监控数据的收集（xpack.monitoring.collection.enabled: true)；另外，需要将收集到的监控数据发送到另一个 es 集群（test-remote）。

2. 重启 ES 节点 `sudo -i service elasticsearch restart`，此时观察 ES 集群的状态（
`GET _cluster/health`），等变为 green 再配置下一个节点。

