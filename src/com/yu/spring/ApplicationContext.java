package com.yu.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * IoC 容器
 */
public class ApplicationContext {

    private Class configClass;
    private ConcurrentMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private ArrayList<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    /**
     * 构造方法（传入一个配置类）
     */
    public ApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 当前配置类上标注了 @ComponentScan 注解
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            // 获取注解信息
            ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            // 获取 @ComponentScan 中配置的扫描路径
            String path = componentScan.value();
            // 替换成文件分隔符“/”的形式：com/yu/service
            path = path.replace(".", "/");

            ClassLoader classLoader = ApplicationContext.class.getClassLoader();
            // getResource 会自动拼接 classpath，这个 URL 指向编译后的 service 这个目录：D:\Code\Java\IDEAProjects\YuSpring\out\production\YuSpring\com\yu\service
            URL resource = classLoader.getResource(path);

            File file = new File(resource.getFile());

            // 如果当前文件对象是文件夹，则继续
            if (file.isDirectory()) {
                // 获取文件夹的所有文件
                File[] files = file.listFiles();
                // 遍历所有文件
                for (File f : files) {
                    // 拿到文件的绝对路径
                    String fileName = f.getAbsolutePath();
                    // 判断是不是 class 文件
                    if (fileName.endsWith(".class")) {
                        // 拿到类的全限定名
                        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                        className = className.replace("\\", ".");
                        try {
                            // 使用类加载器加载该类
                            Class<?> clazz = classLoader.loadClass(className);
                            // 判断该类上是否有 @Component 注解
                            if (clazz.isAnnotationPresent(Component.class)) {
                                // 是否实现了后置处理器 BeanPostProcessor 接口
                                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                    BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.newInstance();
                                    beanPostProcessors.add(beanPostProcessor);
                                }
                                // 有 @Component 注解就生成 BeanDefinition
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz);
                                // 是否标注了 @Scope
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    // 标注了 @Scope 注解，则直接设置 scope 的值
                                    beanDefinition.setScope(clazz.getAnnotation(Scope.class).value());
                                } else {
                                    // 默认是单例
                                    beanDefinition.setScope("singleton");
                                }
                                // 获取配置的 BeanName
                                String beanName = clazz.getAnnotation(Component.class).name();
                                if ("".equals(beanName)) {
                                    // 如果默人没有配置 BeanName，则转换为类名（首字母小写）
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }
                                // 将 beanDefinition 存入 beanDefinitionMap
                                beanDefinitionMap.put(beanName, beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

        }

        // 扫描完成，创建单例 Bean（实例化）
        // 遍历 BeanDefinition
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            // 单例则直接创建 Bean，并放入单例池中
            if ("singleton".equals(beanDefinition.getScope())) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }

    }

    public Object createBean(String beanName, BeanDefinition beanDefinition) {
        /**
         * 1. 实例化对象
         */
        // 获取类对象
        Class clazz = beanDefinition.getType();
        // 创建的 Bean 对象
        Object instance = null;
        try {
            System.out.println("开始创建 " + beanName + " 对象（" + clazz.getName() + "）...");
            // 使用无参构造函数（简化问题）创建对象（反射）
            instance = clazz.getConstructor().newInstance();

            /**
             * 2. 属性注入（DI）
             */
            // 获取该类的所有属性
            Field[] fields = clazz.getDeclaredFields();
            // 判断每个字段是否含有 @Autowired 注解
            for (Field field : fields) {
                // 标准了 @Autowired 就自动注入
                if (field.isAnnotationPresent(Autowired.class)) {
                    // 设置反射权限
                    field.setAccessible(true);
                    // 根据当前属性名去获取 Bean
                    field.set(instance, getBean(field.getName()));
                }
            }

            /**
             * 3. Aware 接口回调
             */
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            /**
             * 4. 初始化前
             */
            // 遍历 BeanPostProcessor 列表，执行 postProcessBeforeInitialization 方法
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                instance = beanPostProcessor.postProcessBeforeInitialization(beanName, instance);
            }

            /**
             * 5. 初始化
             */
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            /**
             * 6. 初始化后
             */
            // 遍历 BeanPostProcessor 列表，执行 postProcessAfterInitialization 方法
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                instance = beanPostProcessor.postProcessAfterInitialization(beanName, instance);
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            // 如果找不到 Bean 的定义就直接抛异常
            throw new NullPointerException();
        } else {
            // 有 Bean 的定义
            // 如果是单例 Bean，则直接从单例池中获取
            if ("singleton".equals(beanDefinition.getScope())) {
                Object bean = singletonObjects.get(beanName);
                // 如果 bean 为空，说明 Bean 还没创建
                if (bean == null) {
                    // 创建的 Bean 对象
                    Object instance = createBean(beanName, beanDefinition);
                    // 放入单例池中
                    singletonObjects.put(beanName, instance);
                }
                // 直接返回单例池中的对象
                return bean;
            } else {
                // 如果是多例 Bean，则每一次 getBean 都创建新的 Bean 对象
                return createBean(beanName, beanDefinition);
            }
        }
    }
}
