package com.yu.spring;

/**
 * Bean 初始化接口
 */
public interface InitializingBean {
    /**
     * 属性注入后回调
     */
    void afterPropertiesSet();
}