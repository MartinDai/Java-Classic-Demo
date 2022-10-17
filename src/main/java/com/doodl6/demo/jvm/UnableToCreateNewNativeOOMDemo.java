package com.doodl6.demo.jvm;

/**
 * 模拟java.lang.OutOfMemoryError: unable to create new native
 * 无法创建新线程导致内存溢出，不好复现，该程序会导致电脑卡顿，建议在容器中运行
 */
public class UnableToCreateNewNativeOOMDemo {

    /**
     * JVM参数：-Xss256k -XX:+HeapDumpOnOutOfMemoryError
     */
    public static void main(String[] args) {
        while (true) {
            new DeathThread().start();
        }
    }

    public static class DeathThread extends Thread {

        @Override
        public void run() {
            while (true) {
                //死循环，防止线程结束
            }
        }
    }
}
