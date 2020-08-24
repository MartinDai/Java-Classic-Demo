package com.doodl6.demo.thread.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedThreadPoolTest {

    private static final AtomicInteger threadNum = new AtomicInteger(0);

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2, r -> {
            //会执行两次
            System.out.println("创建新线程");
            Thread thread = new Thread(r);
            thread.setName("FixedThread-" + threadNum.incrementAndGet());
            return thread;
        });

        //输出10次的线程名字只有两种，说明只有两个线程执行任务
        for (int i = 0; i < 10; i++) {
            int taskNo = i;
            executorService.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "执行任务" + taskNo + "完成");
            });
        }
    }
}
