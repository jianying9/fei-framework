package com.fei.web.request.validation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 非空通用类型处理
 *
 * @author jianying9
 */
public class RequiredValidationImpl implements ParamValidation
{

    private final String errorMsg;

    private final ParamValidation paramValidation;

    public RequiredValidationImpl(ParamValidation paramValidation)
    {
        this.errorMsg = paramValidation.getName() + " can't be null or empty";
        this.paramValidation = paramValidation;
    }

    @Override
    public final String validate(Object value)
    {
        String result;
        if (value != null && value.toString().isEmpty() == false) {
            result = this.paramValidation.validate(value);
        } else {
            result = this.errorMsg;
        }
        return result;
    }

    @Override
    public final String getName()
    {
        return this.paramValidation.getName();
    }

    @Override
    public String getKey()
    {
        return this.paramValidation.getKey();
    }

    @Override
    public String getType()
    {
        return this.paramValidation.getType();
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
            object.put("required", true);
        }
        return array;
    }

}
