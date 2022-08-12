package com.yu.service;

import com.yu.spring.ApplicationContext;

public class Test {
    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext(AppConfig.class);
        UserInterface userService = (UserInterface) context.getBean("userService");
        userService.print();
    }
}
