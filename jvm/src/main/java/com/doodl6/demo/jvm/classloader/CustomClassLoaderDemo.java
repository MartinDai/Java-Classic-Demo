package com.doodl6.demo.jvm.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomClassLoaderDemo {

    private static ClassLoader classLoader = null;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        classLoader = new CustomClassLoader();

        Class<?> clazz = classLoader.loadClass("com.doodl6.demo.jvm.Test");
        Object test = clazz.newInstance();
        Method method = clazz.getMethod("sayHello");
        method.invoke(test);
        System.out.println("class name:" + clazz.getName());
    }
}
