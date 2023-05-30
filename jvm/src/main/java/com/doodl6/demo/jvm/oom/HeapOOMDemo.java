package com.doodl6.demo.jvm.oom;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟java.lang.OutOfMemoryError: Heap space
 * 堆内存溢出
 */
public class HeapOOMDemo {

    /**
     * JVM参数：-Xms5m -Xmx5m -XX:+HeapDumpOnOutOfMemoryError
     */
    public static void main(String[] args) {
        List<byte[]> list = new ArrayList<>();

        while (true) {
            list.add(new byte[1024]);
        }
    }
}
