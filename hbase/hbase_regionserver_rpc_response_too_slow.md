### 现象
对 HBase 进行读写的 java client 端的 读操作 和 写操作 耗时达几十秒。同时有 8 个线程，4 个线程会进行读和写，另外 4 个线程只有读操作，每个线程一次的数据量是 3MB 左右。

### 寻根究底

#### 查看 Regionserver 日志

发现`ipc.RpcServer: (responseTooSlow): {"call":"Multi(org.apache.hadoop.hbase.protobuf.generated.ClientProtos$MultiRequest)","starttimems":1535735262533,"responsesize":1138949,"method":"Multi","processingtimems":14675,"client":"10.17.0.7:52756","queuetimems":0,"class":"HRegionServer"}
`日志信息。

显然 regionserver 在处理 rpc 请求的响应时间太慢造成了上述现象。但是 regionserver 端中的哪一步操作导致了 rpc 请求响应慢呢？

#### 使用 java profile 工具探查“慢”操作

选择使用 arthas 这个工具，按照官方文档（https://alibaba.github.io/arthas/index.html）安装到 regionserver 上，并启动监控 regionserver 进程。

##### 监控第一步

执行`trace org.apache.hadoop.hbase.regionserver.RSRpcServices multi`，发现方法`org.apache.hadoop.hbase.regionserver.RSRpcServices:doNonAtomicRegionMutation`耗时较多。

##### 监控第二步 

执行`trace org.apache.hadoop.hbase.regionserver.RSRpcServices doNonAtomicRegionMutation`

未完待续
