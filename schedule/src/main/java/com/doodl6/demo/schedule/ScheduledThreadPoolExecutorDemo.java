package com.doodl6.demo.schedule;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.*;

public class ScheduledThreadPoolExecutorDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long now = System.currentTimeMillis();
        int delay1 = 5124;
        int delay2 = 2523;
        int delay3 = 1462;
        int delay4 = 4236;
        int delay5 = 3634;
        Date date1 = new Date(now + delay1);
        Date date2 = new Date(now + delay2);
        Date date3 = new Date(now + delay3);
        Date date4 = new Date(now + delay4);
        Date date5 = new Date(now + delay5);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
        ScheduledFuture future1 = executor.schedule(new MyCall(date1), delay1, TimeUnit.MILLISECONDS);
        ScheduledFuture future2 = executor.schedule(new MyCall(date2), delay2, TimeUnit.MILLISECONDS);
        ScheduledFuture future3 = executor.schedule(new MyCall(date3), delay3, TimeUnit.MILLISECONDS);
        ScheduledFuture future4 = executor.schedule(new MyCall(date4), delay4, TimeUnit.MILLISECONDS);
        ScheduledFuture future5 = executor.schedule(new MyCall(date5), delay5, TimeUnit.MILLISECONDS);

        //这里可以获取到任务抛出的异常
//        future2.get();

        //等待所有任务执行完成
        Thread.sleep(10000);
        executor.shutdown();
    }

    static class MyCall implements Callable<Void> {
        Timestamp myTime;

        public MyCall(Date myDate) {
            this.myTime = new Timestamp(myDate.getTime());
        }

        @Override
        public Void call() {
            System.out.println(Thread.currentThread().getName() + " 计划执行时间：" + myTime + "  实际执行时间：" + new Timestamp(System.currentTimeMillis()));

            //多线程调度，单个线程执行时间长不会影响其他任务正常调度
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

//            throw new IllegalStateException("即使有异常也不影响其他任务调度");
            return null;
        }
    }
}
