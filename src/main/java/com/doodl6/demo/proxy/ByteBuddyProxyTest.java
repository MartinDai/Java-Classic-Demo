package com.doodl6.demo.proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 基于byte-buddy实现动态代理
 */
public class ByteBuddyProxyTest {

    public static class Hello {

        public void sayHello() {
            System.out.println("hello world");
        }
    }

    private static Hello createByteBuddyDynamicProxy() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DynamicType.Loaded<Hello> loaded = new ByteBuddy().subclass(Hello.class)
                .method(ElementMatchers.named("sayHello"))
                .intercept(MethodDelegation.to(new HelloInterceptor()))
                .make()
                .load(ByteBuddyProxyTest.class.getClassLoader());
        Hello proxy = loaded.getLoaded().getDeclaredConstructor().newInstance();

        //保存字节码文件
        byte[] bytes = loaded.getBytes();
        ClassUtil.saveClass(bytes, proxy.getClass().getName());

        return proxy;
    }

    public static class HelloInterceptor {

        public Object interceptor(@This Object proxy, @Origin Method method,
                                  @SuperMethod Method superMethod,
                                  @AllArguments Object[] args) throws Exception {
            System.out.println("before invoke method: " + method.getName());
            Object ret = superMethod.invoke(proxy, args);
            System.out.println("after invoke method: " + method.getName());
            return ret;
        }
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        Hello hello = createByteBuddyDynamicProxy();
        hello.sayHello();
        System.out.println(hello.getClass().toString());
    }
}
