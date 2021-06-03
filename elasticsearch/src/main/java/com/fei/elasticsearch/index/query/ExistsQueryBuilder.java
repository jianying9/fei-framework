package com.fei.elasticsearch.index.query;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author jianying9
 */
public class ExistsQueryBuilder implements QueryBuilder
{

    private final String fieldName;

    public ExistsQueryBuilder(String fieldName)
    {
        this.fieldName = fieldName;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject existsJson = new JSONObject();
        existsJson.put("field", fieldName);
        //
        JSONObject queryJson = new JSONObject();
        queryJson.put("exists", existsJson);
        return queryJson;
    }

}
