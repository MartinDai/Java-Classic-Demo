package com.doodl6.demo.schedule.timewheel;

import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public interface Timer {

    Timeout newTimeout(TimerTask task, long delay, TimeUnit unit);

    Set<Timeout> stop();
}