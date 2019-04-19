package us.gfzj.web.controller.sync;

import com.google.common.collect.ImmutableMap;
import com.gridsum.md.platform.core.service.MysqlService;
import com.gridsum.md.platform.core.service.RedisService;
import us.gfzj.bean.SubQueryJob;
import us.gfzj.web.exceptions.TaskNotExistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author zhoujing
 * Create on 19-2-13
 */
@RestController
@RequestMapping("/api/v1")
public class InternalController {
    private static final Logger logger = LogManager.getLogger(InternalController.class);

    @Resource
    private MysqlService mysqlService;

    @Resource
    private RedisService redisService;

    @GetMapping(value = "/internal/Task")
    public List<Map<String, Object>> getJob(@RequestParam(value = "type") String type,
                                            @RequestParam(value = "order") String order) throws Exception {
        List<Map<String, Object>> response = new LinkedList<>();

        if ("extraction".equals(type) && "id_asc".equals(order)) {
            List<SubQueryJob> DAGJob = mysqlService.queryDAGJob();
            if (DAGJob == null || DAGJob.size() == 0) {
                throw new TaskNotExistException("No ready task can be executed.");
            }

            try {
                redisService.setTaskRunInfo(DAGJob);
            } catch (Exception e) {
                mysqlService.setTaskFailed(DAGJob.get(0).getTaskInfoId());
                throw e;
            }

            DAGJob.forEach(subQueryJob -> response.add(ImmutableMap.of(
                    "channelType", subQueryJob.getTaskInfoChannelType(),
                    "taskId", subQueryJob.getTaskInfoId(),
                    "subQueryId", subQueryJob.getSubQueryId())));
        }

        return response;
    }

}
