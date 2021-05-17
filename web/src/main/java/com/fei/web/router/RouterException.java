package com.fei.web.router;

/**
 *
 * @author jianying9
 */
public class RouterException extends Exception
{

    private final String code;

    private final String msg;

    public RouterException(String code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    public RouterException(String code)
    {
        this.code = code;
        this.msg = "";
    }

    public String getCode()
    {
        return code;
    }

    public String getMsg()
    {
        return msg;
    }

    @Override
    public String toString()
    {
        return this.code + "_" + this.msg;
    }

}
