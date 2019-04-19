package us.gfzj.web.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Zhiqiang Lin
 * @Description
 * @create 2019/2/28.
 */
public class JSONObjectUtils {
    public static void sortJSONObject(List<JSONObject> jsonObjectList, String keyType, Class valueType, String sortedKey, String order) throws Exception {
        if ("special".equals(keyType)) {
            Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject object1, JSONObject object2) {
                    int compareResult = object1.getJSONObject("special").getInteger(sortedKey).compareTo(object2.getJSONObject("special").getInteger(sortedKey));
                    if (order.equals("desc")) {
                        compareResult *= -1;
                    }
                    return compareResult;
                }
            });
        } else if ("common".equals(keyType)) {
            Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject object1, JSONObject object2) {
                    int compareResult = object1.getInteger(sortedKey).compareTo(object2.getInteger(sortedKey));
                    if (order.equals("desc")) {
                        compareResult *= -1;
                    }
                    return compareResult;
                }
            });
        } else if ("dynamic".equals(keyType)) {
            Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject object1, JSONObject object2) {
                    int compareResult = object1.getJSONObject("dynamic").getInteger(sortedKey).compareTo(object2.getJSONObject("dynamic").getInteger(sortedKey));
                    if (order.equals("desc")) {
                        compareResult *= -1;
                    }
                    return compareResult;
                }
            });
        }
    }
}
