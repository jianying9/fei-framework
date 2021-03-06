package com.fei.web.request.validation;

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
public class ObjectValidationImpl implements ParamValidation
{

    private final String key;
    private final String name;
    private final String type;
    private final String description;
    private final String errorMsg;
    private final Map<String, ParamValidation> paramValidationMap;

    public ObjectValidationImpl(String key, String name, Map<String, ParamValidation> paramValidationMap, String description)
    {
        this.key = key;
        this.name = name;
        this.type = "object";
        this.description = description;
        this.errorMsg = this.name + " must be object";
        this.paramValidationMap = paramValidationMap;
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
    public String validate(Object value)
    {
        String result = "";
        if (value != null) {
            if (value instanceof JSONObject) {
                result = "";
                JSONObject data = (JSONObject) value;
                Object childValue;
                ParamValidation paramValidation;
                Set<String> keySet = new HashSet();
                keySet.addAll(data.keySet());
                for (String param : keySet) {
                    paramValidation = this.paramValidationMap.get(param);
                    if (paramValidation == null) {
                        //没有定义,丢弃
                        data.remove(param);
                    } else {
                        childValue = data.get(param);
                        result = paramValidation.validate(childValue);
                        if (result.isEmpty() == false) {
                            break;
                        }
                    }
                }
            } else {
                result = this.errorMsg;
            }
        }
        return result;
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
        object.put("required", false);
        JSONArray array = new JSONArray();
        array.add(object);
        //子参数
        JSONArray subArray;
        for (ParamValidation paramValidation : this.paramValidationMap.values()) {
            subArray = paramValidation.getApi();
            array.addAll(subArray);
        }
        return array;
    }

}
