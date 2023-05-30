package com.doodl6.demo.thread.concurrent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        //多线程执行任务，等待所有线程完成以后，再执行主线程
        CountDownLatch countDownLatch = new CountDownLatch(3);
        Thread threadA = new Thread(new MyRunnable(countDownLatch), "threadA");
        Thread threadB = new Thread(new MyRunnable(countDownLatch), "threadB");
        Thread threadC = new Thread(new MyRunnable(countDownLatch), "threadC");

        threadA.start();
        threadB.start();
        threadC.start();

        countDownLatch.await();
        System.out.println("子线程已全部执行完，继续执行主线程");
    }

    private static class MyRunnable implements Runnable {

        private final CountDownLatch countDownLatch;

        public MyRunnable(CountDownLatch countDownLatch){
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            //随机休眠一段时间
            try {
                Thread.sleep(new Random().nextInt(3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " is finished");
            countDownLatch.countDown();
        }
    }
}
