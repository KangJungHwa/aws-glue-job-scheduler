package com.lgdisplay.bigdata.api.glue.scheduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgdisplay.bigdata.api.glue.scheduler.jobs.StartJob;
import com.lgdisplay.bigdata.api.glue.scheduler.model.*;

import com.lgdisplay.bigdata.api.glue.scheduler.model.Job;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.JobRepository;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.RunRepository;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.TriggerRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    @Autowired
    @Qualifier("mapper")
    ObjectMapper mapper;

    public String getTriggerType(Run run){
        Optional<com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger> optTrigger
                =triggerRepository.findByName(run.getTriggerName());
        com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger trigger= optTrigger.get();
        return trigger.getType();
    }

    public String  saveRun(Run run) {
        runRepository.save(run);
        return run.getJobRunId();
    }
    //1 triggerRepository를 뒤져서 jobName을 가져온다.
    //2 loop를 돌려서 저장한다.
    public void  saveMultiRun(Run run) throws Exception{
        List<Job> jobList=jobRepository.findJobNameByTriggerNameParamsNative(run.getTriggerName());
        for (Job job:jobList) {
            String jobRunId = "JOB_" + System.currentTimeMillis();
            run.setJobName(job.getJobName());
            run.setJobRunId(jobRunId);
            runRepository.save(run);
        }
    }


    public void  updateRunStatus(Run run,String Status) {
        run.setJobRunState(JobRunStateEnum.valueOf(Status).name());
        runRepository.save(run);
    }
    public  void  updateTriggerStatus(Run run,String Status) {
        Optional<com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger> optTrigger
        =triggerRepository.findByName(run.getTriggerName());
        com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger trigger= optTrigger.get();
        trigger.setTriggerState(TriggerStateEnum.valueOf(Status).name());
        triggerRepository.save(trigger);
    }
    public String startJobRun(Run run) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(run.getJobName(), run.getUserName())
                .startNow()
                .forJob(JobKey.jobKey(run.getJobName(), run.getUserName()))
                .build();

        scheduler.scheduleJob(trigger);
        return run.getJobRunId();
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

        /*
        GLUE에는 JOB GROUP 개념이 없고
        사용자별로 동일한 JOB을 가질수 있어야 하기때문에
        QUARTZ의 JOB 테이블의 KEY가 JOBNAME,GROUP이기 때문에
        GROUP에 사용자 명을 넣음
        */
        Job jobInfo=job.get();
        JobKey jobKey = new JobKey(jobName, jobInfo.getUsername());
        JobDetail jobDetail = JobBuilder
                .newJob(StartJob.class)
                .withIdentity(jobKey)
                .storeDurably()
                .build();

        scheduler.addJob(jobDetail,false);
        return"jobName";
    }

    // 저장된 job의 트리거를 등록한다.
    public String addTrigger(Map<String,String> paramMap) throws SchedulerException, JsonProcessingException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Optional<com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger> trigger
                = triggerRepository.findByName(paramMap.get("triggerName"));
        if (!trigger.isPresent()) {
            log.error("Trigger Not Found !");
            return "FAIL";
        }
        Optional<Run> run = runRepository.findByTriggerId(trigger.get().getTriggerId());
        if (run.isPresent()) {
            if (run.get().getJobRunState().equals("RUNNING")) {
                log.error("Trigger is already running !");
                return "FAIL";
            }
        }
        String type=trigger.get().getType();
        List<Job> jobList=jobRepository.findJobNameByTriggerNameParamsNative(paramMap.get("triggerName"));
            JobKey jobKey = new JobKey(jobList.get(0).getJobName(), jobList.get(0).getUsername());
            if (type.equals(TriggerTypeEnum.SCHEDULED.name())) {
                createCronTrigger(paramMap, scheduler, trigger, jobKey);
            }else{
                createOneTimeTrigger(paramMap, scheduler, jobKey);
            }
        return trigger.get().getTriggerId();
    }

    //ON_DEMAND TRIGGER 생성
    private void createOneTimeTrigger(Map<String, String> paramMap, Scheduler scheduler, JobKey jobKey)
            throws SchedulerException {
        Trigger qurtzTrigger = TriggerBuilder.newTrigger()
                .withIdentity(paramMap.get("triggerName"), paramMap.get("userName").toUpperCase())
                .startNow()
                .forJob(jobKey)
                .build();
        scheduler.scheduleJob(qurtzTrigger);
    }

    //CRON TAB TRIGGER 생성
    private void createCronTrigger(Map<String, String> paramMap,
                                   Scheduler scheduler,
                                   Optional<com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger> trigger,
                                   JobKey jobKey) throws SchedulerException {
        String cronStr=trigger.get().getSchedule();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(paramMap.get("triggerName"),paramMap.get("userName").toUpperCase())
                .withSchedule(CronScheduleBuilder.cronSchedule(cronStr))
                .forJob(jobKey)
                .build();
        scheduler.scheduleJob(cronTrigger);
    }


    public String stopTrigger(String triggerId) throws SchedulerException, JsonProcessingException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            Optional<com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger> triggerById
                    = triggerRepository.findById(triggerId);
            if (!triggerById.isPresent()) {
                return "FAIL";
            }
            com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger trigger=triggerById.get();
            scheduler.unscheduleJob(TriggerKey.triggerKey(trigger.getName(), trigger.getUserName()));
            Optional<Run> runByTriggerId = runRepository.findByTriggerId(triggerId);
            Run run = runByTriggerId.get();
            run.setJobRunState(JobRunStateEnum.STOPPED.name());
            runRepository.save(run);
            trigger.setTriggerState(TriggerStateEnum.STOPPED.name());
            triggerRepository.save(trigger);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return"jobName";
    }

    public String deleteTrigger(String triggerId) throws SchedulerException, JsonProcessingException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {

            Optional<com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger> triggerById
                    = triggerRepository.findById(triggerId);
            if (!triggerById.isPresent()) {
                return "FAIL";
            }
            com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger trigger=triggerById.get();
            if(trigger.getTriggerState().equals(TriggerStateEnum.RUNNING.name())) {
                return "FAIL";
            }

            scheduler.unscheduleJob(TriggerKey.triggerKey(trigger.getName(), trigger.getUserName()));
            Optional<Run> runByTriggerId = runRepository.findByTriggerId(triggerId);
            triggerRepository.delete(trigger);
            if(!trigger.getTriggerState().equals(TriggerStateEnum.STANDBY.name())) {
                Run run = runByTriggerId.get();
                updateRunStatus(run, JobRunStateEnum.DELETED.name());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
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

    public com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger getTrigger(String TriggerId) {
        Optional<com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger> byId = triggerRepository.findById(TriggerId);
        com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger trigger = byId.get();
        return trigger;
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
