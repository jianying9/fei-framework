package com.fei.web.router;

import com.alibaba.fastjson.JSONObject;
import com.fei.web.request.Request;
import com.fei.web.response.Response;
import java.util.Map;
import com.fei.web.response.filter.ParamFilter;
import java.util.HashSet;
import java.util.Set;

/**
 * 响应参数过滤
 *
 * @author jianying9
 */
public class ResponseFilterHandlerImpl implements RouteHandler
{

    private final RouteHandler nextHandler;

    private final Map<String, ParamFilter> paramFilterMap;

    public ResponseFilterHandlerImpl(RouteHandler nextHandler, Map<String, ParamFilter> paramFilterMap)
    {
        this.nextHandler = nextHandler;
        this.paramFilterMap = paramFilterMap;
    }

    @Override
    public Response processRequest(Request request)
    {
        Response response = this.nextHandler.processRequest(request);
        //删除多余的响应参数
        JSONObject data = response.getData();
        Object childValue;
        ParamFilter paramFilter;
        Set<String> keySet = new HashSet();
        keySet.addAll(data.keySet());
        for (String param : keySet) {
            paramFilter = this.paramFilterMap.get(param);
            if (paramFilter == null) {
                //没有定义,丢弃
                data.remove(param);
            } else {
                childValue = data.get(param);
                paramFilter.filter(childValue);
            }
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
