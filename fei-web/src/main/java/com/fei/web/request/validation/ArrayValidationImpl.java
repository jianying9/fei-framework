package com.fei.web.request.validation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.List;

/**
 * 集合通用类型处理类
 *
 * @author jianying9
 */
public class ArrayValidationImpl implements ParamValidation
{

    private final String errorMsg;

    private final String type;

    private final ParamValidation paramValidation;

    public ArrayValidationImpl(ParamValidation paramValidation)
    {
        this.paramValidation = paramValidation;
        this.errorMsg = this.paramValidation.getName() + " must be array<" + this.paramValidation.getType() + ">";
        this.type = "array<" + this.paramValidation.getType() + ">";
    }

    @Override
    public String getKey()
    {
        return this.paramValidation.getKey();
    }

    @Override
    public String getName()
    {
        return this.paramValidation.getName();
    }

    @Override
    public String validate(Object value)
    {
        String result = "";
        if (value != null) {
            if (value instanceof JSONArray) {
                JSONArray valueArray = (JSONArray) value;
                for (Object subValue : valueArray) {
                    result = this.paramValidation.validate(subValue);
                    if (result.isEmpty() == false) {
                        break;
                    }
                }
            } else if (value instanceof List) {
                List<Object> valueList = (List<Object>) value;
                for (Object subValue : valueList) {
                    result = this.paramValidation.validate(subValue);
                    if (result.isEmpty() == false) {
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

    @Override
    public String getDescrption()
    {
        return this.paramValidation.getDescrption();
    }

    @Override
    public JSONArray getApi()
    {
        JSONArray array = this.paramValidation.getApi();
        if (false == array.isEmpty()) {
            JSONObject object = array.getJSONObject(0);
            object.put("type", this.type);
        }
        return array;
    }

}
