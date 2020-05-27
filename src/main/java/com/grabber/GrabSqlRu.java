package com.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class GrabSqlRu implements Grab {

    @Override
    public void init(Parse parse, Store store, SimpleScheduleBuilder schedule) {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            JobDataMap data = new JobDataMap();
            data.put("parse", parse);
            data.put("store", store);
            data.put("latch", latch);

            JobDetail job = newJob(GrabJob.class)
                    .usingJobData(data)
                    .build();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(schedule)
                    .build();

            scheduler.scheduleJob(job, trigger);

            latch.await();

            scheduler.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            Parse parse = (Parse) context.getJobDetail().getJobDataMap().get("parse");
            Store store = (Store) context.getJobDetail().getJobDataMap().get("store");
            CountDownLatch latch = (CountDownLatch) context.getJobDetail().getJobDataMap().get("latch");
            String url = "https://www.sql.ru/forum/job-offers/";
            int count = 1;
            List<Post> posts;
            do {
                String rUrl = String.format("%s%s", url, count);
                System.out.printf("GrabJob %s %s %n", count, rUrl);
                posts = parse.list(rUrl);
                for (Post post : posts) {
                    store.save(post);
                }
                count++;
            } while (posts.size() > 0);
            latch.countDown();
        }
    }

    public static Properties getProp() {
        Properties prop = new Properties();
        try (FileInputStream in = new FileInputStream("grabber.properties")) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    public static void main(String[] args) {
        SimpleScheduleBuilder times = repeatHourlyForTotalCount(1);
        Parse parse = new ParseSqlRu();
        Grab grab = new GrabSqlRu();
        try (StorePSQL store = new StorePSQL(getProp())) {
            grab.init(parse, store, times);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

