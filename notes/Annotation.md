# Annotation学习笔记

## 简介
Annotation中文名称[注解]，是Java为Class、Method、Field等提供附加元数据的功能。

## 元注解
注解分为元注解和自定义注解，元注解用于修饰自定义注解，主要有4种：@Target、@Retention、@Document、@Inherited。

#### @Target
用于声明注解作用的对象，主要有TYPE(类)、FIELD(成员变量)、METHOD(成员方法)、PARAMETER(方法参数)、CONSTRUCTOR(构造方法)、LOCAL_VARIABLE(方法变量)等。 

#### @Retention
用于声明注解的生命周期，主要有3种：RUNTIME(运行时保留)、CLASS(编译成字节码文件保留，类加载后丢失)、SOURCE(编译时保留，编译成字节码后丢失)。

#### @Documented
用于声明注解是否被javadoc用于生成文档。

#### @Inherited
用于声明注解是否能被子类继承，例如该注解作用于类A，类B继承类A时，注解是否作用于类B。

## 自定义注解
Java标准库提供了一些内置的注解例如：@Override、@Deprecated、@SuppressWarnings等；

Android也提供了一些内置的注解列如：@Nullable、@NonNull、@ColorRes、@StringRes等；

如果我们需要丰富自己的代码功能，也可以自定义需要的注解，通常分为3步：
1. 定义注解。
2. 编写注解处理代码。
3. 使用注解。

**定义注解：**
```java
// 声明作用的对象
@Target(value = {ElementType.TYPE})
// 声明作用的声明周期
@Retention(RetentionPolicy.RUNTIME)
// 使用@interface声明是一个注解
public @interface Model {
    // 声明附加的信息
    String name();
}
```

**编写注解处理代码：**
```java
// 根据注解的功能编写处理程序
public class ModelHandler {
    public static void handle(Object target) {
        Class<?> clazz = target.getClass();
        // 检查是否有对应的注解作用于该类
        if (!clazz.isAnnotationPresent(Model.class)) return;
        // 获取指定注解
        Model model = clazz.getAnnotation(Model.class);
        // 获取注解附带的信息
        String modelName = model.name();
        System.out.println("find model:" + modelName);
    }
}
```
**使用注解**
```java
// 将注解作用于类上
@Model(name = "user")
public class UserModel {}
```

## 注解加载原理
注解是一个继承自Annotation的特殊接口，在源码编译的时候会将注解的信息解析写入字节码文件中，在类加载时会将注解的信息加载到类内存中，当使用反射获取类的注解时会通过动态代理生成一个注解的代理对象$Proxy1去获取注解附加的数据。
