package com.fei.web.router;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fei.app.bean.BeanContext;
import com.fei.app.context.AppContext;
import com.fei.app.utils.ToolUtil;
import com.fei.web.request.validation.ArrayValidationImpl;
import com.fei.web.request.validation.BooleanValidationImpl;
import com.fei.web.request.validation.NumberValidationImpl;
import com.fei.web.request.validation.IntegerValidationImpl;
import com.fei.web.request.validation.RequiredValidationImpl;
import com.fei.web.request.validation.ObjectValidationImpl;
import com.fei.web.request.validation.RegexValidationImpl;
import com.fei.web.request.validation.StringValidationImpl;
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
import com.fei.annotations.web.RequestParam;
import com.fei.annotations.web.ResponseParam;
import com.fei.web.request.validation.DateValidationImpl;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fei.web.request.validation.ParamValidation;
import com.fei.web.response.filter.ArrayFilterImpl;
import com.fei.web.response.filter.BasicFilterImpl;
import com.fei.web.response.filter.ObjectFilterImpl;
import com.fei.web.response.filter.ParamFilter;
import java.util.HashSet;
import java.util.Set;

/**
 * 路由对象上下文
 *
 * @author jianying9
 */
public class RouterContext
{

    private final String name = "router";

    private final Logger logger = LoggerFactory.getLogger(RouterContext.class);

    public final static RouterContext INSTANCE = new RouterContext();

    private final List<Class<?>> currClassLinkList = new ArrayList();

    private Object currController = null;

    private Method currMethod = null;

    public void add(String route, String group, String description, Object controller, Method method, boolean auth)
    {
        Router router = this.create(route, group, description, controller, method, auth);
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
        if (type != void.class) {
            if (ToolUtil.isBasicType(type) || type.isArray() || Collection.class.isAssignableFrom(type) || type.getPackageName().startsWith("java.")) {
                this.logger.error("{}:{} unsupport returnType {}", currController.getClass().getName(), currMethod.getName(), type.getName());
                throw new RuntimeException(currMethod.getName() + " unsupport returnType" + type.getName());
            }
        }
    }

    private Router create(String route, String group, String description, Object controller, Method method, boolean auth)
    {
        this.currController = controller;
        this.currMethod = method;
        //验证返回值类型是否支持
        Class<?> returnType = method.getReturnType();
        this.checkReturnSupport(returnType);
        //业务方法执行
        RouteHandler routeHandler = new ControlHandlerImpl(route, controller, method);
        //返回数据过滤
        ResponseFilterHandlerImpl responseFilterHandlerImpl = this.createResponseFilterImpl(routeHandler, returnType);
        routeHandler = responseFilterHandlerImpl;
        //参数验证
        RequestValidationHandlerImpl requestValidationHandlerImpl = this.createRequestValidationImpl(routeHandler, method);
        routeHandler = requestValidationHandlerImpl;
        //用户验证
        if (auth) {
            routeHandler = new AuthHandlerImpl(routeHandler);
        }
        //
        Router router = new Router(routeHandler, requestValidationHandlerImpl.getParamValidationMap(), responseFilterHandlerImpl.getParamFilterMap(), auth, group, description);
        return router;
    }

    private RequestValidationHandlerImpl createRequestValidationImpl(RouteHandler routeHandler, Method method)
    {
        Parameter[] parameterArray = method.getParameters();
        Class<?> paramClass;
        Map<String, ParamValidation> allMap = new HashMap();
        for (Parameter parameter : parameterArray) {
            paramClass = parameter.getType();
            this.currClassLinkList.clear();
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                if (ToolUtil.isBasicType(paramClass)) {
                    //参数为基础类型
                    ParamValidation paramValidation = this.createBasicValidation(paramClass, parameter.getName(), parameter.getName(), parameter.getAnnotation(RequestParam.class));
                    allMap.put(paramValidation.getKey(), paramValidation);
                } else if (Collection.class.isAssignableFrom(paramClass)) {
                    //参数为集合类型
                    Type generictype = parameter.getParameterizedType();
                    ParameterizedType listGenericType = (ParameterizedType) generictype;
                    Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                    Class<?> subType = (Class<?>) listActualTypeArguments[0];
                    ParamValidation paramValidation = this.createCollectionValidation(subType, parameter.getName(), parameter.getName(), parameter.getAnnotation(RequestParam.class));
                    allMap.put(paramValidation.getKey(), paramValidation);
                } else if (paramClass.isArray()) {
                    //数组
                    Class<?> componentType = paramClass.getComponentType();
                    ParamValidation paramValidation = this.createCollectionValidation(componentType, parameter.getName(), parameter.getName(), parameter.getAnnotation(RequestParam.class));
                    allMap.put(paramValidation.getKey(), paramValidation);
                } else {
                    //对象类型
                    Map<String, ParamValidation> map = this.createObjectValidationMap("", paramClass);
                    allMap.putAll(map);
                }
            } else {
                //对象类型
                Map<String, ParamValidation> map = this.createObjectValidationMap("", paramClass);
                allMap.putAll(map);
            }
        }
        return new RequestValidationHandlerImpl(routeHandler, allMap);
    }

    /**
     * 方法基础类型参数验证
     *
     * @param parameter
     * @return
     */
    private ParamValidation createBasicValidation(Class<?> type, String key, String name, RequestParam requestParam)
    {
        ParamValidation paramValidation;
        if (type == boolean.class || type == Boolean.class) {
            paramValidation = new BooleanValidationImpl(key, name, requestParam.description());
        } else if (type == long.class || type == Long.class || int.class == type || type == Integer.class || short.class == type || type == Short.class) {
            paramValidation = new IntegerValidationImpl(key, name, requestParam.max(), requestParam.min(), requestParam.description());
        } else if (type == double.class || type == Double.class || type == float.class || type == Float.class || Number.class.isAssignableFrom(type)) {
            paramValidation = new NumberValidationImpl(key, name, requestParam.max(), requestParam.min(), requestParam.description());
        } else if (type == String.class) {
            if (requestParam.regexp().isEmpty()) {
                paramValidation = new StringValidationImpl(key, name, requestParam.max(), requestParam.min(), requestParam.description());
            } else {
                paramValidation = new RegexValidationImpl(key, name, requestParam.regexp(), requestParam.description());
            }
        } else if (type == Date.class) {
            paramValidation = new DateValidationImpl(key, name, requestParam.description());
        } else {
            //不支持类型
            this.logger.error("{}:{}:{} unsupport parameterType {}", currController.getClass().getName(), currMethod.getName(), name, type.getName());
            throw new RuntimeException(name + " unsupport parameterType" + type.getName());
        }
        //是否需要非空判断
        if (requestParam.required()) {
            paramValidation = new RequiredValidationImpl(paramValidation);
        }
        return paramValidation;
    }

    /**
     * 参数为集合类型
     *
     * @param parameter
     * @return
     */
    private ParamValidation createCollectionValidation(Class<?> type, String key, String name, RequestParam requestParam)
    {
        ParamValidation paramValidation;
        if (ToolUtil.isBasicType(type)) {
            //参数为原始类型
            paramValidation = this.createBasicValidation(type, key, name, requestParam);
        } else {
            //对象类型
            Map<String, ParamValidation> subValidationMap = this.createObjectValidationMap(name, type);
            paramValidation = new ObjectValidationImpl(key, name, subValidationMap, requestParam.description());
        }
        paramValidation = new ArrayValidationImpl(paramValidation);
        //是否需要非空判断
        if (requestParam.required()) {
            paramValidation = new RequiredValidationImpl(paramValidation);
        }
        return paramValidation;
    }

    private Map<String, ParamValidation> createObjectValidationMap(String parentName, Class<?> paramClass)
    {
        Map<String, ParamValidation> paramValidationMap = new HashMap();
        //
        if (this.currClassLinkList.contains(paramClass) == false) {
            String paramName;
            Class<?> type;
            Field[] fields = paramClass.getDeclaredFields();
            ParamValidation paramValidation;
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
                            paramValidation = this.createBasicValidation(type, field.getName(), paramName, requestParam);
                        } else if (Collection.class.isAssignableFrom(type)) {
                            //参数为集合类型
                            Type generictype = field.getGenericType();
                            ParameterizedType listGenericType = (ParameterizedType) generictype;
                            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                            Class<?> subType = (Class<?>) listActualTypeArguments[0];
                            paramValidation = this.createCollectionValidation(subType, field.getName(), paramName, requestParam);
                        } else if (type.isArray()) {
                            //数组
                            Class<?> componentType = type.getComponentType();
                            paramValidation = this.createCollectionValidation(componentType, field.getName(), paramName, requestParam);
                        } else {
                            //对象
                            Map<String, ParamValidation> subValidationMap = this.createObjectValidationMap(paramName, type);
                            paramValidation = new ObjectValidationImpl(field.getName(), paramName, subValidationMap, requestParam.description());
                            //是否需要非空判断
                            if (requestParam.required()) {
                                paramValidation = new RequiredValidationImpl(paramValidation);
                            }
                        }
                        paramValidationMap.put(paramValidation.getKey(), paramValidation);
                    }
                }
            }
            this.currClassLinkList.add(paramClass);
        }
        return paramValidationMap;
    }

    private ResponseFilterHandlerImpl createResponseFilterImpl(RouteHandler routeHandler, Class<?> returnType)
    {
        this.currClassLinkList.clear();
        Map<String, ParamFilter> paramFilterMap = new HashMap();
        //
        String paramName;
        Class<?> type;
        Field[] fields = returnType.getDeclaredFields();
        ParamFilter paramFilter;
        ResponseParam responseParam;
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) == false && Modifier.isFinal(field.getModifiers()) == false) {
                if (field.isAnnotationPresent(ResponseParam.class)) {
                    responseParam = field.getAnnotation(ResponseParam.class);
                    paramName = field.getName();
                    type = field.getType();
                    if (ToolUtil.isBasicType(type)) {
                        //参数为基础类型
                        paramFilter = this.createBasicFilter(type, field.getName(), paramName, responseParam);
                    } else if (Collection.class.isAssignableFrom(type)) {
                        //参数为集合类型
                        Type generictype = field.getGenericType();
                        ParameterizedType listGenericType = (ParameterizedType) generictype;
                        Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                        Class<?> subType = (Class<?>) listActualTypeArguments[0];
                        paramFilter = this.createCollectionFilter(subType, field.getName(), paramName, responseParam);
                    } else if (type.isArray()) {
                        //数组
                        Class<?> componentType = type.getComponentType();
                        paramFilter = this.createCollectionFilter(componentType, field.getName(), paramName, responseParam);
                    } else {
                        //对象
                        Map<String, ParamFilter> subFilterMap = this.createObjectFilterMap(paramName, type);
                        paramFilter = new ObjectFilterImpl(field.getName(), paramName, subFilterMap, responseParam.description());
                    }
                    paramFilterMap.put(paramFilter.getKey(), paramFilter);
                }
            }
        }
        return new ResponseFilterHandlerImpl(routeHandler, paramFilterMap);
    }

    private ParamFilter createBasicFilter(Class<?> type, String key, String name, ResponseParam responseParam)
    {
        ParamFilter paramFilter;
        if (type == boolean.class || type == Boolean.class) {
            paramFilter = new BasicFilterImpl(key, name, "boolean", responseParam.description());
        } else if (type == long.class || type == Long.class || int.class == type || type == Integer.class || short.class == type || type == Short.class) {
            paramFilter = new BasicFilterImpl(key, name, "integer", responseParam.description());
        } else if (type == double.class || type == Double.class || type == float.class || type == Float.class || Number.class.isAssignableFrom(type)) {
            paramFilter = new BasicFilterImpl(key, name, "number", responseParam.description());
        } else if (type == String.class) {
            paramFilter = new BasicFilterImpl(key, name, "string", responseParam.description());
        } else if (type == Date.class) {
            paramFilter = new BasicFilterImpl(key, name, "date", responseParam.description());
        } else {
            //不支持类型
            this.logger.error("{}:{}:{} unsupport responseType {}", currController.getClass().getName(), currMethod.getName(), name, type.getName());
            throw new RuntimeException(this.currMethod.getName() + "unsupport responseType" + type.getName());
        }
        return paramFilter;
    }

    private ParamFilter createCollectionFilter(Class<?> type, String key, String name, ResponseParam responseParam)
    {
        ParamFilter paramFilter;
        if (ToolUtil.isBasicType(type)) {
            //参数为原始类型
            paramFilter = this.createBasicFilter(type, key, name, responseParam);
        } else {
            //对象类型
            Map<String, ParamFilter> subFilterMap = this.createObjectFilterMap(name, type);
            paramFilter = new ObjectFilterImpl(key, name, subFilterMap, responseParam.description());
        }
        paramFilter = new ArrayFilterImpl(paramFilter);
        return paramFilter;
    }

    private Map<String, ParamFilter> createObjectFilterMap(String parentName, Class<?> paramClass)
    {
        Map<String, ParamFilter> paramFilterMap = new HashMap();
        //
        if (this.currClassLinkList.contains(paramClass) == false) {
            String paramName;
            Class<?> type;
            Field[] fields = paramClass.getDeclaredFields();
            ParamFilter paramFilter;
            ResponseParam responseParam;
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) == false && Modifier.isFinal(field.getModifiers()) == false) {
                    if (field.isAnnotationPresent(ResponseParam.class)) {
                        responseParam = field.getAnnotation(ResponseParam.class);
                        if (parentName.isEmpty()) {
                            paramName = field.getName();
                        } else {
                            paramName = parentName + "." + field.getName();
                        }
                        type = field.getType();
                        if (ToolUtil.isBasicType(type)) {
                            //参数为基础类型
                            paramFilter = this.createBasicFilter(type, field.getName(), paramName, responseParam);
                        } else if (Collection.class.isAssignableFrom(type)) {
                            //参数为集合类型
                            Type generictype = field.getGenericType();
                            ParameterizedType listGenericType = (ParameterizedType) generictype;
                            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                            Class<?> subType = (Class<?>) listActualTypeArguments[0];
                            paramFilter = this.createCollectionFilter(subType, field.getName(), paramName, responseParam);
                        } else if (type.isArray()) {
                            //数组
                            Class<?> componentType = paramClass.getComponentType();
                            paramFilter = this.createCollectionFilter(componentType, field.getName(), paramName, responseParam);
                        } else {
                            //对象
                            Map<String, ParamFilter> subFilterMap = this.createObjectFilterMap(paramName, type);
                            paramFilter = new ObjectFilterImpl(field.getName(), paramName, subFilterMap, responseParam.description());
                        }
                        paramFilterMap.put(paramFilter.getKey(), paramFilter);
                    }
                }
            }
            this.currClassLinkList.add(paramClass);
        }
        return paramFilterMap;
    }

    public Router get(String route)
    {
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        return beanContext.get(this.name, route);
    }

    public JSONObject getApi()
    {
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        Map<String, Object> routerMap = beanContext.get(name);
        JSONArray routerArray = new JSONArray();
        Set<String> groupSet = new HashSet();
        Router router;
        JSONObject routerApi;
        String group;
        for (Object obj : routerMap.values()) {
            router = (Router) obj;
            routerApi = router.getApi();
            routerArray.add(routerApi);
            //
            group = routerApi.getString("group");
            if (group == null || group.isEmpty()) {
                group = "unknown";
            }
            groupSet.add(group);
        }
        //
        JSONArray groupArray = new JSONArray();
        groupArray.addAll(groupSet);
        //
        JSONObject output = new JSONObject();
        output.put("routerArray", routerArray);
        output.put("groupArray", groupArray);
        return output;
    }

}
