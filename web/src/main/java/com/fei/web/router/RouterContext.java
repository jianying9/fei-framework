package com.fei.web.router;

import com.fei.framework.bean.BeanContext;
import com.fei.framework.context.AppContext;
import com.fei.framework.utils.ToolUtil;
import com.fei.web.router.handler.ControlHandlerImpl;
import com.fei.web.router.handler.RequestValidationHandlerImpl;
import com.fei.web.router.handler.RouteHandler;
import com.fei.web.router.validation.ArrayHandlerImpl;
import com.fei.web.router.validation.BooleanHandlerImpl;
import com.fei.web.router.validation.NumberHandlerImpl;
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
import com.fei.web.router.handler.AuthHandlerImpl;
import com.fei.web.router.validation.DateHandlerImpl;
import java.util.Date;

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

    public void add(String route, Object controller, Method method, boolean auth)
    {
        Router router = this.create(route, controller, method, auth);
        //注册到bean
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        beanContext.add(this.name, router.getRoute(), router);
    }

    /**
     * 验证返回值的类型是否支持
     *
     * @param type
     */
    private void checkReturnSupport(Class<?> type)
    {
        if (type != Void.class) {
            if (ToolUtil.isBasicType(type) || type.isArray() || Collection.class.isAssignableFrom(type) || type.getPackageName().startsWith("java.")) {
                this.logger.error("{}:{} unsupport returnType {}", currController.getClass().getName(), currMethod.getName(), type.getName());
                throw new RuntimeException(currMethod.getName() + " unsupport returnType" + type.getName());
            }
        }
    }

    private Router create(String route, Object controller, Method method, boolean auth)
    {
        this.currController = controller;
        this.currMethod = method;
        //验证返回值类型是否支持
        Class<?> returnType = method.getReturnType();
        this.checkReturnSupport(returnType);
        //业务方法执行
        RouteHandler routeHandler = new ControlHandlerImpl(route, controller, method);
        //参数验证
        routeHandler = this.createRequestValidationHandlerImpl(routeHandler, method);
        //用户验证
        if (auth) {
            routeHandler = new AuthHandlerImpl(routeHandler);
        }
        //
        Router router = new Router(routeHandler);
        return router;
    }

    private RouteHandler createRequestValidationHandlerImpl(RouteHandler routeHandler, Method method)
    {
        Parameter[] parameterArray = method.getParameters();
        Class<?> paramClass;
        Map<String, ValidationHandler> allMap = new HashMap();
        //校验-输入对象和输出对象只能是
        for (Parameter parameter : parameterArray) {
            paramClass = parameter.getType();
            this.currClassLinkList.clear();
            this.currParameter = parameter;
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                if (ToolUtil.isBasicType(paramClass)) {
                    //参数为基础类型
                    ValidationHandler validationHandler = this.createBasicValidationHandler(paramClass, parameter.getName(), parameter.getName(), parameter.getAnnotation(RequestParam.class));
                    allMap.put(validationHandler.getKey(), validationHandler);
                } else if (Collection.class.isAssignableFrom(paramClass)) {
                    //参数为集合类型
                    Type generictype = parameter.getParameterizedType();
                    ParameterizedType listGenericType = (ParameterizedType) generictype;
                    Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                    Class<?> subType = (Class<?>) listActualTypeArguments[0];
                    ValidationHandler validationHandler = this.createCollectionValidationHandler(subType, parameter.getName(), parameter.getName(), parameter.getAnnotation(RequestParam.class));
                    allMap.put(validationHandler.getKey(), validationHandler);
                } else if (paramClass.isArray()) {
                    //数组
                    Class<?> componentType = paramClass.getComponentType();
                    ValidationHandler validationHandler = this.createCollectionValidationHandler(componentType, parameter.getName(), parameter.getName(), parameter.getAnnotation(RequestParam.class));
                    allMap.put(validationHandler.getKey(), validationHandler);
                } else {
                    //对象类型
                    Map<String, ValidationHandler> map = this.createObjectValidationHandlerMap("", paramClass);
                    allMap.putAll(map);
                }
            } else {
                //对象类型
                Map<String, ValidationHandler> map = this.createObjectValidationHandlerMap("", paramClass);
                allMap.putAll(map);
            }
        }
        routeHandler = new RequestValidationHandlerImpl(routeHandler, allMap);
        return routeHandler;
    }

    /**
     * 方法基础类型参数验证
     *
     * @param parameter
     * @return
     */
    private ValidationHandler createBasicValidationHandler(Class<?> type, String key, String name, RequestParam requestParam)
    {
        ValidationHandler validationHandler;
        if (type == boolean.class || type == Boolean.class) {
            validationHandler = new BooleanHandlerImpl(key, name);
        } else if (type == long.class || type == Long.class || int.class == type || type == Integer.class || short.class == type || type == Short.class) {
            validationHandler = new IntegerHandlerImpl(key, name, requestParam.max(), requestParam.min());
        } else if (type == double.class || type == Double.class || type == float.class || type == Float.class || Number.class.isAssignableFrom(type)) {
            validationHandler = new NumberHandlerImpl(key, name, requestParam.max(), requestParam.min());
        } else if (type == String.class) {
            if (requestParam.regexp().isEmpty()) {
                validationHandler = new StringHandlerImpl(key, name, requestParam.max(), requestParam.min());
            } else {
                validationHandler = new RegexHandlerImpl(key, name, requestParam.regexp());
            }
        } else if (type == Date.class) {
            validationHandler = new DateHandlerImpl(key, name);
        } else {
            //不支持类型
            this.logger.error("{}:{}:{} unsupport parameterType {}", currController.getClass().getName(), currMethod.getName(), this.currParameter.getName(), type.getName());
            throw new RuntimeException(this.currParameter.getName() + " unsupport parameterType" + type.getName());
        }
        //是否需要非空判断
        if (requestParam.required()) {
            validationHandler = new NotNullHandlerImpl(validationHandler);
        }
        return validationHandler;
    }

    /**
     * 参数为集合类型
     *
     * @param parameter
     * @return
     */
    private ValidationHandler createCollectionValidationHandler(Class<?> type, String key, String name, RequestParam requestParam)
    {
        ValidationHandler validationHandler;
        if (ToolUtil.isBasicType(type)) {
            //参数为原始类型
            validationHandler = this.createBasicValidationHandler(type, key, name, requestParam);
        } else {
            //对象类型
            Map<String, ValidationHandler> subValidationHandlerMap = this.createObjectValidationHandlerMap(name, type);
            validationHandler = new ObjectHandlerImpl(key, name, subValidationHandlerMap);
        }
        validationHandler = new ArrayHandlerImpl(validationHandler);
        //是否需要非空判断
        if (requestParam.required()) {
            validationHandler = new NotNullHandlerImpl(validationHandler);
        }
        return validationHandler;
    }

    private Map<String, ValidationHandler> createObjectValidationHandlerMap(String parentName, Class<?> paramClass)
    {
        Map<String, ValidationHandler> validationHandlerMap = new HashMap();
        //
        if (this.currClassLinkList.contains(paramClass) == false) {
            String paramName;
            Class<?> type;
            Field[] fields = paramClass.getDeclaredFields();
            ValidationHandler validationHandler;
            RequestParam requestParam;
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) == false && Modifier.isFinal(field.getModifiers()) == false) {
                    if (field.isAnnotationPresent(RequestParam.class)) {
                        requestParam = field.getAnnotation(RequestParam.class);
                        if (parentName.isEmpty()) {
                            paramName = field.getName();
                        } else {
                            paramName = parentName + "." + field.getName();
                        }
                        type = field.getType();
                        if (ToolUtil.isBasicType(type)) {
                            //参数为基础类型
                            validationHandler = this.createBasicValidationHandler(type, field.getName(), paramName, requestParam);
                        } else if (Collection.class.isAssignableFrom(paramClass)) {
                            //参数为集合类型
                            Type generictype = field.getGenericType();
                            ParameterizedType listGenericType = (ParameterizedType) generictype;
                            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                            Class<?> subType = (Class<?>) listActualTypeArguments[0];
                            validationHandler = this.createCollectionValidationHandler(subType, field.getName(), paramName, requestParam);
                        } else if (paramClass.isArray()) {
                            //数组
                            Class<?> componentType = paramClass.getComponentType();
                            validationHandler = this.createCollectionValidationHandler(componentType, field.getName(), paramName, requestParam);
                        } else {
                            //对象
                            Map<String, ValidationHandler> subValidationHandlerMap = this.createObjectValidationHandlerMap(paramName, type);
                            validationHandler = new ObjectHandlerImpl(field.getName(), paramName, subValidationHandlerMap);
                            //是否需要非空判断
                            if (requestParam.required()) {
                                validationHandler = new NotNullHandlerImpl(validationHandler);
                            }
                        }
                        validationHandlerMap.put(validationHandler.getKey(), validationHandler);
                    }
                }
            }
            this.currClassLinkList.add(paramClass);
        }
        return validationHandlerMap;
    }

    public Router get(String route)
    {
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        return beanContext.get(this.name, route);
    }

}
