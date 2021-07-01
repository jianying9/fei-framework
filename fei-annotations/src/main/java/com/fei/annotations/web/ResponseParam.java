package com.fei.annotations.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 响应参数
 *
 * @author jianying9
 */
@Target(value = {ElementType.TYPE, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ResponseParam
{

    public String desc();

}
