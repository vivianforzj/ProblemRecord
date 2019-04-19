## 工程列表
- spring-boot-apollo: apollo 结合 spring boot 的使用方式；另外也加入了 log4j 的相关使用。可理解为通过 apollo 动态配置日志级别的工程

## Spring boot 使用过程中问题记录集合

### log4j2 不打印第三方依赖的日志

使用 log4j2时，发现会打印大量的第三方依赖的日志，大部分信息是没有意义的，所以希望能够不要打印。

假设不想打印 org.apache.hadoop 的日志信息，可以在 `application.properties` 中加如下内容

```
logging.level.org.apache.hadoop=ERROR
```
此时，只有在发生 error 时，才会打印 hadoop 信息。

### Application 类放在工程里 java 目录下报错

没有把启动类放到 package 中，启动报错如下:

```
Caused by: java.lang.ClassNotFoundException: org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
```

但是，实际上并没有要使用 db, 网上搜了一下，说把启动类放到 package 下就不会出现这个问题了。确实如此，已验证。但是，为什么呢？
