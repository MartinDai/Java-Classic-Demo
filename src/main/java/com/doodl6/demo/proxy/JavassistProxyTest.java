package com.doodl6.demo.proxy;

import javassist.*;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.io.IOException;

/**
 * 基于javassist实现动态代理
 */
public class JavassistProxyTest {

    public static class Hello {

        public void sayHello() {
            System.out.println("hello world");
        }
    }

    /**
     * 第一种方式，使用ProxyFactory
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxyByFactory(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();

        // 设置父类为被代理类的类型
        proxyFactory.setSuperclass(clazz);

        // 创建代理类的class
        Class<?> proxyClass = proxyFactory.createClass();

        // 创建代理类实例
        T proxyBean = (T) proxyClass.newInstance();

        // 创建被代理类实例，这里比较简单直接使用newInstance，真实场景还要考虑是否默认的构造方法不可访问或者有多个重载构造方法等情况
        final T bean = clazz.newInstance();

        ((ProxyObject) proxyBean).setHandler((self, thisMethod, proceed, args) -> {
            if ("sayHello".equals(thisMethod.getName())) {
                System.out.println("before invoke factory proxy method:" + thisMethod.getName());
                Object result = thisMethod.invoke(bean, args);
                System.out.println("after invoke factory proxy method:" + thisMethod.getName());
                return result;
            } else {
                return thisMethod.invoke(bean, args);
            }
        });

        return proxyBean;
    }

    /**
     * 第二种方式，使用代码动态创建
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxyByClass(Class<T> clazz) throws CannotCompileException, IllegalAccessException, InstantiationException, NotFoundException, IOException {
        // 创建类池，true 表示使用默认路径
        ClassPool classPool = new ClassPool(true);

        String className = clazz.getName();
        // 创建一个代理类
        CtClass proxyCtClass = classPool.makeClass(className + "JavassistProxy");

        // 设置代理类的父类
        proxyCtClass.setSuperclass(classPool.get(clazz.getName()));

        // 添加默认构造函数
        proxyCtClass.addConstructor(CtNewConstructor.defaultConstructor(proxyCtClass));

        //循环代理类的方法，添加代理逻辑
        CtClass targetCtClass = classPool.get(clazz.getName());
        CtMethod[] ctMethods = targetCtClass.getDeclaredMethods();
        for (CtMethod ctMethod : ctMethods) {
            //只代理sayHello方法，这里可以根据具体需求选择性代理
            if ("sayHello".equals(ctMethod.getName())) {
                CtMethod newMethod = new CtMethod(ctMethod.getReturnType(), ctMethod.getName(),
                        ctMethod.getParameterTypes(), proxyCtClass);
                newMethod.setBody("super." + ctMethod.getName() + "();");
                newMethod.insertBefore("System.out.println(\"before invoke class proxy method:" + ctMethod.getName() + "\");");
                newMethod.insertAfter("System.out.println(\"after invoke class proxy method:" + ctMethod.getName() + "\");");
                proxyCtClass.addMethod(newMethod);
            }
        }

        //把生成的class写入文件
        ClassUtil.saveClass(proxyCtClass.toBytecode(), proxyCtClass.getName());
//        proxyCtClass.writeFile("target/classes");

        return (T) proxyCtClass.toClass().newInstance();
    }

    public static void main(String[] args) throws Throwable {
        Hello proxyHello1 = getProxyByFactory(Hello.class);
        proxyHello1.sayHello();
        System.out.println(proxyHello1.getClass().toString());
        System.out.println("----------");

        Hello proxyHello2 = getProxyByClass(Hello.class);
        proxyHello2.sayHello();
        System.out.println(proxyHello2.getClass().toString());
    }

}