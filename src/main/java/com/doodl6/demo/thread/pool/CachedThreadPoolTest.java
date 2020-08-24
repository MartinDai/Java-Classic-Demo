package com.doodl6.demo.thread.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPoolTest {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool(r -> {
            //每次提交任务都会创建线程
            System.out.println("创建新线程");
            Thread thread = new Thread(r);
            thread.setName("CachedThread-" + System.currentTimeMillis());
            return thread;
        });

        //输出10次的线程名字都不相同，说明每个任务都有一个单独的线程执行
        for (int i = 0; i < 10; i++) {
            try {
                //为了模拟出不同的线程名称，每次提交都休眠1毫秒
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
