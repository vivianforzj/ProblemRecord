package us.gfzj.web.controller.sync;

import com.alibaba.fastjson.JSONObject;
import com.gridsum.md.platform.core.service.HBaseService;
import com.gridsum.md.platform.core.util.PlatformDocumentUtil;
import us.gfzj.enums.ChannelTypeEnum;
import us.gfzj.util.ColumnNameMapping;
import us.gfzj.web.config.WebHBaseConf;
import us.gfzj.web.controller.async.AsyncController;
import us.gfzj.web.exceptions.InvalidUserParametersException;
import us.gfzj.web.exceptions.DataNotExistException;
import us.gfzj.web.util.HBaseServiceMapper;
import us.gfzj.web.util.JSONObjectUtils;
import us.gfzj.web.util.RequestValidUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;


/**
 * @author Zhiqiang Lin
 * @Description
 * @create 2019/2/22.
 */
@RestController
@RequestMapping("/api/v1")
public class SyncController {
    private Logger logger = LogManager.getLogger(AsyncController.class);
    @Resource(name = "hbaseServiceMapper")
    private HBaseServiceMapper hbaseServiceMapper;
    @Resource(name = "webHBaseConf")
    private WebHBaseConf webHBaseConf;

    @GetMapping(value = "/query/{channelType:qa|weibo|weixin}/Articles")
    @Transactional(rollbackFor = {Exception.class})
    public JSONObject getArticles(@PathVariable("channelType") String channelType,
                                  @RequestParam(name = "ids") String[] ids,
                                  @RequestParam(name = "reference") String reference,
                                  @RequestParam(name = "columns", required = false) String[] columns,
                                  @RequestParam(name = "rangeby", required = false) String rangeBy,
                                  @RequestParam(name = "rangefrom", required = false) Integer rangeFrom,
                                  @RequestParam(name = "rangeto", required = false) Integer rangeTo,
                                  @RequestParam(name = "orderby", required = false) String orderBy,
                                  @RequestParam(name = "order", required = false) String order, HttpServletResponse httpServletResponse) throws Exception {
        ArticleQueryBean articleQueryBean = new ArticleQueryBean(ids, reference, columns, rangeBy, rangeFrom, rangeTo, orderBy, order);
        logger.info(articleQueryBean);
        if (!RequestValidUtil.validArticleQueryBean(articleQueryBean)) {
            throw new InvalidUserParametersException("Request parameter not valid");
        }
        JSONObject response = new JSONObject();
        HBaseService hbaseService = hbaseServiceMapper.getHBaseService(ChannelTypeEnum.QA_ARTICLE);
        List<String> columnList = new ArrayList<>();
        if (articleQueryBean.getColumns() != null && articleQueryBean.getColumns().length != 0) {
            for (String columnName : columns) {
                String hbaseColumnName = ColumnNameMapping.getAbbreviatedName(columnName);
                if (hbaseColumnName == null) {
                    if (columnName.startsWith("special_")) {
                        hbaseColumnName = columnName.replace("special_", "s.");
                    }
                }
                if (!StringUtils.isBlank(hbaseColumnName)) {
                    columnList.add(hbaseColumnName);
                }
            }
        }
        if (reference.equals("single")) {
            try {
                if (ids.length == 1) {
                    Result result = hbaseService.getByRowKey(webHBaseConf.getQaTableName(), ids[0], columnList);
                    JSONObject jsonObject = null;
                    if (!result.isEmpty()) {
                        jsonObject = PlatformDocumentUtil.generatPlatformDocumentObject(result, false);
                    }
                    if (jsonObject == null || jsonObject.size() == 0) {
                        throw new DataNotExistException("Not found");
                    }
                    response.putAll(jsonObject);
                } else {
                    Result[] results = hbaseService.getByRowKeys(webHBaseConf.getQaTableName(), Arrays.asList(ids), columnList);
                    List<JSONObject> jsonObjectList = new ArrayList<>();
                    for (Result result : results) {
                        if (result.isEmpty()) {
                            continue;
                        }
                        JSONObject jsonObject = PlatformDocumentUtil.generatPlatformDocumentObject(result, false);
                        if (jsonObject != null) {
                            jsonObjectList.add(jsonObject);
                        }
                    }
                    if (jsonObjectList == null || jsonObjectList.size() == 0) {
                        throw new DataNotExistException("Not found");
                    }
                    response.put("data", jsonObjectList);
                    response.put("queryhit", jsonObjectList.size());
                }
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        } else if (reference.equals("family")) {
            if (StringUtils.isBlank(articleQueryBean.getRangeBy())) {
                ResultScanner resultScanner = hbaseService.getByRowKeyPrefix(webHBaseConf.getQaTableName(), articleQueryBean.getIds()[0], columnList);
                List<JSONObject> jsonObjectList = new ArrayList<>();
                for (Result result : resultScanner) {
                    try {
                        jsonObjectList.add(PlatformDocumentUtil.generatPlatformDocumentObject(result, false));
                        if (jsonObjectList == null) {
                            throw new DataNotExistException("Not found");
                        }
                        response.put("queryhit", jsonObjectList.size());
                        response.put("data", jsonObjectList);
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            } else {
                Map<String, Integer> hitNumMap = new HashMap<>();
                List<JSONObject> jsonObjectList = hbaseService.getByRowKeyPrefixAndFloorFilter(webHBaseConf.getQaTableName(), articleQueryBean.getIds()[0], articleQueryBean.getRangeFrom(), articleQueryBean.getRangeTo(), columnList, hitNumMap);
                if (jsonObjectList == null) {
                    throw new DataNotExistException("Not found");
                }

                try {
                    if (orderBy.startsWith("special_")) {
                        orderBy = orderBy.replace("special_", "");
                        JSONObjectUtils.sortJSONObject(jsonObjectList, "special", Integer.class, orderBy, order);
                    } else if (orderBy.startsWith("dynamic_")) {
                        orderBy = orderBy.replace("dynamic_", "");
                        JSONObjectUtils.sortJSONObject(jsonObjectList, "dynamic", Integer.class, orderBy, order);
                    } else {
                        JSONObjectUtils.sortJSONObject(jsonObjectList, "common", Integer.class, orderBy, order);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }


                /*Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject object1, JSONObject object2) {
                        int compareResult = object1.getJSONObject("special").getInteger("floor").compareTo(object2.getJSONObject("special").getInteger("floor"));
                        if (articleQueryBean.getOrder().equals("desc")) {
                            compareResult *= -1;
                        }
                        return compareResult;
                    }
                });*/
                response.put("queryhit", hitNumMap.get("queryHitNum"));
                response.put("filterhit", hitNumMap.get("filterHitNum"));
                response.put("data", jsonObjectList);
            }
        }
        return response;
    }
}
