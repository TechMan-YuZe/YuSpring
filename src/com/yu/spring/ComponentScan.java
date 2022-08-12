package com.yu.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 运行时
@Target(ElementType.TYPE) // 标在类上
public @interface ComponentScan {
    // 包路径
    String value() default "";
}