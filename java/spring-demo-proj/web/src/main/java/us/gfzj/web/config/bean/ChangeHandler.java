package us.gfzj.web.config.bean;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import us.gfzj.conf.ChangeListener;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.springframework.stereotype.Service;

/**
 * @author Zhiqiang Lin
 * @Description
 * @create 2019/1/15.
 */
@Service
public class ChangeHandler implements ChangeListener {
    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        System.out.println(changeEvent.getNamespace() + " changed");
        if (changeEvent.isChanged("log.level")) {
            String strLevel = changeEvent.getChange("log.level").getNewValue();
            Level level = Level.toLevel(strLevel);
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            org.apache.logging.log4j.core.config.Configuration conf = context.getConfiguration();

            // change root logger
            conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(level);
            context.updateLoggers(conf);
        } else {
//            System.exit(-1);
        }
    }
}
