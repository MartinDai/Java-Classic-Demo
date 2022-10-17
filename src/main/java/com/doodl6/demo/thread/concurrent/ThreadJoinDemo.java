package com.doodl6.demo.thread.concurrent;

public class ThreadJoinDemo {

    public static void main(String[] args) throws InterruptedException {
        //保证A,B,C三个线程顺序执行
        Thread threadA = new Thread(new MyRunnable(), "threadA");
        Thread threadB = new Thread(new MyRunnable(), "threadB");
        Thread threadC = new Thread(new MyRunnable(), "threadC");

        threadA.start();
        threadA.join();
        threadB.start();
        threadB.join();
        threadC.start();
        threadC.join();
    }

    private static class MyRunnable implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " is finished");
        }
    }
}
