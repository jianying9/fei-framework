package com.fei.web.request.validation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 字符类型处理类
 *
 * @author jianying9
 */
public abstract class AbstractParamValidation implements ParamValidation
{

    @Override
    public abstract String getName();

    @Override
    public abstract String getType();

    @Override
    public abstract String getDescrption();

    @Override
    public JSONArray getApi()
    {
        JSONObject object = new JSONObject();
        object.put("name", this.getName());
        object.put("type", this.getType());
        object.put("description", this.getDescrption());
        object.put("required", false);
        JSONArray array = new JSONArray();
        array.add(object);
        return array;
    }

}
