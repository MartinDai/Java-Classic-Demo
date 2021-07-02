package com.doodl6.demo.schedule;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTest {

    public static void main(String[] args) throws InterruptedException {
        long now = System.currentTimeMillis();
        Date date1 = new Date(now + 5123);
        Date date2 = new Date(now + 2423);
        Date date3 = new Date(now + 1864);
        Date date4 = new Date(now + 4193);
        Date date5 = new Date(now + 3749);
        Timer timer = new Timer();
        timer.schedule(new MyTimerTask(date1), date1);
        timer.schedule(new MyTimerTask(date2), date2);
        timer.schedule(new MyTimerTask(date3), date3);
        timer.schedule(new MyTimerTask(date4), date4);
        timer.schedule(new MyTimerTask(date5), date5);
        Thread.sleep(6000);
        timer.cancel();
    }

    static class MyTimerTask extends TimerTask {
        Timestamp myTime;

        public MyTimerTask(Date myDate) {
            this.myTime = new Timestamp(myDate.getTime());
        }

        @Override
        public void run() {
            System.out.println("计划执行时间：" + myTime + "  实际执行时间：" + new Timestamp(System.currentTimeMillis()));
            //因为是单线程执行调度，所以如果任务执行时间比较长或者抛出异常都会影响后续的任务调度
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

//            throw new IllegalStateException("任务中断，后面所有的任务都不会执行了");
        }
    }
}
