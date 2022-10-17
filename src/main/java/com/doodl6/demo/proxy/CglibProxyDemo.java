package com.doodl6.demo.proxy;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 基于cglib实现动态代理
 */
public class CglibProxyDemo {

    public static class Hello {

        public void sayHello() {
            System.out.println("hello world");
        }
    }

    public static class MyInterceptor implements MethodInterceptor {

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            boolean needProxy = "sayHello".equals(method.getName());
            if (needProxy) {
                System.out.println("before invoke method:" + method.getName());
            }

            Object result = methodProxy.invokeSuper(obj, args);
            if (needProxy) {
                System.out.println("after invoke method:" + method.getName());
            }
            return result;
        }
    }

    public static void main(String[] args) throws Exception {
        //配置生成的代理类文件路径
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "target/classes");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Hello.class);
        enhancer.setCallback(new MyInterceptor());

        Hello hello = (Hello) enhancer.create();
        hello.sayHello();
        System.out.println(hello.getClass().toString());
    }

}