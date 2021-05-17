package com.fei.web.request;

import com.alibaba.fastjson.JSONObject;

/**
 * 请求信息
 *
 * @author jianying9
 */
public class Request
{

    private final String route;

    private final String auth;

    private final JSONObject data;

    public Request(String route, String auth, JSONObject data)
    {
        this.route = route;
        this.auth = auth;
        this.data = data;
    }

    public String getRoute()
    {
        return route;
    }

    public String getAuth()
    {
        return auth;
    }

    public JSONObject getData()
    {
        return data;
    }

}
