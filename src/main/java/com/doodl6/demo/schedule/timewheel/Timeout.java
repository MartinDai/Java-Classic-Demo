package com.doodl6.demo.schedule.timewheel;

import java.sql.Timestamp;

public interface Timeout {

    Timestamp getTimestamp();

    boolean isExpired();

}