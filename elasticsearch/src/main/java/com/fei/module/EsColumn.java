package com.fei.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于描述entity中各个field的信息
 *
 * @author jianying9
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface EsColumn
{

    /**
     * 是否主键,唯一
     *
     * @return
     */
    public boolean key() default false;

    /**
     * 数据为空时，取默认值
     *
     * @return
     */
    public String defaultValue() default "";

    /**
     * 描述
     *
     * @return
     */
    public String desc();

    /**
     * 是否分词
     *
     * @return
     */
    public boolean analyzer() default false;
}
