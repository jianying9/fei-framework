package com.fei.web.response.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 基础数据类型
 *
 * @author jianying9
 */
public final class BasicFilterImpl implements ParamFilter
{

    private final String key;
    private final String name;
    private final String type;
    private final String description;

    public BasicFilterImpl(String key, String name, String type, String description)
    {
        this.key = key;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    @Override
    public void filter(Object value)
    {
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getKey()
    {
        return this.key;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public String getDescrption()
    {
        return this.description;
    }

    @Override
    public JSONArray getApi()
    {
        JSONObject object = new JSONObject();
        object.put("name", this.getName());
        object.put("type", this.getType());
        object.put("description", this.getDescrption());
        JSONArray array = new JSONArray();
        array.add(object);
        return array;
    }

}
