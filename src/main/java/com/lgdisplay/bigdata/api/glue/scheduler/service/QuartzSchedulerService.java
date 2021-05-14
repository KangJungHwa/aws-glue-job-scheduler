package com.lgdisplay.bigdata.api.glue.scheduler.service;

import com.lgdisplay.bigdata.api.glue.scheduler.jobs.StartJob;
import com.lgdisplay.bigdata.api.glue.scheduler.model.Job;
import com.lgdisplay.bigdata.api.glue.scheduler.model.Run;
import com.lgdisplay.bigdata.api.glue.scheduler.model.http.StartJobRunRequest;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.JobRepository;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.RunRepository;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.TriggerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/*
* id를 가지고 job 정보를 가지고 와서 실행을 시킨다.
* 실행시킨 후 정보 업데이트 해준다.
 */


@Service
@Slf4j
public class QuartzSchedulerService {

    @Autowired
    RunRepository runRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    TriggerRepository triggerRepository;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private QuartzSchedulerService quartzSchedulerService;


    public String  startJob(Run run) {
        runRepository.save(run);
        return run.getJobRunId();
    }

    public String registAndStartJob(Run run) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        JobKey jobKey=new JobKey(run.getJobName(),"Group");
        Optional<String> cronTab = triggerRepository.findCronExpression(run.getJobName());
        if (!cronTab.isPresent()) {
            log.error("crontab Expression Not Found !");
            return "crontab Expression Not Found !";
        }
        String cronStr=cronTab.get();
        cronStr = cronStr.replace("cron(", "").replace(")", "");

        JobDetail jobDetail = JobBuilder
                .newJob(StartJob.class)
                .withIdentity(jobKey)
                .storeDurably()
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(run.getJobName(), "Group")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronStr))
                .build();


        scheduler.scheduleJob(jobDetail, trigger);

        return run.getJobRunId();
    }

    public String  createJob(String userName,String jobName) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            Optional<Job> job = jobRepository.findByUsernameAndJobName(userName, jobName);
            if (!job.isPresent()) {
                return "JOB_NOT_FOUND";
            }
            JobKey jobKey = new JobKey(jobName, "GROUP");
            JobDetail jobDetail = JobBuilder
                    .newJob(StartJob.class)
                    .withIdentity(jobKey)
                    .storeDurably()
                    .build();

            scheduler.addJob(jobDetail,true);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return"jobName";
    }

    //스케줄된 trigger만 정지 한다.(unscheduleJob)
    public String killJob(Long jobId){
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            Optional<Job> job = jobRepository.findById(jobId);
            if (!job.isPresent()) {
                return "JOB_NOT_FOUND";
            }
            String jobName=job.get().getJobName();
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, "Group"));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return"jobName";
    }

    //나중에 스케줄링할 job을 테이블에 등록만 한다.
    public String addJob(String userName,String jobName) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Optional<Job> job = jobRepository.findByUsernameAndJobName(userName,jobName);
        if (!job.isPresent()) {
            return "JOB_NOT_FOUND";
        }
        JobKey jobKey = new JobKey(jobName, "GROUP");
        JobDetail jobDetail = JobBuilder
                .newJob(StartJob.class)
                .withIdentity(jobKey)
                .storeDurably()
                .build();

        scheduler.addJob(jobDetail,false);
        return"jobName";
    }

    //TODO 수정 및 테스트
    //이미 저장된 job의 트리거를 등록한다.
    public String addTrigger(String userName,String jobName) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Optional<Job> job = jobRepository.findByUsernameAndJobName(userName,jobName);
        JobKey jobKey = new JobKey(job.get().getJobName(), "GROUP");
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .forJob(jobKey)
                .build();
        scheduler.scheduleJob(trigger);
        return"jobName";
    }

    //TODO 수정 및 테스트
    //트리거를 수정한다.
    public String updateTrigger(Run run) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        //JobKey jobKey=new JobKey(run.getJobName(),"Group");
        Optional<String> cronTab = triggerRepository.findCronExpression(run.getJobName());
        if (!cronTab.isPresent()) {
            log.error("crontab Expression Not Found !");
            return "crontab Expression Not Found !";
        }

        String cronStr=cronTab.get();
        cronStr = cronStr.replace("cron(", "").replace(")", "");
        Trigger oldTrigger = scheduler.getTrigger(TriggerKey.triggerKey("oldTrigger", "group1"));
        TriggerBuilder tb = oldTrigger.getTriggerBuilder();
        Trigger newTrigger = tb.withSchedule(CronScheduleBuilder.cronSchedule(cronStr))
                .build();
        scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
        return"jobName";
    }


    //job테이블에 삭제 하고 스케줄링이 걸려 있다면 스케줄링도 삭제 한다.
    public String deleteJob(Long jobId) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Optional<Job> job = jobRepository.findById(jobId);
        if (!job.isPresent()) {
            return "JOB_NOT_FOUND";
        }
        JobKey jobKey = new JobKey(job.get().getJobName(), "GROUP");
        scheduler.deleteJob(jobKey);
        return"jobName";
    }




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
