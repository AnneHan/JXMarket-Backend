package com.hyl.api.util.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 自定义注解 实现属性值不为空
 * @author AnneHan
 * @date 2023-09-15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CheckNotNull {

    String message() default "";
    boolean required() default true;
}
