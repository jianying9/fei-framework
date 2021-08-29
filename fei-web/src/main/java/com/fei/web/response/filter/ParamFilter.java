package com.fei.web.response.filter;

/**
 *
 * @author jianying9
 */
public interface ParamFilter
{

    public String getKey();

    public String getName();

    public String getType();

    public void filter(Object value);

}
