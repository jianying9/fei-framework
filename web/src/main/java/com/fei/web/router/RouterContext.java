package com.fei.web.router;

import com.fei.framework.bean.BeanContext;
import com.fei.framework.context.AppContext;
import com.fei.web.router.handler.ControlHandlerImpl;
import com.fei.web.router.handler.RouteHandler;
import java.lang.reflect.Method;

/**
 * 路由对象上下文
 *
 * @author jianying9
 */
public class RouterContext
{

    private final String name = "router";

    public final static RouterContext CONTEXT = new RouterContext();

    public void add(String route, Object controller, Method method)
    {
        Router router = this.create(route, controller, method);
        //注册到bean
        BeanContext beanContext = AppContext.CONTEXT.getBeanContext();
        beanContext.add(this.name, router.getRoute(), router);
    }

    private Router create(String route, Object controller, Method method)
    {
        RouteHandler routeHandler = new ControlHandlerImpl(route, controller, method);
        Router router = new Router(routeHandler);
        return router;
    }

    public Router get(String route)
    {
        BeanContext beanContext = AppContext.CONTEXT.getBeanContext();
        return beanContext.get(this.name, route);
    }

}
