package com.fei.web.router.validation;

import com.alibaba.fastjson.JSONObject;
import java.util.Map;

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
                JSONObject jsonValue = (JSONObject) value;
                Object childValue;
                ValidationHandler validationHandler;
                if (this.validationHandlerMap.isEmpty() == false) {
                    for (Map.Entry<String, ValidationHandler> entry : this.validationHandlerMap.entrySet()) {
                        validationHandler = entry.getValue();
                        childValue = jsonValue.get(entry.getKey());
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
