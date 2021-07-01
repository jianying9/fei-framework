package com.fei.web.router.handler;

import com.fei.web.request.Request;
import com.fei.web.response.Response;

/**
 *
 * @author jianying9
 */
public interface RouteHandler
{
    public String getRoute();

    public Response processRequest(Request request);

    @Override
    public String toString();
}
