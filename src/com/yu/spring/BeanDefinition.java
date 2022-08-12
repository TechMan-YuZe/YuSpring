package com.yu.spring;

/**
 * Bean 的定义
 */
public class BeanDefinition {
    /**
     * Bean 的类型
     */
    private Class type;

    /**
     * Bean 的作用域
     */
    private String scope;

    public BeanDefinition() {}

    public BeanDefinition(Class type, String scope) {
        this.type = type;
        this.scope = scope;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
