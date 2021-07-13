package com.fei.web.request;

import com.fei.web.router.BizContext;
import com.alibaba.fastjson.JSONObject;
import com.fei.web.component.Token;

/**
 * 请求信息
 *
 * @author jianying9
 */
public class Request
{

    private final BizContext bizContext;

    private final Token token;

    private final JSONObject data;

    public Request(BizContext bizContext, Token token, JSONObject data)
    {
        this.bizContext = bizContext;
        this.token = token;
        this.data = data;
    }

    public String getRoute()
    {
        return this.bizContext.getRoute();
    }

    public Token getToken()
    {
        return token;
    }

    public JSONObject getData()
    {
        return data;
    }

    public BizContext getBizContext()
    {
        return this.bizContext;
    }

}
