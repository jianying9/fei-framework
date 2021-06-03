package com.fei.elasticsearch.index.query;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author jianying9
 */
public class MultiMatchQueryBuilder implements QueryBuilder
{

    private final String[] fieldNames;

    private final String text;

    public MultiMatchQueryBuilder(String text, String... fieldNames)
    {
        this.fieldNames = fieldNames;
        this.text = text;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject multiJson = new JSONObject();
        multiJson.put("query", text);
        multiJson.put("fields", this.fieldNames);
        //
        JSONObject queryJson = new JSONObject();
        queryJson.put("multi_match", multiJson);
        return queryJson;
    }
}
