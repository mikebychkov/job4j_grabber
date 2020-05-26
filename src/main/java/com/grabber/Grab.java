package com.grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;

public interface Grab {
    void init(Parse parse, Store store, SimpleScheduleBuilder schedule) throws SchedulerException;
}
