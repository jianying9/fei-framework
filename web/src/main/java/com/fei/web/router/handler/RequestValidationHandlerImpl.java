package com.fei.web.router.handler;

import com.alibaba.fastjson.JSONObject;
import com.fei.web.request.Request;
import com.fei.web.response.Response;
import com.fei.web.router.validation.ValidationHandler;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author jianying9
 */
public class RequestValidationHandlerImpl implements RouteHandler
{

    private final RouteHandler nextHandler;

    private final Map<String, ValidationHandler> validationHandlerMap;

    public RequestValidationHandlerImpl(RouteHandler nextHandler, Map<String, ValidationHandler> validationHandlerMap)
    {
        this.nextHandler = nextHandler;
        this.validationHandlerMap = validationHandlerMap;
    }

    @Override
    public Response processRequest(Request request)
    {
        Response response;
        JSONObject data = request.getData();
        Object childValue;
        ValidationHandler validationHandler;
        String result = "";
        for (Entry<String, ValidationHandler> entry : this.validationHandlerMap.entrySet()) {
            validationHandler = entry.getValue();
            childValue = data.get(entry.getKey());
            result = validationHandler.validate(childValue);
            if (result.isEmpty() == false) {
                break;
            }
        }
        if (result.isEmpty()) {
            //验证通过
            response = this.nextHandler.processRequest(request);
        } else {
            //验证失败
            response = new Response(request.getRoute());
            response.setCode(Response.INVALID);
            response.setMsg(result);
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
