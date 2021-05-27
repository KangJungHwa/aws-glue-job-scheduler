package com.lgdisplay.bigdata.api.glue.scheduler.jobs;

import com.lgdisplay.bigdata.api.glue.scheduler.model.JobRunStateEnum;
import com.lgdisplay.bigdata.api.glue.scheduler.model.Run;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.JobRepository;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.TriggerRepository;
import com.lgdisplay.bigdata.api.glue.scheduler.service.QuartzSchedulerService;
import com.lgdisplay.bigdata.api.glue.scheduler.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@DisallowConcurrentExecution
public class StartJob implements Job {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    TriggerRepository triggerRepository;

    @Autowired
    QuartzSchedulerService schedulerService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        //job 실행시 glue 트리거 테이블 조회해서
        // action 필드에 있는 json을 읽어서 리스트의 job 항목을 실행한다.
        //아래의 key를 이용해서 트리거에 포함된 job을 조회하여 호출한다.
        //jobRun도 여기서 insert한다.

        String triggerName=context.getTrigger().getKey().getName();
        log.info("~~~~~~~~~~~~~~triggerName~~~~~~~~~~~~~~"+triggerName);
        try {

            List<com.lgdisplay.bigdata.api.glue.scheduler.model.Job> jobList
                    = jobRepository.findJobNameByTriggerNameParamsNative(triggerName);

            Optional<com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger> trigger
                    = triggerRepository.findByName(triggerName);
            if(jobList.size()==0){
                Optional<com.lgdisplay.bigdata.api.glue.scheduler.model.Job> optionalJob
                        = jobRepository.findByJobName(triggerName);
                if(optionalJob.isPresent()) {
                    executeTriggerJob(null, null,optionalJob.get());
                }
            }
            for (com.lgdisplay.bigdata.api.glue.scheduler.model.Job job:jobList) {

                executeTriggerJob(triggerName, trigger, job);

            }
         } catch ( InterruptedException e) {
            throw new JobExecutionException(e);
        }
    }

    private void executeTriggerJob(String triggerName,
                                   Optional<com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger> trigger,
                                   com.lgdisplay.bigdata.api.glue.scheduler.model.Job job)
                                   throws InterruptedException {
        String jobRunId = "JOB_" + System.currentTimeMillis();
        Run startJobRun=null;
        if(triggerName==null) {
             startJobRun = Run.builder()
                    .jobRunId(jobRunId)
                    .jobName(job.getJobName())
                    .jobRunState(JobRunStateEnum.STARTING.name())
                    .userName(job.getUsername())
                    .build();
        }else{
             startJobRun = Run.builder()
                    .jobRunId(jobRunId)
                    .jobName(job.getJobName())
                    .jobRunState(JobRunStateEnum.STARTING.name())
                    .triggerId(trigger.get().getTriggerId())
                    .triggerName(triggerName)
                    .userName(job.getUsername())
                    .build();
        }

        schedulerService.saveRun(startJobRun);
        //여기에서 실행 로직이 들어감
        Long pid=0L;
        try {
            //스크립트 명 또는 jobName으로 명명 규칙을 통해 python R matlap을 구분한다.
            //아래 명령어에 script명과 scriptLocation을 입력한다.

            String jobType=job.getScriptName();
            ProcessBuilder pb=null;
            //트리거는 여러 사용자의 job을 조합해서 만들수 있으므로 job의 경로를 사용한다.
            //TODO 여기에 DEV 대신에 공용 저장소로 변경해 준다.
            String scriptLocation="C:/DEV/" + job.getUsername() +"/Documents/"+ job.getScriptLocation();
            boolean isExists=fileExistsCheck(scriptLocation);
            if(!isExists){
                log.error(" 스크립트 경로 : " + scriptLocation +" : File Not Found");
                throw new Exception("File Not Found!");
            }
            log.info(" 스크립트 경로 : " + scriptLocation);
            // JOB 시작전에 SAVE
            startJobRun.setJobRunState(JobRunStateEnum.RUNNING.name());
            schedulerService.saveRun(startJobRun);
                switch (jobType) {
                    case "PYTHON":
                        pb = new ProcessBuilder("python", scriptLocation);
                        break;
                    case "R":
                        pb = new ProcessBuilder("R", scriptLocation);
                        break;
                    case "MATLAB":
                        pb = new ProcessBuilder("matlab", scriptLocation);
                        break;
                    default:
                        log.error("Invalid Script Type");
                        break;
                }

                Process p = pb.start();
                pid = p.pid();
                //pid 저장
                startJobRun.setPid(pid);
                schedulerService.saveRun(startJobRun);

                InputStream inputStream = p.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "euc-kr"));
                String line = null;
                if ((line = br.readLine()) != null) {
                    log.info("~~~~~~~~~~~~~~스크립트 실행 내역~~~~~~~~~~~~~~~~" + line);
                }
        } catch (Exception e) {
            //에러가 발생하면 run에 fail을 입력
            startJobRun.setJobRunState(JobRunStateEnum.FAILED.name());
            schedulerService.saveRun(startJobRun);
        }
        //catch에 걸리지 않으며 Success를 입력해 준다.
        //종료후 SUCCEEDED 입력
        startJobRun.setJobRunState(JobRunStateEnum.SUCCEEDED.name());
        schedulerService.saveRun(startJobRun);
    }

    public boolean fileExistsCheck(String location) {
        File f = new File(location);
        if (!f.exists()) {
            return false;
        }
        return true;
    }

}