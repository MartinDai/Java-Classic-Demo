package com.doodl6.demo.jvm;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * 模拟java.lang.OutOfMemoryError: Metaspace
 */
public class MetaspaceOOM {

    /**
     * JVM参数：-XX:MaxMetaspaceSize=10M -XX:+HeapDumpOnOutOfMemoryError
     */
    public static void main(String[] args) {
        int loadClassCount = 0;
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(Object.class);
            enhancer.setUseCache(false);
            enhancer.setCallback((MethodInterceptor) (obj, method, args1, proxy) -> proxy.invokeSuper(obj, args1));
            enhancer.create();
            System.out.println("已加载增强类数量:" + (++loadClassCount));
        }
    }

}
