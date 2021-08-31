package com.fei.web.response.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * json对象类型处理类
 *
 * @author jianying9
 */
public class ObjectFilterImpl implements ParamFilter
{

    private final String key;
    private final String name;
    private final String type;
    private final String description;
    private final Map<String, ParamFilter> paramFilterMap;

    public ObjectFilterImpl(String key, String name, Map<String, ParamFilter> paramFilterMap, String description)
    {
        this.key = key;
        this.name = name;
        this.description = description;
        this.type = "object";
        this.paramFilterMap = paramFilterMap;
    }

    @Override
    public String getKey()
    {
        return this.key;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void filter(Object value)
    {
        if (value != null) {
            if (value instanceof JSONObject) {
                JSONObject data = (JSONObject) value;
                Object childValue;
                ParamFilter paramFilter;
                Set<String> keySet = new HashSet();
                keySet.addAll(data.keySet());
                for (String param : keySet) {
                    paramFilter = this.paramFilterMap.get(param);
                    if (paramFilter == null) {
                        //没有定义,丢弃
                        data.remove(param);
                    } else {
                        childValue = data.get(param);
                        paramFilter.filter(childValue);
                    }
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
        //子参数
        JSONArray subArray;
        for (ParamFilter paramFilter : this.paramFilterMap.values()) {
            subArray = paramFilter.getApi();
            array.addAll(subArray);
        }
        return array;
    }

}
