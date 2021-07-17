package com.fei.web.router;

import com.alibaba.fastjson.JSONObject;
import com.fei.app.utils.ToolUtil;
import com.fei.web.component.JwtBean;
import com.fei.web.component.Token;
import com.fei.web.logger.BizLogger;
import com.fei.web.logger.BizLoggerFactory;
import com.fei.web.request.Request;
import com.fei.web.response.Response;

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

    public String processRequest(JSONObject input, String auth)
    {
        //初始业务上下文对象
        BizContext bizContext = new BizContext(this.getRoute());
        bizContext.setStartTime(System.currentTimeMillis());
        //初始化业务追踪对象
        String groupId = input.getString("_groupId");
        if (groupId == null || groupId.isEmpty()) {
            groupId = ToolUtil.getAutomicId();
        }
        bizContext.setGroupId(groupId);
        //解析用户token
        Token token = null;
        if (auth == null) {
            //如果没有主动传入auth信息,则从请求参数里面寻找私有参数_auth
            auth = input.getString("_auth");
        }
        if (auth != null && auth.isEmpty() == false) {
            token = JwtBean.INSTANCE.verifyToken(auth);
            if(token != null) {
                bizContext.setUserId(token.userId);
                bizContext.setUserName(token.userName);
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

    @Override
    public String toString()
    {
        return this.routeHandler.toString();
    }

}
