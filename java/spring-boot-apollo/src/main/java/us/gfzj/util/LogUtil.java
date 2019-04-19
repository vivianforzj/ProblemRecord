package us.gfzj.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import us.gfzj.conf.LogConf;


public class LogUtil {

    public static void intLogger(LogConf logConf) {
        String strLevel = logConf.getLogLevel();
        Level level = Level.toLevel(strLevel);
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration conf = context.getConfiguration();

        // change root logger
        conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(level);
        context.updateLoggers(conf);
    }
}
