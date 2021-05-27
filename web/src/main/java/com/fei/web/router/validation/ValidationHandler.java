package com.fei.web.router.validation;

/**
 *
 * @author jianying9
 */
public interface ValidationHandler
{

    public String getKey();

    public String getName();

    public String getType();

    public String validate(Object value);

}
