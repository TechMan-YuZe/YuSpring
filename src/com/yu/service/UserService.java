package com.yu.service;

import com.yu.spring.*;

@Component(name = "userService")
@Scope
public class UserService implements BeanNameAware, InitializingBean, UserInterface {

    @Autowired
    private OrderService orderService;

    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        System.out.println("执行 BeanNameAware 接口的 setBeanName 方法");
        // Aware 机制进行 beanName 赋值
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("执行 InitializingBean 接口的 afterPropertiesSet 方法");
        // 扩展点，随便做什么操作
    }

    @Override
    public String print() {
        System.out.println(orderService);
        return "print success";
    }
}
