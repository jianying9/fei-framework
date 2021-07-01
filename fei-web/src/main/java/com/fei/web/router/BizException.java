package com.fei.web.router;

/**
 * 业务异常
 *
 * @author jianying9
 */
public class BizException extends Exception
{

    private final String code;

    private final String msg;

    public BizException(String code, String msg)
    {
        this.code = "biz_" + code;
        this.msg = msg;
    }

    public BizException(String code)
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
