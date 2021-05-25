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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        //Object bean = ApplicationContextHolder.get().getBean("");
        long startTime = System.currentTimeMillis();
        ProcessBuilder processBuilder = new ProcessBuilder();

        try {
            //job 실행시 glue 트리거 테이블 조회해서
            // action 필드에 있는 json을 읽어서 리스트의 job 항목을 실행한다.
            //아래의 key를 이용해서 트리거에 포함된 job을 조회하여 호출한다.
            //jobRun도 여기서 insert한다.

            String triggerName=context.getTrigger().getKey().getName();
            List<com.lgdisplay.bigdata.api.glue.scheduler.model.Job> jobList
                    = jobRepository.findJobNameByTriggerNameParamsNative(triggerName);

            Optional<com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger> trigger
                    = triggerRepository.findByName(triggerName);

            for (com.lgdisplay.bigdata.api.glue.scheduler.model.Job job:jobList) {

                String jobRunId = "JOB_" + System.currentTimeMillis();

                Run startJobRun = Run.builder()
                        .jobRunId(jobRunId)
                        .jobName(job.getJobName())
                        .jobRunState(JobRunStateEnum.RUNNING.name())
                        .triggerId(trigger.get().getTriggerId())
                        .triggerName(triggerName)
                        .userName(job.getUsername())
                        .build();
                Thread.sleep(3000);
                schedulerService.saveRun(startJobRun);
                //여기에서 실행 로직이 들어감
                Long pid=0L;
                try {
                    //스크립트 명 또는 jobName으로 명명 규칙을 통해 python R matlap을 구분한다.
                    //아래 명령어에 script명과 scriptLocation을 입력한다.

                    String jobName=job.getJobName();
                    String[] arrPrefix = jobName.split("_");
                    String strPrefix=arrPrefix[0];
                    ProcessBuilder pb=null;
                    //여기에 DEV 대신에 공용 저장소로 변경해 준다.
                    String path="/DEV" + job.getScriptLocation();
                    switch (strPrefix){
                        case "PY":
                             pb = new ProcessBuilder("python",path+job.getScriptName());
                            break;
                        case "R":
                             pb = new ProcessBuilder("R",path+job.getScriptName());
                            break;
                        case "MAT":
                            pb = new ProcessBuilder("MATRAP",path+job.getScriptName());
                            break;
                        default:
                            log.error("Invalid Script Name");
                            break;
                    }

                    Process p = pb.start();
                    pid = p.pid();

                    InputStream inputStream = p.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"euc-kr"));
                    String line=null;
                    if((line = br.readLine()) != null){
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    startJobRun.setJobRunState(JobRunStateEnum.FAILED.name());
                    startJobRun.setPid(pid);
                    schedulerService.saveRun(startJobRun);
                       //에러가 발생하면 run에 fail을 입력하고
                }finally {
                    //여기에서 run에 success를 입력함.
                    startJobRun.setJobRunState(JobRunStateEnum.SUCCEEDED.name());
                    startJobRun.setPid(pid);
                    schedulerService.saveRun(startJobRun);

                }
            }
            Thread.sleep(10000);
        } catch ( InterruptedException e) {
            throw new JobExecutionException(e);
        }
        long endTime = System.currentTimeMillis();
        log.info(">>>>>>>>>>>>> Running Job has been completed , cost time : "+(endTime - startTime)+"ms\n ");
    }

}