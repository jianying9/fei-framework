package com.fei.web.router;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fei.app.utils.ToolUtil;
import com.fei.web.component.JwtBean;
import com.fei.web.component.Token;
import com.fei.web.logger.BizLogger;
import com.fei.web.logger.BizLoggerFactory;
import com.fei.web.request.Request;
import com.fei.web.request.validation.ParamValidation;
import com.fei.web.response.Response;
import com.fei.web.response.filter.ParamFilter;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class Router
{

    private final Map<String, ParamValidation> paramValidationMap;

    private final Map<String, ParamFilter> paramFilterMap;

    private final RouteHandler routeHandler;

    public Router(RouteHandler routeHandler, Map<String, ParamValidation> paramValidationMap, Map<String, ParamFilter> paramFilterMap)
    {
        this.routeHandler = routeHandler;
        this.paramValidationMap = paramValidationMap;
        this.paramFilterMap = paramFilterMap;
    }

    public String getRoute()
    {
        return this.routeHandler.getRoute();
    }

    public String processRequest(JSONObject input, String auth)
    {
        String reponseBody;
        //
        String _api = input.getString("_api");
        if ("true".equals(_api)) {
            //执行接口描述
            reponseBody = this.processApi();
        } else {
            //执行业务
            reponseBody = this.processBiz(input, auth);
        }
        return reponseBody;
    }

    private String processBiz(JSONObject input, String auth)
    {
        //初始业务上下文对象
        BizContext bizContext = new BizContext(this.getRoute());
        bizContext.setStartTime(System.currentTimeMillis());
        //初始化业务追踪对象
        String _groupId = input.getString("_groupId");
        if (_groupId == null || _groupId.isEmpty()) {
            _groupId = ToolUtil.getAutomicId();
        }
        bizContext.setGroupId(_groupId);
        //解析用户token
        Token token = null;
        if (auth == null) {
            //如果没有主动传入auth信息,则从请求参数里面寻找私有参数_auth
            auth = input.getString("_auth");
        }
        if (auth != null && auth.isEmpty() == false) {
            token = JwtBean.INSTANCE.verifyToken(auth);
            if (token != null) {
                bizContext.setUserId(token.id);
                bizContext.setUserName(token.name);
            }
        }
        Request request = new Request(bizContext, token, input);
        Response response = this.routeHandler.processRequest(request);
        JSONObject output = response.toJSONObject();
        String reponseBody = output.toJSONString();
        String requestBody = input.toJSONString();
        bizContext.setEndTime(System.currentTimeMillis());
        //业务日志
        BizLogger bizLogger = BizLoggerFactory.getBizLogger();
        bizLogger.log(bizContext, requestBody, reponseBody);
        return reponseBody;
    }

    private String processApi()
    {
        JSONArray requestArray = new JSONArray();
        for (ParamValidation paramValidation : this.paramValidationMap.values()) {
            requestArray.addAll(paramValidation.getApi());
        }
        //
        JSONArray responseArray = new JSONArray();
        for (ParamFilter paramFilter : this.paramFilterMap.values()) {
            responseArray.addAll(paramFilter.getApi());
        }
        JSONObject output = new JSONObject();
        output.put("requestArray", requestArray);
        output.put("responseArray", responseArray);
        return output.toJSONString();
    }

    @Override
    public String toString()
    {
        return this.routeHandler.toString();
    }

}
