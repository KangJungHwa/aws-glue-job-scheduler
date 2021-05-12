package com.lgdisplay.bigdata.api.glue.scheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgdisplay.bigdata.api.glue.scheduler.jobs.StartJob;
import com.lgdisplay.bigdata.api.glue.scheduler.model.Run;
import com.lgdisplay.bigdata.api.glue.scheduler.model.http.StartJobRunRequest;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.RunRepository;
import com.lgdisplay.bigdata.api.glue.scheduler.service.QuartzSchedulerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Job Scheduler Controller.
 */
@RestController
@RequestMapping("/service/scheduler")
@Api(value = "Scheduler", description = "Job Scheduler API")
@Slf4j
public class SchedulerController extends DefaultController {

    @Autowired
    RunRepository runRepository;

    @Autowired
    QuartzSchedulerService schedulerService;

    @Autowired
    @Qualifier("mapper")
    ObjectMapper mapper;

    @PostMapping("/startJobRun")
    public ResponseEntity startJobRun(@RequestBody Map params) throws Exception  {
        log.info("{}", params);
        String jobRunId=params.get("jobRunId").toString();
        StartJobRunRequest startJobRunRequest = mapper.readValue(params.get("body").toString(), StartJobRunRequest.class);

        Run startJobRun = Run.builder()
                .jobRunId(jobRunId)
                .jobName(startJobRunRequest.getJobName())
                .arguments(params.get("arguments").toString())
                .jobRunState("START")
                .body(params.get("body").toString()).build();
        runRepository.save(startJobRun);
        //TODO
        // 아래에서 JOB 실행시키고 STATE 코드 RUNNING FINISH 업데이트 시키는 로직추가
        JobKey jobKey = new JobKey("startJob", "Groupe1");
        JobDetail jobDetail = JobBuilder.newJob(StartJob.class).withIdentity(jobKey).build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.clear();
        scheduler.start();

        //TODO
        // 아래의 Job Name과 group 스케줄 가져오는 메소드를 서비스 클래스에서 정의 한다.
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("startJob", "Groupe1")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/3 * * * * ?"))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);


        return ResponseEntity.ok(jobRunId);
    }




    @PostMapping("/job")
    @ApiOperation(value = "Job 등록", notes = "Job을 등록합니다.")
    public ResponseEntity regist(HttpServletRequest request, @RequestBody Map<String, String> params) {
        String userName = getValue(params, "userName", "");

        if (!StringUtils.hasLength(userName)) {
            log.warn("요청을 처리하기 위해서 필요한 UserName이 존재하지 않습니다.");
            return ResponseEntity.ok(_false());
        }

        return ResponseEntity.ok(_true());
    }

    @DeleteMapping("/job/{jobId}")
    @ApiOperation(value = "Job 삭제", notes = "등록되어 있는 Job을 삭제합니다.")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable("jobId") String jobId) {
        return ResponseEntity.ok(_true());
    }

    @GetMapping("/job/{jobId}")
    @ApiOperation(value = "Job 정보 확인", notes = "등록되어 있는 Job을 확인합니다.")
    public ResponseEntity get(HttpServletRequest request, @PathVariable("jobId") String jobId) {
        return ResponseEntity.ok(_true());
    }


    @DeleteMapping("/job/kill/{jobId}")
    @ApiOperation(value = "Job 강제 중지", notes = "실행중인 Job을 강제 중지합니다.")
    public ResponseEntity kill(HttpServletRequest request, @PathVariable("jobId") String jobId) {
        return ResponseEntity.ok(_true());
    }

}
