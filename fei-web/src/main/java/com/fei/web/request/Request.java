package com.fei.web.request;

import com.alibaba.fastjson.JSONObject;
import com.fei.web.component.Token;

/**
 * 请求信息
 *
 * @author jianying9
 */
public class Request
{

    private final String route;

    private final Token token;

    private final JSONObject data;

    public Request(String route, Token token, JSONObject data)
    {
        this.route = route;
        this.token = token;
        this.data = data;
    }

    public String getRoute()
    {
        return route;
    }

    public Token getToken()
    {
        return token;
    }

    public JSONObject getData()
    {
        return data;
    }

}
