package com.doodl6.demo.thread.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadPoolDemo {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
            //只会执行一次这里的逻辑
            System.out.println("创建新线程");
            Thread thread = new Thread(r);
            thread.setName("SingleThread");
            return thread;
        });

        //输出10次的线程名字相同，说明只有一个线程执行任务
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

        executorService.shutdown();
    }
}
