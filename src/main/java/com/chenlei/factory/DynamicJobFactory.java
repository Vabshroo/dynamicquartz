package com.chenlei.factory;

import com.chenlei.entity.ScheduleJob;
import com.chenlei.util.DynamicQuartzUtil;
import org.quartz.*;

/**
 * Created by chenlei on 2017/8/28.
 */
public class DynamicJobFactory   {

    public void scanJobs() throws SchedulerException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        //查找数据库所有任务
        //查找新增和有更新的任务
        //查找更新时间在上次执行时间之后的任务
        //...
        for (ScheduleJob job : DynamicQuartzUtil.jobs) {
            // Keys are composed of both a name and group, and the name  must be unique within the group
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            // 获取trigger
            CronTrigger trigger = (CronTrigger) DynamicQuartzUtil.scheduler.getTrigger(triggerKey);
            // 不存在，创建一个
            if (null == trigger) {
                DynamicQuartzUtil.createScheduler(job);
            } else {// Trigger已存在，那么更新相应的定时设置
                DynamicQuartzUtil.updateScheduler(job, triggerKey, trigger);
            }
        }

    }

}
