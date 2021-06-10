package com.fei.web.router.handler;

import com.alibaba.fastjson.JSONObject;
import com.fei.web.component.Session;
import com.fei.web.request.Request;
import com.fei.web.response.Response;

/**
 *
 * @author jianying9
 */
public class AuthHandlerImpl implements RouteHandler
{

    private final RouteHandler nextHandler;

    public AuthHandlerImpl(RouteHandler nextHandler)
    {
        this.nextHandler = nextHandler;
    }

    @Override
    public Response processRequest(Request request)
    {
        Response response;
        JSONObject data = request.getData();
        Session session = request.getSession();
        if (session == null) {
            //验证失败
            response = new Response(request.getRoute());
            response.setCode(Response.UNLOGIN);
        } else {
            //验证通过
            response = this.nextHandler.processRequest(request);
        }
        return response;
    }

    @Override
    public String toString()
    {
        return this.nextHandler.toString();
    }

    @Override
    public String getRoute()
    {
        return this.nextHandler.getRoute();
    }

}
