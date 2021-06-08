package com.fei.web.router;

import com.fei.framework.bean.BeanContext;
import com.fei.framework.context.AppContext;
import com.fei.web.router.handler.ControlHandlerImpl;
import com.fei.web.router.handler.RequestValidationHandlerImpl;
import com.fei.web.router.handler.RouteHandler;
import com.fei.web.router.validation.ArrayHandlerImpl;
import com.fei.web.router.validation.BooleanHandlerImpl;
import com.fei.web.router.validation.DoubleHandlerImpl;
import com.fei.web.router.validation.IntegerHandlerImpl;
import com.fei.web.router.validation.NotNullHandlerImpl;
import com.fei.web.router.validation.ObjectHandlerImpl;
import com.fei.web.router.validation.RegexHandlerImpl;
import com.fei.web.router.validation.StringHandlerImpl;
import com.fei.web.router.validation.ValidationHandler;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fei.module.RequestParam;

/**
 * 路由对象上下文
 *
 * @author jianying9
 */
public class RouterContext
{

    private final String name = "router";

    private final Logger logger = LogManager.getLogger(RouterContext.class);

    public final static RouterContext INSTANCE = new RouterContext();

    private final List<Class<?>> currClassLinkList = new ArrayList();

    private Object currController = null;

    private Method currMethod = null;

    private Parameter currParameter = null;

    public void add(String route, Object controller, Method method)
    {
        Router router = this.create(route, controller, method);
        //注册到bean
        BeanContext beanContext = AppContext.CONTEXT.getBeanContext();
        beanContext.add(this.name, router.getRoute(), router);
    }

    private Router create(String route, Object controller, Method method)
    {
        this.currController = controller;
        this.currMethod = method;
        //校验-输入对象和输出对象只能是
        Class<?> returnType = method.getReturnType();
        if (Collection.class.isAssignableFrom(returnType)) {
            this.logger.error("{}:{} unsupport Collection returnType", currController.getClass().getName(), currMethod.getName());
            throw new RuntimeException(method.getName() + " unsupport Collection returnType");
        } else if (returnType.isArray()) {
            this.logger.error("{}:{} unsupport Array returnType", currController.getClass().getName(), currMethod.getName());
            throw new RuntimeException(method.getName() + " unsupport Array returnType");
        }
        //
        Class<?> paramClass;
        String paramName;
        Parameter[] parameterArray = method.getParameters();
        for (Parameter parameter : parameterArray) {
            paramClass = parameter.getType();
            paramName = parameter.getName();
            if (Collection.class.isAssignableFrom(paramClass)) {
                this.logger.error("{}:{}:{} unsupport Collection parameter", currController.getClass().getName(), currMethod.getName(), paramName);
                throw new RuntimeException(paramName + " unsupport Collection parameter");
            } else if (paramClass.isArray()) {
                this.logger.error("{}:{}:{} unsupport Array parameter", currController.getClass().getName(), currMethod.getName(), paramName);
                throw new RuntimeException(paramName + " unsupport Array parameter");
            }
        }
        //业务方法执行
        RouteHandler routeHandler = new ControlHandlerImpl(route, controller, method);
        //参数验证
        routeHandler = this.createRequestValidationHandlerImpl(routeHandler, method);
        //
        Router router = new Router(routeHandler);
        return router;
    }

    private RouteHandler createRequestValidationHandlerImpl(RouteHandler routeHandler, Method method)
    {
        Parameter[] parameterArray = method.getParameters();
        Class<?> paramClass;
        Map<String, ValidationHandler> allMap = new HashMap();
        Map<String, ValidationHandler> map;
        //校验-输入对象和输出对象只能是
        for (Parameter parameter : parameterArray) {
            paramClass = parameter.getType();
            this.currClassLinkList.clear();
            this.currParameter = parameter;
            map = this.createValidationHandlerMap("", paramClass);
            allMap.putAll(map);

        }
        routeHandler = new RequestValidationHandlerImpl(routeHandler, allMap);
        return routeHandler;
    }

    private Map<String, ValidationHandler> createValidationHandlerMap(String parentName, Class<?> paramClass)
    {
        if (this.currClassLinkList.contains(paramClass)) {
            //参数递归,阻止
            this.logger.error("{}:{}:{} unsupport RquestParam for-loop", this.currController.getClass().getName(), this.currMethod.getName(), this.currParameter.getType().getName());
            throw new RuntimeException(this.currParameter.getType().getName() + " unsupport RquestParam for-loop");
        }
        this.currClassLinkList.add(paramClass);
        Map<String, ValidationHandler> validationHandlerMap = new HashMap();
        String paramName;
        String key;
        Class<?> type;
        ValidationHandler validationHandler;
        Field[] fields = paramClass.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) == false && Modifier.isFinal(field.getModifiers()) == false) {
                if (field.isAnnotationPresent(RequestParam.class)) {
                    if (parentName.isEmpty()) {
                        paramName = field.getName();
                    } else {
                        paramName = parentName + "." + field.getName();
                    }
                    key = field.getName();
                    type = field.getType();
                    validationHandler = this.createValidationHandler(type, key, paramName, field);
                    validationHandlerMap.put(key, validationHandler);
                }
            }
        }
        return validationHandlerMap;
    }

    private ValidationHandler createValidationHandler(Class<?> type, String key, String paramName, Field field)
    {
        ValidationHandler validationHandler;
        RequestParam requestParam = field.getAnnotation(RequestParam.class);
        if (type == boolean.class || type == Boolean.class) {
            validationHandler = new BooleanHandlerImpl(key, paramName);
        } else if (type == long.class || int.class == type || type == Integer.class || type == Long.class) {
            validationHandler = new IntegerHandlerImpl(key, paramName, requestParam.max(), requestParam.min());
        } else if (type == double.class || type == Double.class) {
            validationHandler = new DoubleHandlerImpl(key, paramName, requestParam.max(), requestParam.min());
        } else if (type == String.class) {
            if (requestParam.regexp().isEmpty()) {
                validationHandler = new StringHandlerImpl(key, paramName, requestParam.max(), requestParam.min());
            } else {
                validationHandler = new RegexHandlerImpl(key, paramName, requestParam.regexp());
            }
        } else if (type.isArray()) {
            //数组
            Class<?> componentType = type.getComponentType();
            validationHandler = this.createValidationHandler(componentType, key, paramName, field);
            validationHandler = new ArrayHandlerImpl(validationHandler);
        } else if (Collection.class.isAssignableFrom(type)) {
            //集合泛型
            Type generictype = field.getGenericType();
            ParameterizedType listGenericType = (ParameterizedType) generictype;
            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
            Class<?> subType = (Class<?>) listActualTypeArguments[0];
            validationHandler = this.createValidationHandler(subType, key, paramName, field);
            validationHandler = new ArrayHandlerImpl(validationHandler);
        } else {
            //对象
            Map<String, ValidationHandler> subValidationHandlerMap = this.createValidationHandlerMap(paramName, type);
            validationHandler = new ObjectHandlerImpl(key, paramName, subValidationHandlerMap);
        }
        //是否需要非空判断
        if (requestParam.notNull()) {
            validationHandler = new NotNullHandlerImpl(validationHandler);
        }
        return validationHandler;
    }

    public Router get(String route)
    {
        BeanContext beanContext = AppContext.CONTEXT.getBeanContext();
        return beanContext.get(this.name, route);
    }

}
