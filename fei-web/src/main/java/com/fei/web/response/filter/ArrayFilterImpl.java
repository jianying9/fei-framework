package com.fei.web.response.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.List;

/**
 * 集合通用类型处理类
 *
 * @author jianying9
 */
public class ArrayFilterImpl implements ParamFilter
{

    private final String type;

    private final ParamFilter paramFilter;

    public ArrayFilterImpl(ParamFilter paramFilter)
    {
        this.paramFilter = paramFilter;
        this.type = "array<" + this.paramFilter.getType() + ">";
    }

    @Override
    public String getKey()
    {
        return this.paramFilter.getKey();
    }

    @Override
    public String getName()
    {
        return this.paramFilter.getName();
    }

    @Override
    public void filter(Object value)
    {
        if (value != null) {
            if (value instanceof JSONArray) {
                JSONArray valueArray = (JSONArray) value;
                for (Object subValue : valueArray) {
                    this.paramFilter.filter(subValue);
                }
            } else if (value instanceof List) {
                List<Object> valueList = (List<Object>) value;
                for (Object subValue : valueList) {
                    this.paramFilter.filter(subValue);
                }
            }
        }
    }

    @Override
    public String getType()
    {
        return this.type;
    }

    @Override
    public String getDescrption()
    {
        return this.paramFilter.getDescrption();
    }

    @Override
    public JSONArray getApi()
    {
        JSONArray array = this.paramFilter.getApi();
        if (false == array.isEmpty()) {
            JSONObject object = array.getJSONObject(0);
            object.put("type", this.type);
        }
        return array;
    }

}
