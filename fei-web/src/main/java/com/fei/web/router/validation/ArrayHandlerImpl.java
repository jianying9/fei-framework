package com.fei.web.router.validation;

import com.alibaba.fastjson.JSONArray;
import java.util.List;

/**
 * 集合通用类型处理类
 *
 * @author jianying9
 */
public class ArrayHandlerImpl implements ValidationHandler
{

    private final String errorMsg;
    
    private final String type;

    private final ValidationHandler typeValidationHandler;

    public ArrayHandlerImpl(ValidationHandler typeValidationHandler)
    {
        this.typeValidationHandler = typeValidationHandler;
        this.errorMsg = this.typeValidationHandler.getName() + " must be array<" + this.typeValidationHandler.getType() + ">";
        this.type = "array<" + this.typeValidationHandler.getType() + ">";
    }

    @Override
    public String getKey()
    {
        return this.typeValidationHandler.getKey();
    }

    @Override
    public String getName()
    {
        return this.typeValidationHandler.getName();
    }

    @Override
    public String validate(Object value)
    {
        String result = "";
        if (value != null) {
            if (value instanceof JSONArray) {
                JSONArray valueArray = (JSONArray) value;
                for (Object subValue : valueArray) {
                    result = this.typeValidationHandler.validate(subValue);
                    if (result.isEmpty() == false) {
                        result = this.errorMsg;
                        break;
                    }
                }
            } else if (value instanceof List) {
                List<Object> valueList = (List<Object>) value;
                for (Object subValue : valueList) {
                    result = this.typeValidationHandler.validate(subValue);
                    if (result.isEmpty() == false) {
                        result = this.errorMsg;
                        break;
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
