package com.doodl6.demo.jvm;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * 模拟java.lang.OutOfMemoryError: Direct buffer memory
 * 堆外内存溢出
 */
public class DirectMemoryOOM {

    private static final int _1MB = 1024 * 1024;

    /**
     * JVM参数：-XX:MaxDirectMemorySize=20M
     */
    public static void main(String[] args) {
        List<ByteBuffer> list = new LinkedList<>();
        while (true) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(_1MB);
            list.add(byteBuffer);
        }
    }

}