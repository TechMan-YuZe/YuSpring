package com.yu.service;

import com.yu.spring.BeanPostProcessor;
import com.yu.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class YuBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(String beanName, Object bean) {
        System.out.println("进入 YuBeanPostProcessor 的 postProcessBeforeInitialization 方法");

        // 这里可以做很多扩展，比如判断当前 Bean 的类型是否满足某些条件才做扩展
        if (bean instanceof UserService) {
            System.out.println("对 " + beanName + " 对象进行初始化 前 扩展");
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object bean) {
        System.out.println("进入 YuBeanPostProcessor 的 postProcessAfterInitialization 方法");

        // 这里可以做很多扩展，比如判断当前 Bean 的类型是否满足某些条件才做扩展（AOP 就是在这里实现）
        if (bean instanceof UserService) {
            System.out.println("对 " + beanName + " 对象进行初始化 后 扩展");

            // 开始 AOP
            Object proxyInstance = Proxy.newProxyInstance(YuBeanPostProcessor.class.getClassLoader(), UserService.class.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println(method.getName() + " 方法执行前");
                    // 执行被代理对象的方法
                    Object res = method.invoke(bean, args);
                    System.out.println(method.getName() + " 方法执行后");
                    return res;
                }
            });

            return proxyInstance;
        }
        return bean;
    }
}