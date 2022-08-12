# 项目结构

![image-20220812173443669](https://typora-images-yuze.oss-cn-hangzhou.aliyuncs.com/images/202208121734768.png)

- service 包模拟业务场景
    - AppConfig：配置类，负责扫描 service 包中的 Bean
    - OrderService：订单业务
    - Test：测试 Spring 的功能
    - UserInterface：用户接口，用来测试 JDK 动态代理
    - UserService：用户业务
    - YuBeanPostProcessor：实现了 BeanPostProcessor 接口的类（扩展点）
- spring 包：spring 核心功能
    - Autowired：自动装配注解
        - value：设置 BeanName
    - BeanDefinition：Bean 的定义
        - type：Bean 的类型
        - scope：Bean 的作用域
    - BeanNameAware：BeanName 的 Aware 回调接口（扩展点）
        - setBeanName：设置 BeanName
    - BeanPostProcessor：Bean 的后置处理器接口（扩展点）
        - postProcessBeforeInitialization：初始化前回调
        - postProcessAfterInitialization：初始化后回调
    - Component：标注 Bean 的注解
        - value：BeanName
    - ComponentScan：包扫描注解，可配置包扫描路径
        - value：包路径
    - InitializingBean：Bean 的初始化接口（扩展点）
        - afterPropertiesSet：属性注入后回调
    - Scope
        - value：Bean 的作用域，默认是 singleton 单例
    - ApplicationContext：IoC 容器
        - configClass：解析哪个类
        - ConcurrentMap<String, BeanDefinition> beanDefinitionMap：beanDefinition 的集合
        - ConcurrentMap<String, Object> singletonObjects：单例池
        - ArrayList<BeanPostProcessor> beanPostProcessors：后置处理器列表
        - YuApplicationContext(Class configClass)：IoC 容器的构造方法，主要功能 IoC 和 AOP
        - Object createBean(String beanName, BeanDefinition beanDefinition)：真正创建 Bean
        - Object getBean(String beanName)：获取 Bean