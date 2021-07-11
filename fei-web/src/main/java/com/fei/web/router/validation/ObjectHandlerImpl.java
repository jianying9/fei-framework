package com.fei.web.router.validation;

import com.alibaba.fastjson.JSONObject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * json对象类型处理类
 *
 * @author jianying9
 */
public class ObjectHandlerImpl implements ValidationHandler
{

    private final String key;
    private final String name;
    private final String type;
    private final String errorMsg;
    private final Map<String, ValidationHandler> validationHandlerMap;

    public ObjectHandlerImpl(String key, String name, Map<String, ValidationHandler> validationHandlerMap)
    {
        this.key = key;
        this.name = name;
        this.type = "object";
        this.errorMsg = this.name + " must be object";
        this.validationHandlerMap = validationHandlerMap;
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
                ValidationHandler validationHandler;
                Set<String> keySet = new HashSet();
                keySet.addAll(data.keySet());
                for (String key : keySet) {
                    validationHandler = this.validationHandlerMap.get(key);
                    if (validationHandler == null) {
                        //没有定义,丢弃
                        data.remove(key);
                    } else {
                        childValue = data.get(key);
                        result = validationHandler.validate(childValue);
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

}
