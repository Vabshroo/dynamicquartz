package com.chenlei.entity;

import com.chenlei.task.DynamicTask;

import java.io.Serializable;

/**
 * Created by chenlei on 2017/8/28.
 */
public class ScheduleJob implements Serializable {

    private String jobId;
    private String jobName;
    private String jobGroup;
    private Integer jobStatus;
    private String cronExpression;
    private String desc;
    private DynamicTask dynamicTask;
    private String triggerState;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DynamicTask getDynamicTask() {
        return dynamicTask;
    }

    public void setDynamicTask(DynamicTask dynamicTask) {
        this.dynamicTask = dynamicTask;
    }

    public String getTriggerState() {
        return triggerState;
    }

    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState;
    }

    @Override
    public int hashCode() {
        return (jobName + jobGroup).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (jobName + jobGroup).equals(((ScheduleJob)obj).getJobName() + ((ScheduleJob)obj).getJobGroup());
    }

    public static class Builder{

        ScheduleJob scheduleJob;

        public Builder(){
            scheduleJob = new ScheduleJob();
        }

        public Builder jobId(String jobId){
            scheduleJob.setJobId(jobId);
            return this;
        }

        public Builder jobName(String jobName){
            scheduleJob.setJobName(jobName);
            return this;
        }

        public Builder jobGroup(String jobGroup){
            scheduleJob.setJobGroup(jobGroup);
            return this;
        }

        public Builder jobStatus(Integer jobStatus){
            scheduleJob.setJobStatus(jobStatus);
            return this;
        }

        public Builder cronExpression(String cronExpression){
            scheduleJob.setCronExpression(cronExpression);
            return this;
        }

        public Builder desc(String desc){
            scheduleJob.setDesc(desc);
            return this;
        }

        public Builder dynamicTask(DynamicTask dynamicTask){
            scheduleJob.setDynamicTask(dynamicTask);
            return this;
        }

        public ScheduleJob build(){
            return scheduleJob;
        }
    }

}
