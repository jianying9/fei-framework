package com.fei.elasticsearch.index.query;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author jianying9
 */
public class RangeQueryBuilder implements QueryBuilder
{

    private final String fieldName;
    private Object minValue = null;
    private Object maxValue = null;
    private boolean gte = false;
    private boolean gt = false;
    private boolean lte = false;
    private boolean lt = false;

    public RangeQueryBuilder(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public RangeQueryBuilder gt(Object from)
    {
        this.gt = true;
        this.gte = false;
        this.minValue = from;
        return this;
    }

    public RangeQueryBuilder gte(Object from)
    {
        this.gt = false;
        this.gte = true;
        this.minValue = from;
        return this;
    }

    public RangeQueryBuilder lt(Object to)
    {
        this.lt = true;
        this.lte = false;
        this.maxValue = to;
        return this;
    }

    public RangeQueryBuilder lte(Object to)
    {
        this.lt = false;
        this.lte = true;
        this.minValue = to;
        return this;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject valueJson = new JSONObject();
        if (this.gt) {
            valueJson.put("gt", minValue);
        }
        if (this.gte) {
            valueJson.put("gte", minValue);
        }
        if (this.lt) {
            valueJson.put("lt", maxValue);
        }
        if (this.lte) {
            valueJson.put("lte", maxValue);
        }
        //
        JSONObject rangeJson = new JSONObject();
        rangeJson.put(fieldName, valueJson);
        //
        JSONObject queryJson = new JSONObject();
        queryJson.put("range", rangeJson);
        return queryJson;
    }
}
