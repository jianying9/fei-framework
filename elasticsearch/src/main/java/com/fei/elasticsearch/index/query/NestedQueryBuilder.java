package com.fei.elasticsearch.index.query;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author jianying9
 */
public class NestedQueryBuilder implements QueryBuilder
{

    private final String path;

    private final QueryBuilder queryBuilder;

    public NestedQueryBuilder(String path, QueryBuilder queryBuilder)
    {
        this.path = path;
        this.queryBuilder = queryBuilder;
    }
    
    @Override
    public boolean canFilter()
    {
        return false;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject nestedJson = new JSONObject();
        nestedJson.put("path", this.path);
        nestedJson.put("score_mode", "none");
        nestedJson.put("query", this.queryBuilder.toJSONObject());
        //
        JSONObject queryJson = new JSONObject();
        queryJson.put("nested", nestedJson);
        return queryJson;
    }

}
