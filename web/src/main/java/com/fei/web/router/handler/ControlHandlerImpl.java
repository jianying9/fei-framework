package com.fei.web.router.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.fei.framework.utils.ToolUtil;
import com.fei.web.request.Request;
import com.fei.web.response.Response;
import com.fei.web.router.RouterContext;
import com.fei.web.router.RouterException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private final Logger logger = LogManager.getLogger(RouterContext.class);

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
        Object[] params = new Object[this.parameters.length];
        for (int index = 0; index < this.parameters.length; index++) {
            paramClass = this.parameters[index].getType();
            params[index] = TypeUtils.castToJavaBean(request.getData(), paramClass);
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
            if (RouterException.class.isInstance(t)) {
                //业务异常处理
                RouterException routerException = (RouterException) t;
                response.setCode(routerException.getCode());
                response.setMsg(routerException.getMsg());
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
