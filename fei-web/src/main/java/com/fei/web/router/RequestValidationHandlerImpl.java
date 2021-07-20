package com.fei.web.router;

import com.alibaba.fastjson.JSONObject;
import com.fei.web.request.Request;
import com.fei.web.response.Response;
import com.fei.web.router.validation.ValidationHandler;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        //丢弃没有定义的参数
        Set<String> keySet = new HashSet();
        keySet.addAll(data.keySet());
        for (String key : keySet) {
            validationHandler = this.validationHandlerMap.get(key);
            if (validationHandler == null) {
                //没有定义,丢弃
                data.remove(key);
            }
        }
        //验证
        String result = "";
        for (Map.Entry<String, ValidationHandler> entry : this.validationHandlerMap.entrySet()) {
            childValue = data.get(entry.getKey());
            validationHandler = entry.getValue();
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
