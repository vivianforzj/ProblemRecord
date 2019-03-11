Spring boot 使用过程中问题记录集合

### log4j2 不打印第三方依赖的日志

使用 log4j2时，发现会打印大量的第三方依赖的日志，大部分信息是没有意义的，所以希望能够不要打印。

假设不想打印 org.apache.hadoop 的日志信息，可以在 `application.properties` 中加如下内容

```
logging.level.org.apache.hadoop=ERROR
```
此时，只有在发生 error 时，才会打印 hadoop 信息。

