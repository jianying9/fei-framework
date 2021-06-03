package com.fei.elasticsearch.search.sort;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author jianying9
 */
public class FieldSortBuilder implements SortBuilder
{

    private final String fieldName;

    private SortOrder order = SortOrder.ASC;

    public FieldSortBuilder(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public FieldSortBuilder order(SortOrder order)
    {
        this.order = order;
        return this;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject fieldJson = new JSONObject();
        fieldJson.put("order", order.toString());
        //
        JSONObject queryJson = new JSONObject();
        queryJson.put(this.fieldName, fieldJson);
        return queryJson;
    }

}
