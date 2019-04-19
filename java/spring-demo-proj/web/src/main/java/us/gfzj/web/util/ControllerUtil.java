package us.gfzj.web.util;

import us.gfzj.bean.TaskInfo;
import us.gfzj.enums.TaskInfoEnums;
import us.gfzj.web.exceptions.OutputEndpointNotAvaiableException;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * @author zhoujing
 * Create on 19-2-3
 */
public class ControllerUtil {

    public static void validateOutputEndpoint(TaskInfo taskInfo) throws OutputEndpointNotAvaiableException {
        TaskInfoEnums.OutputType outputType = TaskInfoEnums.OutputType.values()[taskInfo.getOutputType()];
        if (outputType == TaskInfoEnums.OutputType.EVENTHUB) {
            try {
                EventHubClient eventHubClient = EventHubClient.createSync(taskInfo.getOutputEndpoint(),
                        Executors.newSingleThreadExecutor());
                eventHubClient.closeSync();
            } catch (EventHubException | IOException | NullPointerException e) {
                String msg = String.format("Invalid endpoint: %s", taskInfo.getOutputEndpoint());
                throw new OutputEndpointNotAvaiableException(msg);
            }
        }
    }
}
