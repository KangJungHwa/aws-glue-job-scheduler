package com.lgdisplay.bigdata.api.glue.scheduler.service;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuartzSchedulerService {
//TODO
//실제로 QUARTZ 테이블에 가져와서 아래를 구현한다.
    @Autowired
    Scheduler scheduler;

//    public JobEntity getJobEntityById(Integer id) {
//        return repository.getById(id);
//    }
//
//
//    /**
//     */
//    public Integer create(JobEntity jobEntity){
//
//        repository.save(jobEntity);
//        return jobEntity.getId();
//    }
//    /**
//     */
//    public Integer pause(JobEntity jobEntity){
//
//        repository.save(jobEntity);
//        return jobEntity.getId();
//    }
//    /**
//     */
//    public Integer delete(JobEntity jobEntity){
//        repository.delete(jobEntity);
//        return jobEntity.getId();
//    }
//
//    public List<JobEntity> loadJobs() {
//        return repository.findAll();
//    }
//
//    public JobDataMap getJobDataMap(JobEntity job) {
//        JobDataMap map = new JobDataMap();
//        map.put("name", job.getName());
//        map.put("jobGroup", job.getJobGroup());
//        map.put("cronExpression", job.getCron());
//        map.put("parameter", job.getParameter());
//        map.put("jobDescription", job.getDescription());
//        map.put("vmParam", job.getVmParam());
//        map.put("jarPath", job.getJarPath());
//        map.put("status", job.getStatus());
//        return map;
//    }
//    public JobDetail getJobDetail(JobKey jobKey, String description, JobDataMap map) {
//        return JobBuilder.newJob(DynamicJob.class)
//                .withIdentity(jobKey)
//                .withDescription(description)
//                .setJobData(map)
//                .storeDurably()
//                .build();
//    }
//
//
//    public Trigger getTrigger(JobEntity job) {
//        return TriggerBuilder.newTrigger()
//                .withIdentity(job.getName(), job.getJobGroup())
//                .withSchedule(CronScheduleBuilder.cronSchedule(job.getCron()))
//                .build();
//    }
//
//
//    public JobKey getJobKey(JobEntity job) {
//        return JobKey.jobKey(job.getName(), job.getJobGroup());
//    }


}
