package com.fei.elasticsearch.index.query;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author jianying9
 */
public class TermQueryBuilder implements QueryBuilder
{

    private final String fieldName;

    private final Object value;

    public TermQueryBuilder(String fieldName, Object value)
    {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject termsJson = new JSONObject();
        termsJson.put(fieldName, value);
        //
        JSONObject queryJson = new JSONObject();
        queryJson.put("term", termsJson);
        return queryJson;
    }

}
