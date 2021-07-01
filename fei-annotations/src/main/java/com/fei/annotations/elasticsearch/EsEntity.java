package com.fei.annotations.elasticsearch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * elasticsearch entity annotation
 *
 * @author jianying9
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface EsEntity
{

    /**
     * 虚拟表空间(es不存在表空间,会自动加在index的前缀.例如:database_user_entity),用于区分不同环境.默认使用EsConfig.DATABASE全局定义,可以指定表空间
     *
     * @return
     */
    public String database() default "";

    /**
     * index名称,默认为类名格式转换后结果.例如:UserEntity.class -> user_entity
     *
     * @return
     */
    public String index() default "";

}
