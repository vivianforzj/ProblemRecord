package us.gfzj.web.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import us.gfzj.bean.TaskInfo;
import us.gfzj.enums.TaskInfoEnums;
import us.gfzj.web.config.WebElasticsearchConf;
import us.gfzj.web.exceptions.InvalidJsonFormatException;
import us.gfzj.web.exceptions.InvalidTimeRangeException;
import us.gfzj.web.exceptions.InvalidUserParametersException;

import java.util.Date;

/**
 * @author zhoujing
 * Create on 19-1-30
 */
public class QueryTaskBodyParser {
    private final String ingestTimeStr = "ingestTime";
    private final String publishTimeStr = "publishTime";
    private final String seedPackageIdsStr = "seedPackageIds";
    private final String subscriptionIdsStr = "subscriptionIds";
    private JSONObject filter;
    private JSONObject range;
    private JSONObject ingestTime;
    private JSONObject publishTime;
    private JSONArray seedPackageIds;
    private JSONArray subscriptionIds;
    private JSONObject expansion;
    private JSONObject retPara;

    public QueryTaskBodyParser(String body) throws InvalidUserParametersException {
        try {
            JSONObject jsonObject = JSONObject.parseObject(body);
            filter = jsonObject.getJSONObject("filter");
            range = jsonObject.getJSONObject("range");
            expansion = jsonObject.getJSONObject("expansion");
            retPara = jsonObject.getJSONObject("retPara");
        } catch (JSONException e) {
            throw new InvalidJsonFormatException("Request body is not valid json", e);
        }

        if (filter == null || range == null || expansion == null || retPara == null) {
            throw new InvalidUserParametersException("Miss some required fields in [filter, range, expansion, retPara]");
        } else if (!range.containsKey(ingestTimeStr) && !range.containsKey(publishTimeStr)) {
            throw new InvalidTimeRangeException("No ingestTimeStr or publishTime");
        }

        ingestTime = range.getJSONObject(ingestTimeStr);
        publishTime = range.getJSONObject(publishTimeStr);

        try {
            seedPackageIds = range.getJSONArray(seedPackageIdsStr);
            subscriptionIds = range.getJSONArray(subscriptionIdsStr);
        } catch (Exception e) {
            throw new InvalidUserParametersException("[seedPackageIds] or [subscriptionIds] may not in array format", e);
        }
        if(range.containsKey(seedPackageIdsStr) && seedPackageIds == null) {
            throw new InvalidUserParametersException("[seedPackageIds] has null value");
        }
        if(range.containsKey(subscriptionIdsStr) && subscriptionIds == null) {
            throw new InvalidUserParametersException("[subscriptionIds] may has null value");
        }

        validateTimeRange();
    }

    private void validateTimeRange() throws InvalidTimeRangeException {
        if (ingestTime != null) {
            if (!ingestTime.containsKey("gte") || !ingestTime.containsKey("lte")) {
                throw new InvalidTimeRangeException("IngestTime misses gte or lte");
            }
        }
        if (ingestTime != null) {
            JSONObject publishTime = getPublishTime();
            if (!publishTime.containsKey("gte")) {
                throw new InvalidTimeRangeException("PublishTime misses gte");
            }
        }
    }

    public String getOutputEndpoint() {
        return retPara.getString("recEndpoint");
    }

    public JSONObject getIngestTime() {
        return ingestTime;
    }

    public JSONObject getPublishTime() {
        return publishTime;
    }

    public TaskInfo getTaskInfo(String channelType, String contentType) {
        TaskInfo taskInfo = new TaskInfo();

        taskInfo.setType(TaskInfoEnums.Type.ASYNC.ordinal());
        taskInfo.setPriority(4);

        Date now = new Date();
        taskInfo.setCreateTime(now);
        taskInfo.setUpdateTime(now);

        taskInfo.setOutputEndpoint(this.getOutputEndpoint());
        taskInfo.setOutputContentType(TaskInfoEnums.OutputContentType.JSON.ordinal());
        taskInfo.setOutputType(TaskInfoEnums.OutputType.EVENTHUB.ordinal());
        taskInfo.setOutputEncoding(TaskInfoEnums.OutputEncoding.RAW.ordinal());

        taskInfo.setStatus(TaskInfoEnums.Status.CREATE.ordinal());
        taskInfo.setDagType(TaskInfoEnums.DAGType.ES_HBASE_SEND.ordinal());

        taskInfo.setChannelType(TaskInfoEnums.ChannelType.valueOf(channelType.toUpperCase()).ordinal());
        taskInfo.setContentType(TaskInfoEnums.ContentType.valueOf(contentType.toUpperCase()).ordinal());

        return taskInfo;
    }

    public String getContent() {
        JSONArray generatedMust = new JSONArray();
        JSONObject ingestTime = getIngestTime();
        JSONObject publishTime = getPublishTime();
        if (ingestTime != null) {
            generatedMust.add(ImmutableMap.of("range",
                    ImmutableMap.of(String.format("_%s", ingestTimeStr), ingestTime)));
        }
        if (publishTime != null) {
            generatedMust.add(ImmutableMap.of("range", ImmutableMap.of(publishTimeStr, publishTime)));
        }

        JSONArray generatedShould = new JSONArray();
        if (range.containsKey(seedPackageIdsStr) && seedPackageIds.size() == 1) {
            generatedShould.add(ImmutableMap.of("terms",
                    ImmutableMap.of(subscriptionIdsStr,
                            ImmutableMap.of("index", WebElasticsearchConf.getESMappingSubIdsAndSeedpkgIdsIndex(),
                                    "type", "assist",
                                    "id", seedPackageIds.get(0).toString(),
                                    "path", subscriptionIdsStr
                            ))));
        }
        if (range.containsKey(subscriptionIdsStr)) {
            generatedShould.add(ImmutableMap.of("terms", ImmutableMap.of(subscriptionIdsStr, subscriptionIds)));
        }

        JSONObject generateFilter = new JSONObject();
        if (generatedShould.size() > 0) {
            generateFilter.put("bool", ImmutableMap.of("must", generatedMust, "should", generatedShould, "minimum_should_match", 1));
        } else {
            generateFilter.put("bool", ImmutableMap.of("must", generatedMust));
        }

        JSONObject content = new JSONObject();
        JSONArray must = new JSONArray();
        must.add(filter);
        must.add(generateFilter);
        content.put("query", ImmutableMap.of("bool", ImmutableMap.of("must", must)));

        return content.toJSONString();
    }
}
