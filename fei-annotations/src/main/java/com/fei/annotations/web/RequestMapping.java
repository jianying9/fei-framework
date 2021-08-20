package com.fei.annotations.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口
 *
 * @author jianying9
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RequestMapping
{

    /**
     * 路径
     *
     * @return
     */
    public String value();

    /**
     * 用户验证，在controller的auth=false时,该设置有效
     *
     * @return
     */
    public boolean auth() default false;

    /**
     * 描述
     *
     * @return
     */
    public String description();
}
