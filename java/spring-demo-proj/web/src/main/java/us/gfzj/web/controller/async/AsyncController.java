package us.gfzj.web.controller.async;

import com.gridsum.md.platform.core.service.MysqlService;
import us.gfzj.bean.TaskInfo;
import us.gfzj.bean.TaskSubQuery;
import us.gfzj.web.util.ControllerUtil;
import us.gfzj.web.util.QueryTaskBodyParser;
import us.gfzj.web.util.QueryTaskDecomposer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhoujing
 * Create on 19-1-30
 */
@RestController
@RequestMapping("/api/v1")
public class AsyncController {
    private static final Logger logger = LogManager.getLogger(AsyncController.class);

    @Resource
    private MysqlService mysqlService;

    @PostMapping(value = "/extraction/{channelType:qa|weibo|weixin}/{contentType:article}/Task", consumes = {"application/json"})
    public Map<String, Object> ingestQueryTask(@PathVariable("channelType") String channelType,
                                               @PathVariable("contentType") String contentType,
                                               @RequestBody String body) throws Exception {
        QueryTaskBodyParser parser = new QueryTaskBodyParser(body);
        TaskInfo taskInfo = parser.getTaskInfo(channelType, contentType);
        ControllerUtil.validateOutputEndpoint(taskInfo);

        String confPrefix = String.format("%s.%s", channelType, contentType);
        List<TaskSubQuery> subQueryList = QueryTaskDecomposer.decompose(parser, confPrefix);

        mysqlService.ingestQueryTask(taskInfo, subQueryList);

        Map<String, Object> response = new HashMap<>(2);
        response.put("description", "data extraction successful!");
        response.put("taskId", taskInfo.getId());

        return response;
    }


}
