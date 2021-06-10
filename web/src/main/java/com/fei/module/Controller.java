package com.fei.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口
 *
 * @author jianying9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller
{

    /**
     * 路径
     *
     * @return
     */
    public String value();

    /**
     * 名称
     *
     * @return
     */
    public String name();

    /**
     * 用户验证,如果为true,则该controller下所有的接口都需要用户验证
     *
     * @return
     */
    public boolean auth() default false;

}
