package com.doodl6.demo.thread.concurrent;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        //多线程执行任务，等待所有线程准备完成以后，再一起继续执行
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
        Thread threadA = new Thread(new MyRunnable(cyclicBarrier), "threadA");
        Thread threadB = new Thread(new MyRunnable(cyclicBarrier), "threadB");
        Thread threadC = new Thread(new MyRunnable(cyclicBarrier), "threadC");

        threadA.start();
        threadB.start();
        threadC.start();

        //等待所有子线程执行完毕
        Thread.sleep(4000);
    }

    private static class MyRunnable implements Runnable {

        private final CyclicBarrier cyclicBarrier;

        public MyRunnable(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            //随机休眠一段时间
            try {
                Thread.sleep(new Random().nextInt(3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + " is ready");

            try {
                //所有子线程都走到这里以后，才会继续往下走
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + " is finished");
        }
    }
}
