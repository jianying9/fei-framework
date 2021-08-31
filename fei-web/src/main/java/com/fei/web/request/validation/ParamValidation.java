package com.fei.web.request.validation;

import com.alibaba.fastjson.JSONArray;

/**
 *
 * @author jianying9
 */
public interface ParamValidation
{

    public String getKey();

    public String getName();

    public String getType();
    
    public String getDescrption();

    public String validate(Object value);
    
    public JSONArray getApi();

}
