package com.doodl6.demo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 利用jdk自带的方式实现动态代理
 */
public class JdkProxyDemo {

    private interface IHello {
        void sayHello();
    }

    /**
     * 被代理的类，需要实现接口
     */
    private static class Hello implements IHello {

        @Override
        public void sayHello() {
            System.out.println("hello world");
        }
    }

    /**
     * 基于JDK自带的InvocationHandler实现的动态代理类
     */
    private static class JdkProxy implements InvocationHandler {

        Object target;

        private JdkProxy(Object object) {
            this.target = object;
        }

        public static Object getProxyInstance(Object object) {
            return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), new JdkProxy(object));
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("before invoke method:" + method.getName());
            Object result = method.invoke(target, args);
            System.out.println("after invoke method:" + method.getName());
            return result;
        }
    }

    public static void main(String[] args) {
        //开启保存生成的类文件，运行完成后会在项目根目录根据类包名生成文件夹存放代理类class文件
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        IHello hello = (IHello) JdkProxy.getProxyInstance(new Hello());
        hello.sayHello();
        System.out.println(hello.getClass().toString());
    }
}