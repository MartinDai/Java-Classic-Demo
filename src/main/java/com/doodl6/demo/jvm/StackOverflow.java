package com.doodl6.demo.jvm;

/**
 * 模拟java.lang.StackOverflowError
 * 栈溢出
 */
public class StackOverflow {

    private static int stackDepth = 0;

    /**
     * JVM参数：-Xss256k
     */
    public static void main(String[] args) {
        try {
            deepStack();
        } catch (Throwable e) {
            System.out.println("stack depth:" + stackDepth);
            throw e;
        }
    }

    public static void deepStack() {
        stackDepth++;
        deepStack();
    }
}
