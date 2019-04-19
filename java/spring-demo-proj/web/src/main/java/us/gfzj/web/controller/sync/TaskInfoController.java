package us.gfzj.web.controller.sync;

import com.gridsum.md.platform.core.service.MysqlService;
import com.gridsum.md.platform.core.service.RedisService;
import us.gfzj.web.exceptions.TaskNotExistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoujing
 * Create on 19-2-22
 */
@RestController
@RequestMapping("/api/v1")
public class TaskInfoController {
    private static final Logger logger = LogManager.getLogger(TaskInfoController.class);

    @Resource
    private MysqlService mysqlService;

    @Resource
    private RedisService redisService;

    @GetMapping(value = "/extraction/TaskInfo")
    public Map<String, Object> queryTaskInfo(@RequestParam(value = "id") int id,
                                             @RequestParam(value = "type") String type) throws TaskNotExistException {
        Map<String, Object> response = new HashMap<>();

        if ("async".equals(type)) {
            String taskStatus = mysqlService.getTaskStatus(id);
            if (taskStatus == null) {
                throw new TaskNotExistException(String.format("Task [%s] not exists", id));
            }

            int hitNumber = redisService.getHitNumber(id);
            int outputNumber = redisService.getOutputNumber(id);

            if (hitNumber != -1) {
                response.put("hitNumber", hitNumber);
            }
            if (outputNumber != -1) {
                response.put("outputNumber", outputNumber);
            }
            response.put("taskStatus", taskStatus);
        }

        return response;
    }
}
