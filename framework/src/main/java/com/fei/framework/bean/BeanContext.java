package com.fei.framework.bean;

import com.fei.framework.util.ClassUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jianying9
 */
public class BeanContext
{

    private final Logger logger = LogManager.getLogger(BeanContext.class);

    private final Map<String, Map<Class<?>, Object>> beanGroupMap = new HashMap();

    public synchronized void add(String group, Object bean)
    {
        Map<Class<?>, Object> beanMap = beanGroupMap.get(group);
        if (beanMap == null) {
            beanMap = new HashMap();
            beanGroupMap.put(group, beanMap);
        }
        beanMap.put(bean.getClass(), bean);
    }

    public Map<Class<?>, Object> getGroup(String group)
    {
        return beanGroupMap.get(group);
    }

    public <B extends Object> B get(String group, Class<?> clazz)
    {
        Object result = null;
        Map<Class<?>, Object> beanMap = beanGroupMap.get(group);
        if (beanMap != null) {
            result = beanMap.get(clazz);
        }
        return (B) result;
    }

    /**
     * 初始化所有资源
     */
    public void build()
    {
        Map<Class<?>, Object> allMap = new HashMap();
        for (Map<Class<?>, Object> beanMap : this.beanGroupMap.values()) {
            allMap.putAll(beanMap);
        }
        //自动注入
        for (Entry<String, Map<Class<?>, Object>> groupEntry : this.beanGroupMap.entrySet()) {
            for (Entry<Class<?>, Object> entry : groupEntry.getValue().entrySet()) {
                this.logger.info("autowired {} class:{}.", groupEntry.getKey(), entry.getKey().getName());
                this.autowired(entry.getValue(), allMap);
            }
        }
        //初始化
        for (Entry<String, Map<Class<?>, Object>> groupEntry : this.beanGroupMap.entrySet()) {
            for (Entry<Class<?>, Object> entry : groupEntry.getValue().entrySet()) {
                this.logger.info("init {} class:{}.", groupEntry.getKey(), entry.getKey().getName());
                this.init(entry.getValue());
            }
        }
    }

    /**
     * 注入
     *
     * @param bean
     * @param allMap
     */
    private void autowired(Object bean, Map<Class<?>, Object> allMap)
    {
        //递归注入
        this.autowired(bean, bean.getClass(), allMap);
    }

    /**
     * 递归注入
     *
     * @param bean
     * @param superClass
     */
    private void autowired(Object bean, Class<?> superClass, Map<Class<?>, Object> allMap)
    {
        Field[] fileds = superClass.getDeclaredFields();
        Class<?> key;
        Object value;
        for (Field field : fileds) {
            //只有非静态字段和非final字段才会注入
            if (Modifier.isStatic(field.getModifiers()) == false && Modifier.isFinal(field.getModifiers()) == false) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    key = this.getKey(field);
                    value = allMap.get(key);
                    if (value == null) {
                        this.logger.error("autowired error. Cause: can not find bean by class {}", key.getName());
                        throw new RuntimeException("autowired rrror");
                    } else {
                        field.setAccessible(true);
                        try {
                            field.set(bean, value);
                        } catch (IllegalArgumentException | IllegalAccessException ex) {
                            this.logger.error("autowired error {}", field.getName(), ex);
                            throw new RuntimeException("autowired rrror");
                        }
                    }
                }
            }
        }
        if (superClass.getSuperclass().equals(Object.class) == false) {
            //父类存在,为父类注入
            this.autowired(bean, superClass.getSuperclass(), allMap);
        }
    }

    /**
     * 根据field的类型获取key,优先取类型的第一个泛型,如果不是泛型,则直接取类型
     *
     * @param field
     * @return
     */
    private Class<?> getKey(Field field)
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

    /**
     * 初始化
     *
     * @param bean
     */
    private void init(Object bean)
    {
        this.init(bean, bean.getClass());
    }

    /**
     * 递归父类初始化
     *
     * @param bean
     */
    private void init(Object bean, Class<?> superClass)
    {
        Class<?> clazz = bean.getClass();
        try {

            Method method = ClassUtil.getMethodByName(clazz, "init");
            if (method != null) {
                //自身存在init方法
                method.setAccessible(true);
                method.invoke(bean);
            } else {
                //自身不存在,递归父类
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
