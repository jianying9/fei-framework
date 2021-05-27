package com.fei.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数
 *
 * @author jianying9
 */
@Target(value = {ElementType.TYPE, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RequestParam
{

    public boolean notNull() default true;

    public long max() default Long.MAX_VALUE;

    public long min() default 0;

    public String regexp() default "";

    public String desc();

}