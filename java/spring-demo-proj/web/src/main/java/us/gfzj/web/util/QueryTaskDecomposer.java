package us.gfzj.web.util;

import com.alibaba.fastjson.JSONObject;
import us.gfzj.bean.TaskSubQuery;
import us.gfzj.constant.SystemConstants;
import us.gfzj.enums.TaskSubQueryEnums;
import us.gfzj.util.DateUtil;
import us.gfzj.web.config.SubQueryConf;
import us.gfzj.web.config.WebElasticsearchConf;
import us.gfzj.web.constants.ConfKeyTemplate;
import us.gfzj.web.exceptions.InvalidTimeRangeException;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author zhoujing
 * Create on 19-1-30
 */
public class QueryTaskDecomposer {
    public static List<TaskSubQuery> decompose(QueryTaskBodyParser parser, String confPrefix)
            throws InvalidTimeRangeException {
        List<String> indices = getIndices(parser, confPrefix);

        List<TaskSubQuery> subQueries = splitToSubQueries(indices);
        String content = parser.getContent();
        for (TaskSubQuery subQuery : subQueries) {
            subQuery.setMethod(TaskSubQueryEnums.Method.POST.ordinal());
            subQuery.setContent(content);

            Date now = new Date();
            subQuery.setCreateTime(now);
            subQuery.setUpdateTime(now);
        }

        return subQueries;
    }

    private static List<String> getIndices(QueryTaskBodyParser parser, String confPrefix) throws InvalidTimeRangeException {
        JSONObject ingestTime = parser.getIngestTime();
        JSONObject publishTime = parser.getPublishTime();
        List<String> indices = new LinkedList<>(), ingestTimeIndices, publishTimeIndices;
        if (ingestTime == null && publishTime == null) {
            throw new InvalidTimeRangeException("No ingestTime or publishTime");
        }
        if (ingestTime != null) {
            ingestTimeIndices = getIngestTimeIndices(confPrefix, ingestTime);
            indices.addAll(ingestTimeIndices);
        }
        if (publishTime != null) {
            publishTimeIndices = getPublishTimeIndices(confPrefix, publishTime);
            if (ingestTime != null) {
                indices.retainAll(publishTimeIndices);
            } else {
                indices.addAll(publishTimeIndices);
            }
        }

        if (indices.size() == 0) {
            throw new InvalidTimeRangeException("Time range is 0");
        }
        return indices;
    }

    private static List<String> getPublishTimeIndices(String confPrefix, JSONObject publishTime)
            throws InvalidTimeRangeException {
        List<String> indices = new ArrayList<>();

        Instant start, end;
        try {
            start = DateUtil.parseToInstant(publishTime.getString("gte"));
            end = new Date().toInstant();
        } catch (NullPointerException e) {
            throw new InvalidTimeRangeException("PublishTime misses gte", e);
        } catch (DateTimeParseException e) {
            throw new InvalidTimeRangeException("Invalid publishTime date format", e);
        }
        if (end.isBefore(start) || end.equals(start)) {
            throw new InvalidTimeRangeException("Invalid publishTime range");
        }

        String indexNameFormat = WebElasticsearchConf.getElasticsearchConf(String.format(ConfKeyTemplate.INDEX_NAME_FORMAT,
                confPrefix));
        Instant eachDay = start;
        while (eachDay.isBefore(end) || DateUtil.isSameDay(eachDay, end)) {
            String indexName = calculateIndexName(indexNameFormat, Date.from(eachDay));
            indices.add(indexName);

            eachDay = eachDay.plus(1, ChronoUnit.DAYS);
        }

        return indices;
    }

    private static List<String> getIngestTimeIndices(String confPrefix, JSONObject ingestTime)
            throws InvalidTimeRangeException {
        List<String> indices = new ArrayList<>();

        Instant start, end;
        try {
            start = DateUtil.parseToInstant(ingestTime.getString("gte"));
            end = DateUtil.parseToInstant(ingestTime.getString("lte"));
        } catch (NullPointerException e) {
            throw new InvalidTimeRangeException("IngestTime misses gte or lte", e);
        } catch (DateTimeParseException e) {
            throw new InvalidTimeRangeException("Invalid ingestTime date format", e);
        }

        Instant now = new Date().toInstant();
        if (start.isAfter(now) || start.equals(now)) {
            throw new InvalidTimeRangeException("IngestTime gte is after now");
        } else if (end.isAfter(now) || end.equals(now)) {
            end = now;
        }
        if (end.isBefore(start) || end.equals(start)) {
            throw new InvalidTimeRangeException("Invalid ingestTime range");
        }

        String indexNameFormat = WebElasticsearchConf.getElasticsearchConf(String.format(ConfKeyTemplate.INDEX_NAME_FORMAT,
                confPrefix));
        Instant eachDay = start;
        while (eachDay.isBefore(end) || DateUtil.isSameDay(eachDay, end)) {
            String indexName = calculateIndexName(indexNameFormat, Date.from(eachDay));
            indices.add(indexName);

            eachDay = eachDay.plus(1, ChronoUnit.DAYS);
        }
        return indices;
    }

    private static List<TaskSubQuery> splitToSubQueries(List<String> indices) {
        List<TaskSubQuery> subQueries = new LinkedList<>();
        List<Integer> subQueryIndexNumList = calculateIndexNumList(indices.size());

        int first = 0, last = 0;
        for (int indexNum : subQueryIndexNumList) {
            last += indexNum;

            TaskSubQuery subQuery = new TaskSubQuery();
            String endpoint = String.join(",", indices.subList(first, last)) + "/_search";
            subQuery.setEndpoint(endpoint);

            subQueries.add(subQuery);

            first = last;
        }

        return subQueries;
    }

    private static List<Integer> calculateIndexNumList(int indexNum) {
        int maxSubQueryNum = SubQueryConf.MAX_SUB_QUERY_NUM;
        List<Integer> list = new LinkedList<>();
        if (indexNum <= maxSubQueryNum) {
            while (indexNum-- > 0) {
                list.add(1);
            }
        } else {
            int base = indexNum / maxSubQueryNum;
            int remainder = indexNum % maxSubQueryNum;
            int count = maxSubQueryNum;
            while (count-- > 0) {
                int value = base;
                if (remainder-- > 0) {
                    value += 1;
                }
                list.add(value);
            }
        }

        return list;
    }

    private static String calculateIndexName(String format, Date date) {
        if (format == null) {
            throw new IllegalArgumentException("null format");
        }
        if (date == null) {
            throw new IllegalArgumentException("null date");
        }

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(SystemConstants.CHINA));
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return String.format(format, year, month, day);
    }
}
