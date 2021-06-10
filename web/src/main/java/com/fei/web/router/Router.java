package com.fei.web.router;

import com.alibaba.fastjson.JSONObject;
import com.fei.web.component.JwtBean;
import com.fei.web.component.Session;
import com.fei.web.request.Request;
import com.fei.web.response.Response;
import com.fei.web.router.handler.RouteHandler;

/**
 *
 * @author jianying9
 */
public class Router
{

    private final RouteHandler routeHandler;

    public Router(RouteHandler routeHandler)
    {
        this.routeHandler = routeHandler;
    }

    public String getRoute()
    {
        return this.routeHandler.getRoute();
    }

    public JSONObject processRequest(JSONObject input, String auth)
    {
        Session session = null;
        if (auth != null) {
            session = JwtBean.INSTANCE.verifyToken(auth);
        }
        Request request = new Request(this.getRoute(), session, input);
        Response response = this.routeHandler.processRequest(request);
        return response.toJSONObject();
    }

    @Override
    public String toString()
    {
        return this.routeHandler.toString();
    }

}
