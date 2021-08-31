package com.fei.module;

import com.fei.annotations.web.Controller;
import com.fei.annotations.web.RequestMapping;
import com.fei.app.context.AppContext;
import com.fei.annotations.module.Module;
import com.fei.app.module.ModuleContext;
import com.fei.app.bean.BeanContext;
import com.fei.app.utils.ClassUtil;
import com.fei.web.router.RouterContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Controller 接口
 * @author jianying9
 */
@Module
public class ControllerContext implements ModuleContext
{

    private final String name = "controller";

    private final Logger logger = LoggerFactory.getLogger(ControllerContext.class);

    private final List<Object> controllerList = new ArrayList();

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void init(Set<Class<?>> classSet)
    {
        Object bean;
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        try {
            for (Class<?> clazz : classSet) {
                if (clazz.isAnnotationPresent(Controller.class)) {
                    this.logger.info("find Controller class:{}.", clazz.getName());
                    bean = clazz.getDeclaredConstructor().newInstance();
                    //
                    this.controllerList.add(bean);
                    //
                    beanContext.add(this.name, bean);
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        ClassUtil.removeClass(classSet, controllerList);
    }

    @Override
    public void build()
    {
        //初始化router
        Class<?> clazz;
        Controller control;
        RequestMapping requestMapping;
        String route;
        boolean auth;
        RouterContext routerContext = RouterContext.INSTANCE;
        for (Object controller : this.controllerList) {
            clazz = controller.getClass();
            control = clazz.getAnnotation(Controller.class);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (Modifier.isStatic(method.getModifiers()) == false && method.isAnnotationPresent(RequestMapping.class)) {
                    requestMapping = method.getAnnotation(RequestMapping.class);
                    route = control.value() + requestMapping.value();
                    auth = control.auth();
                    if (auth == false) {
                        auth = requestMapping.auth();
                    }
                    routerContext.add(route, control.name(), requestMapping.description(), controller, method, auth);
                }
            }
        }
    }

}
