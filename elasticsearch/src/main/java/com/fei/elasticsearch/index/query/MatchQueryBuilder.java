package com.fei.elasticsearch.index.query;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author jianying9
 */
public class MatchQueryBuilder implements QueryBuilder
{

    private final String fieldName;

    private final String text;

    public MatchQueryBuilder(String fieldName, String text)
    {
        this.fieldName = fieldName;
        this.text = text;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject matchJson = new JSONObject();
        matchJson.put(fieldName, text);
        //
        JSONObject queryJson = new JSONObject();
        queryJson.put("match", matchJson);
        return queryJson;
    }
}
