package com.fei.elasticsearch.index.query;

import com.alibaba.fastjson.JSONObject;
import java.util.Collection;

/**
 *
 * @author jianying9
 */
public class TermsQueryBuilder implements QueryBuilder
{

    private final String fieldName;

    private final Collection<Object> values;

    public TermsQueryBuilder(String fieldName, Collection<Object> values)
    {
        this.fieldName = fieldName;
        this.values = values;
    }
    
    @Override
    public boolean canFilter()
    {
        return true;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject termsJson = new JSONObject();
        termsJson.put(fieldName, values);
        //
        JSONObject queryJson = new JSONObject();
        queryJson.put("terms", termsJson);
        return queryJson;
    }

}
