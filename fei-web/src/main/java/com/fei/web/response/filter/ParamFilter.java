package com.fei.web.response.filter;

import com.alibaba.fastjson.JSONArray;

/**
 *
 * @author jianying9
 */
public interface ParamFilter
{

    public String getKey();

    public String getName();

    public String getType();
    
    public String getDescrption();

    public void filter(Object value);
    
    public JSONArray getApi();

}
