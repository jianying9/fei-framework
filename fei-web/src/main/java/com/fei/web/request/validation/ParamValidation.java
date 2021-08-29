package com.fei.web.request.validation;

/**
 *
 * @author jianying9
 */
public interface ParamValidation
{

    public String getKey();

    public String getName();

    public String getType();

    public String validate(Object value);

}
