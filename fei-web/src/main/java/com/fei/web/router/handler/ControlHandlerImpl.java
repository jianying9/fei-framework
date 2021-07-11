package com.fei.web.router.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.fei.app.utils.ToolUtil;
import com.fei.web.component.Session;
import com.fei.web.component.Token;
import com.fei.web.request.Request;
import com.fei.web.response.Response;
import com.fei.web.router.RouterContext;
import com.fei.web.router.BizException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jianying9
 */
public class ControlHandlerImpl implements RouteHandler
{

    private final String route;

    private final Object controller;

    private final Method method;

    private final Parameter[] parameters;

    private final Logger logger = LoggerFactory.getLogger(RouterContext.class);

    public ControlHandlerImpl(String route, Object controller, Method method)
    {
        this.route = route;
        this.controller = controller;
        this.method = method;
        this.parameters = this.method.getParameters();
    }

    @Override
    public Response processRequest(Request request)
    {
        Response response = new Response(request.getRoute());
        //注入请求参数
        Class<?> paramClass;
        String paramName;
        Object[] params = new Object[this.parameters.length];
        for (int index = 0; index < this.parameters.length; index++) {
            paramClass = this.parameters[index].getType();
            if (paramClass == Session.class) {
                Session session = null;
                Token token = request.getToken();
                if (token != null && token.expired == false) {
                    session = new Session();
                    session.userId = token.userId;
                    session.userName = token.userName;
                    session.expireTime = token.expireTime;
                }
                params[index] = session;
            } else if (ToolUtil.isBasicType(paramClass) || Collection.class.isAssignableFrom(paramClass) || paramClass.isArray()) {
                paramName = this.parameters[index].getName();
                params[index] = request.getData().get(paramName);
            } else {
                params[index] = TypeUtils.castToJavaBean(request.getData(), paramClass);
            }
        }
        //调用并处理结果
        try {
            Object data = this.method.invoke(this.controller, params);
            if (data != null) {
                String dataJson = JSON.toJSONStringWithDateFormat(data, ToolUtil.DATE_FORMAT);
                JSONObject jsonData = JSON.parseObject(dataJson);
                response.setData(jsonData);
            }
        } catch (InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (BizException.class.isInstance(t)) {
                //业务异常处理
                BizException bizException = (BizException) t;
                response.setCode(bizException.getCode());
                response.setMsg(bizException.getMsg());
            } else {
                response.setCode(Response.EXCEPTION);
                this.logger.error("exec route:{}, msg:{}", this.route, this.toString(), ex);
            }
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            //反射异常
            response.setCode(Response.EXCEPTION);
            this.logger.error("exec route:{}, msg:{}", this.route, this.toString(), ex);
        } catch (RuntimeException ex) {
            //运行时异常处理
            response.setCode(Response.EXCEPTION);
            this.logger.error("exec route:{}, msg:{}", this.route, this.toString(), ex);
        }
        return response;
    }

    @Override
    public String toString()
    {
        return this.controller.getClass().getName() + "_" + this.method.getName();
    }

    @Override
    public String getRoute()
    {
        return this.route;
    }

}
