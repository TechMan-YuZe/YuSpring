package com.yu.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 运行时
@Target(ElementType.FIELD) // 标在属性上
public @interface Autowired {
    // 包路径
    String value() default "";
}