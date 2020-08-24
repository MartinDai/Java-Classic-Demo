package com.doodl6.demo.thread.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NormalThreadPoolTest {

    private static final AtomicInteger threadNum = new AtomicInteger(0);

    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(
                //核心线程数
                1,
                //最大线程数
                2,
                //空闲时间
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(5),
                r -> {
                    System.out.println("创建新线程");
                    Thread thread = new Thread(r);
                    thread.setName("NormalThread-" + threadNum.incrementAndGet());
                    return thread;
                },
                new ThreadPoolExecutor.CallerRunsPolicy());

        for (int i = 0; i < 10; i++) {
            int taskNo = i;
            executorService.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + "执行任务" + taskNo + "完成");
            });
        }

        executorService.shutdown();
    }
}
