package com.doodl6.demo.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 基于cglib实现动态代理
 */
public class CglibProxyTest {

    public static class Hello {

        public void sayHello() {
            System.out.println("hello world");
        }
    }

    public static void main(String[] args) throws Throwable {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Hello.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args1, proxy) -> {
            boolean needProxy = "sayHello".equals(method.getName());
            if (needProxy) {
                System.out.println("before invoke method:" + method.getName());
            }

            Object result = proxy.invokeSuper(obj, args1);
            if (needProxy) {
                System.out.println("before invoke method:" + method.getName());
            }
            return result;
        });
        Hello hello = (Hello) enhancer.create();
        hello.sayHello();
        System.out.println(hello.getClass().toString());
    }

}