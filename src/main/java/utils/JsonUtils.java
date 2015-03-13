package utils;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    public static ObjectMapper MAPPER = null;

    static {
        MAPPER = new ObjectMapper(null, null, null, null, null);
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        MAPPER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(DeserializationConfig.Feature.USE_GETTERS_AS_SETTERS, false);
        MAPPER.getDeserializationConfig().setDateFormat(DateUtils.getSecondFormatter());
    }

    public static String toStr(Object model) throws IOException {
        return MAPPER.writeValueAsString(model);
    }

    public static <T> T fromStr(String content, Class<T> clazz) throws IOException {
        return MAPPER.readValue(content, clazz);
    }

    public static Map<String, Object> fromStr(String content) throws IOException {
        return fromStr(content, Map.class);
    }

    public static String ListToStr(List list) throws IOException {
        return MAPPER.writeValueAsString(list);
    }

    /**
     * 对json串进行反序列化，得到列表对象
     *
     * @param content
     * @param clazz
     * @return
     * @throws java.io.IOException
     * @throws org.json.JSONException
     */
    public static <T> List<T> fromArray(String content, Class<T> clazz) {
        List<T> resultList = new ArrayList<T>();
        JSONArray array = null;
        try {
            array = new JSONArray(content);
        } catch (JSONException e) {
        } finally {
            for (int i = 0; array != null && i < array.length(); i++) {
                T t = null;
                try {
                    t = fromStr(array.getString(i), clazz);
                } catch (Exception e) {
                }
                resultList.add(t);
            }
        }
        return resultList;
    }
}
