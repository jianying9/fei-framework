package com.fei.elasticsearch.script;

import com.alibaba.fastjson.JSONObject;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class Script
{

    private final String lang = "painless";

    private final String source;

    private final Map<String, Object> paramsMap;

    public Script(String source)
    {
        this.source = source;
        this.paramsMap = Collections.EMPTY_MAP;
    }

    public Script(String source, Map<String, Object> paramsMap)
    {
        this.source = source;
        this.paramsMap = paramsMap;
    }

    public JSONObject toJSONObject()
    {
        JSONObject valueJson = new JSONObject();
        valueJson.put("lang", this.lang);
        valueJson.put("source", this.source);
        if (this.paramsMap != null && this.paramsMap.isEmpty()) {
            valueJson.put("params", this.paramsMap);
        }
        //
        JSONObject scriptJson = new JSONObject();
        scriptJson.put("script", valueJson);
        return scriptJson;
    }

}
