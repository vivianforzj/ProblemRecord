package us.gfzj.conf;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;

public interface ChangeListener extends ConfigChangeListener {
    @Override
    void onChange(ConfigChangeEvent changeEvent);
}
