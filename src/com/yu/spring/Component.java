package com.yu.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表明这是一个 Bean
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时
@Target(ElementType.TYPE) // 标在类上
public @interface Component {
    /**
     * 标识 Bean 的名字
     * @return
     */
    String name() default "";
}