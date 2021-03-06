package com.fei.app.bean;

import com.fei.annotations.component.Resource;
import com.fei.app.utils.ClassUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jianying9
 */
public class BeanContext
{

    private final Logger logger = LoggerFactory.getLogger(BeanContext.class);

    private final Map<String, Map<String, Object>> beanGroupMap = new HashMap();

    public void add(String group, Object bean)
    {
        this.add(group, bean.getClass().getName(), bean);
    }

    public void add(String group, String key, Object bean)
    {
        Map<String, Object> beanMap = beanGroupMap.get(group);
        if (beanMap == null) {
            beanMap = new HashMap();
            beanGroupMap.put(group, beanMap);
        }
        if (beanMap.containsKey(key)) {
            Object existBean = beanMap.get(key);
            this.logger.error("repeated key:{},{}, existClass:{} currClass:{}.", group, key, existBean.getClass().getName(), bean.getClass().getName());
            throw new RuntimeException("repeated key:" + key);
        }
        beanMap.put(key, bean);
    }

    public <B extends Object> B get(String group, Class<?> clazz)
    {
        return this.get(group, clazz.getName());
    }

    public <B extends Object> B get(String group, String key)
    {
        Object result = null;
        Map<String, Object> beanMap = beanGroupMap.get(group);
        if (beanMap != null) {
            result = beanMap.get(key);
        }
        return (B) result;
    }

    public Map<String, Object> get(String group)
    {
        Map<String, Object> map = beanGroupMap.get(group);
        if (map == null) {
            map = Collections.EMPTY_MAP;
        }
        return map;
    }

    /**
     * ?????????????????????
     */
    public void build()
    {
        Map<String, Object> allMap = new HashMap();
        for (Map<String, Object> beanMap : this.beanGroupMap.values()) {
            allMap.putAll(beanMap);
        }
        //????????????
        for (Entry<String, Map<String, Object>> groupEntry : this.beanGroupMap.entrySet()) {
            for (Entry<String, Object> entry : groupEntry.getValue().entrySet()) {
                this.logger.info("autowired {} class:{}.", groupEntry.getKey(), entry.getKey());
                this.resource(entry.getValue(), allMap);
            }
        }
        //?????????
        for (Entry<String, Map<String, Object>> groupEntry : this.beanGroupMap.entrySet()) {
            for (Entry<String, Object> entry : groupEntry.getValue().entrySet()) {
                this.logger.info("init {} class:{}.", groupEntry.getKey(), entry.getKey());
                this.init(entry.getValue());
            }
        }
    }

    public void resource(Object obj)
    {
        Map<String, Object> allMap = new HashMap();
        for (Map<String, Object> beanMap : this.beanGroupMap.values()) {
            allMap.putAll(beanMap);
        }
        this.resource(obj, allMap);
    }

    /**
     * ??????
     *
     * @param obj
     * @param allMap
     */
    private void resource(Object obj, Map<String, Object> allMap)
    {
        //????????????
        this.resource(obj, obj.getClass(), allMap);
    }

    /**
     * ????????????
     *
     * @param obj
     * @param superClass
     */
    private void resource(Object obj, Class<?> superClass, Map<String, Object> allMap)
    {
        Field[] fileds = superClass.getDeclaredFields();
        String key;
        Object value;
        Resource autowired;
        for (Field field : fileds) {
            //???????????????????????????final??????????????????
            if (Modifier.isStatic(field.getModifiers()) == false && Modifier.isFinal(field.getModifiers()) == false) {
                if (field.isAnnotationPresent(Resource.class)) {
                    autowired = field.getAnnotation(Resource.class);
                    key = autowired.value();
                    if (key.isEmpty()) {
                        key = this.getKey(field);
                    }
                    value = allMap.get(key);
                    if (value == null) {
                        this.logger.error("autowired error. Cause: can not find bean by name {}", key);
                        throw new RuntimeException("autowired rrror");
                    } else {
                        field.setAccessible(true);
                        try {
                            field.set(obj, value);
                        } catch (IllegalArgumentException | IllegalAccessException ex) {
                            this.logger.error("autowired error {}", field.getName(), ex);
                            throw new RuntimeException("autowired rrror");
                        }
                    }
                }
            }
        }
        if (superClass.getSuperclass().equals(Object.class) == false) {
            //????????????,???????????????
            this.resource(obj, superClass.getSuperclass(), allMap);
        }
    }

    /**
     * ??????field???????????????key,?????????????????????????????????,??????????????????,??????????????????
     *
     * @param field
     * @return
     */
    private String getKey(Field field)
    {
        Class<?> key = this.getKeyClass(field);
        return key.getName();
    }

    private Class<?> getKeyClass(Field field)
    {
        Class<?> key;
        Type type = field.getGenericType();
        if (ParameterizedType.class.isInstance(type)) {
            ParameterizedType listGenericType = (ParameterizedType) type;
            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
            key = (Class<?>) listActualTypeArguments[0];
        } else {
            key = field.getType();
        }
        return key;
    }

    public Set<Class<?>> getDependency(Class<?> superClass)
    {
        Set<Class<?>> classSet = new HashSet();
        Field[] fileds = superClass.getDeclaredFields();
        Class<?> keyClass;
        String key;
        Resource autowired;
        for (Field field : fileds) {
            //???????????????????????????final??????????????????
            if (Modifier.isStatic(field.getModifiers()) == false && Modifier.isFinal(field.getModifiers()) == false) {
                if (field.isAnnotationPresent(Resource.class)) {
                    autowired = field.getAnnotation(Resource.class);
                    key = autowired.value();
                    if (key.isEmpty()) {
                        keyClass = this.getKeyClass(field);
                        classSet.add(keyClass);
                    }
                }
            }
        }
        superClass = superClass.getSuperclass();
        if (superClass != null && superClass.equals(Object.class) == false) {
            Set<Class<?>> superClassSet = this.getDependency(superClass.getSuperclass());
            classSet.addAll(superClassSet);
        }
        return classSet;
    }

    /**
     * ?????????
     *
     * @param obj
     */
    private void init(Object obj)
    {
        this.init(obj, obj.getClass());
    }

    /**
     * ?????????????????????
     *
     * @param bean
     */
    private void init(Object bean, Class<?> superClass)
    {
        Class<?> clazz = bean.getClass();
        try {

            Method method = ClassUtil.getMethodByName(clazz, "init");
            if (method != null && Modifier.isStatic(method.getModifiers()) == false) {
                //????????????init??????
                method.setAccessible(true);
                method.invoke(bean);
            } else {
                //???????????????,????????????
                if (superClass.getSuperclass().equals(Object.class) == false) {
                    this.init(bean, superClass.getSuperclass());
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            this.logger.error("init error {}", bean.getClass(), ex);
            throw new RuntimeException("init rrror");
        }
    }

}
