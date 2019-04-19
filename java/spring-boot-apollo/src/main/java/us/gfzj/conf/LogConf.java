package us.gfzj.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration("logConf")
public class LogConf {

    @Value("${log.level:info}")
    private String logLevel;

    public String getLogLevel() {
        return this.logLevel;
    }

}