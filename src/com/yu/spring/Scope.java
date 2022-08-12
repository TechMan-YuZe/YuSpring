package com.yu.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bean 的作用域注解
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时
@Target(ElementType.TYPE) // 标在类上
public @interface Scope {
    // 作用域
    String value() default "singleton";
}