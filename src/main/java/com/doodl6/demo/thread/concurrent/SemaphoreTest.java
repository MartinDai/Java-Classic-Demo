package com.doodl6.demo.thread.concurrent;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class SemaphoreTest {

    public static void main(String[] args) throws InterruptedException {
        //同一时间最多只允许2个线程执行
        Semaphore semaphore = new Semaphore(2);
        Thread threadA = new Thread(new MyRunnable(semaphore), "threadA");
        Thread threadB = new Thread(new MyRunnable(semaphore), "threadB");
        Thread threadC = new Thread(new MyRunnable(semaphore), "threadC");
        Thread threadD = new Thread(new MyRunnable(semaphore), "threadD");

        threadA.start();
        threadB.start();
        threadC.start();
        threadD.start();

        //等待所有子线程执行完毕
        Thread.sleep(12000);
    }

    private static class MyRunnable implements Runnable {

        private final Semaphore semaphore;

        public MyRunnable(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            //申请1个信号量
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + " is running");

            //随机休眠一段时间
            try {
                Thread.sleep(new Random().nextInt(3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //释放1个信号量
            semaphore.release();
            System.out.println(Thread.currentThread().getName() + " is finished");
        }
    }
}
