package com.fei.web.request;

import com.alibaba.fastjson.JSONObject;
import com.fei.web.component.Session;

/**
 * 请求信息
 *
 * @author jianying9
 */
public class Request
{

    private final String route;

    private final Session session;

    private final JSONObject data;

    public Request(String route, Session session, JSONObject data)
    {
        this.route = route;
        this.session = session;
        this.data = data;
    }

    public String getRoute()
    {
        return route;
    }

    public Session getSession()
    {
        return session;
    }

    public JSONObject getData()
    {
        return data;
    }

}
