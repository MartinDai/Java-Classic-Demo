package com.doodl6.demo.schedule.timewheel;

public interface TimerTask {

    void run(Timeout timeout) throws Exception;
}