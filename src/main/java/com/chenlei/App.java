package com.chenlei;

import com.chenlei.entity.ScheduleJob;
import com.chenlei.task.DynamicTask2;
import com.chenlei.util.DynamicQuartzUtil;
import org.quartz.CronTrigger;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, SchedulerException, IllegalAccessException, InterruptedException {
        DynamicQuartzUtil.context = new ClassPathXmlApplicationContext("classpath:spring/D*.xml");

        new Thread(new Runnable() {
            public void run() {
                try {
                    DynamicQuartzUtil.initScheduler();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SchedulerException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            DynamicQuartzUtil.jobs.add(new ScheduleJob.Builder()
                    .jobId(UUID.randomUUID().toString())
                    .jobName("新加进来的")
                    .jobGroup("monitorGroup")
                    .desc("新加进来的")
                    .jobStatus(0)
                    .cronExpression("0/5 * * * * ?")
                    .dynamicTask(new DynamicTask2())
                    .build());
                DynamicQuartzUtil.jobs.get(0).setCronExpression("*/3 * * * * ?");

                try {
                    Thread.sleep(40000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    ScheduleJob job = DynamicQuartzUtil.jobs.get(1);
                    job.setJobStatus(1);
                    TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
                    // 获取trigger
                    CronTrigger trigger = (CronTrigger) DynamicQuartzUtil.scheduler.getTrigger(triggerKey);
                    DynamicQuartzUtil.updateScheduler(job,triggerKey,trigger);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
