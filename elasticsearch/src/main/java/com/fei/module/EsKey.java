package com.fei.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于描述entity的主键
 *
 * @author jianying9
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface EsKey
{

    /**
     * 描述
     *
     * @return
     */
    public String desc();

    /**
     * 新增时,如果主键不存在自动生成主键
     *
     * @return
     */
    public boolean auto() default false;
}
