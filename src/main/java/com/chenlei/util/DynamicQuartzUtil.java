package com.chenlei.util;

import com.chenlei.entity.ScheduleJob;
import com.chenlei.task.DynamicTask1;
import org.quartz.*;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

import java.util.*;

/**
 * Created by chenlei on 2017/8/28.
 */
public class DynamicQuartzUtil {

    public static Set<String> jobIds = new HashSet<String>();

    public static List<ScheduleJob> jobs = new ArrayList<ScheduleJob>();

    //public scheduler
    public static Scheduler scheduler;

    //spring context
    public static ApplicationContext context;

    public static void initScheduler() throws NoSuchMethodException, ClassNotFoundException, SchedulerException, InstantiationException, IllegalAccessException {
        getContext();
        scheduler = (StdScheduler) context.getBean("schedulerFactoryBean");
        jobs.add(new ScheduleJob.Builder()
                .jobId(UUID.randomUUID().toString())
                .jobName("测试任务")
                .jobGroup("monitorGroup")
                .desc("开始1秒钟一次，然后5秒钟一次，最后改变执行任务")
                .jobStatus(0)
                .cronExpression("0/1 * * * * ?")
                .dynamicTask(new DynamicTask1())
                .build());
        for (ScheduleJob job : jobs) {
            createScheduler(job);
        }//可以自定义注解在spring context加载完成后自动初始化，ApplicationContextAware,BeanFactoryPostProcessor
        System.out.println("初始化完毕");
        System.out.println(allJobs());

    }

    public static void createScheduler(ScheduleJob job) throws NoSuchMethodException, ClassNotFoundException, SchedulerException, IllegalAccessException, InstantiationException {
        if(job.getJobStatus() == 0){//可用
            MethodInvokingJobDetailFactoryBean methodInvJobDetailFB = new MethodInvokingJobDetailFactoryBean();
            methodInvJobDetailFB.setName(job.getJobName());
            methodInvJobDetailFB.setTargetObject(getContext().getBean(job.getDynamicTask().getClass()));
            //执行方法
            methodInvJobDetailFB.setTargetMethod("run");
            //提交
            methodInvJobDetailFB.afterPropertiesSet();
            //并发
            methodInvJobDetailFB.setConcurrent(false);

            //cron表达式
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();

            JobDetail jobDetail = methodInvJobDetailFB.getObject();
            jobDetail.getJobDataMap().put("scheduleJob", job);

            //set scheduler
            scheduler.scheduleJob(jobDetail, trigger);
            System.out.println("新增：" + job.getJobName());
            System.out.println(allExecutingJobs());

        }
    }

    public static void updateScheduler(ScheduleJob job, TriggerKey triggerKey, CronTrigger trigger) throws SchedulerException {
        if (job.getJobStatus() == 0) {// 0启用 1禁用
            if (!trigger.getCronExpression().equalsIgnoreCase(job.getCronExpression())) {
                // 表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                // 按新的trigger重新设置job执行
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        } else if (job.getJobStatus() == 1) {
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(trigger.getJobKey());// 删除任务
        }
        System.out.println("更新：" + job.getJobName());
    }

    public static ApplicationContext getContext(){
        if(context == null){
            context = new ClassPathXmlApplicationContext("classpath:spring/D*.xml");
        }
        return context;
    }

    /**
     * 所有Schedule
     * @return
     * @throws SchedulerException
     */
    public static List<ScheduleJob> allJobs() throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                ScheduleJob job = new ScheduleJob();
                job.setJobName(jobKey.getName());
                job.setJobGroup(jobKey.getGroup());
                job.setDesc("触发器:" + trigger.getKey());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setTriggerState(triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronExpression(cronExpression);
                }
                jobList.add(job);
            }
        }
        return jobList;
    }

    /**
     * 所有执行中的任务
     * @return
     * @throws SchedulerException
     */
    public static List<ScheduleJob> allExecutingJobs() throws SchedulerException {
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        List<ScheduleJob> jobList = new ArrayList<ScheduleJob>(executingJobs.size());
        for (JobExecutionContext executingJob : executingJobs) {
            ScheduleJob job = new ScheduleJob();
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            job.setJobName(jobKey.getName());
            job.setJobGroup(jobKey.getGroup());
            job.setDesc("触发器:" + trigger.getKey());
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            job.setTriggerState(triggerState.name());
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                job.setCronExpression(cronExpression);
            }
            jobList.add(job);
        }
        return jobList;
    }

    /**
     * 暂停任务
     * @param job
     * @throws SchedulerException
     */
    public static void pauseJob(ScheduleJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        scheduler.pauseJob(jobKey);
    }

    /**
     * 恢复任务
     * @param job
     * @throws SchedulerException
     */
    public static void resumeJob(ScheduleJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        scheduler.resumeJob(jobKey);
    }

    /**
     * 删除任务
     * @param job
     * @throws SchedulerException
     */
    public static void deleteJob(ScheduleJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        scheduler.deleteJob(jobKey);
        jobs.remove(job);
    }

    /**
     * 立即执行任务
     * @param job
     * @throws SchedulerException
     */
    public void triggerJob(ScheduleJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        scheduler.triggerJob(jobKey);
    }



}
