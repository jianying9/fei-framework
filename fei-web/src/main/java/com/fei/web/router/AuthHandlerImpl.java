package com.fei.web.router;

import com.fei.web.component.Token;
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
        Token token = request.getToken();
        if (token == null) {
            //验证失败
            response = new Response(request.getRoute());
            response.setCode(Response.UNLOGIN);
            response.setMsg(Response.UNLOGIN);
        } else if (token.expired) {
            //过期
            response = new Response(request.getRoute());
            response.setCode(Response.EXPIRED);
            response.setMsg(Response.EXPIRED);
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
