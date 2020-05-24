package com.quartz3;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    public static Properties getProp() {
        Properties prop = new Properties();
        try (FileInputStream in = new FileInputStream("rabbit.properties")) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    public static void main(String[] args) {
        Properties prop = getProp();
        try (Connection conn = DriverManager.getConnection(
                prop.getProperty("rabbit.connection.url"),
                prop.getProperty("rabbit.connection.user"),
                prop.getProperty("rabbit.connection.password")
                )
        ) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("conn", conn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            Connection conn = (Connection) context.getJobDetail().getJobDataMap().get("conn");
            String query = "INSERT INTO rabbit(created_date) VALUES(?)";
            try (PreparedStatement st = conn.prepareStatement(query)) {
                st.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
