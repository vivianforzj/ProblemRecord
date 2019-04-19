package us.gfzj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import us.gfzj.conf.LogConf;
import us.gfzj.util.LogUtil;

import javax.annotation.Resource;

@SpringBootApplication
public class Application implements CommandLineRunner {
    @Resource
    private LogConf logConf;
    private Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LogUtil.intLogger(logConf);
        logger.debug("Initialize log level as {}", logConf.getLogLevel());

        // do other
    }
}
