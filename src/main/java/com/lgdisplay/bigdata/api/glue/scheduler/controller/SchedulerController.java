package com.lgdisplay.bigdata.api.glue.scheduler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgdisplay.bigdata.api.glue.scheduler.jobs.StartJob;
import com.lgdisplay.bigdata.api.glue.scheduler.model.*;
import com.lgdisplay.bigdata.api.glue.scheduler.model.Trigger;
import com.lgdisplay.bigdata.api.glue.scheduler.model.http.StartJobRunRequest;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.TriggerRepository;
import com.lgdisplay.bigdata.api.glue.scheduler.service.QuartzSchedulerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

/**
 * Job Scheduler Controller.
 */
@RestController
@RequestMapping("/service/scheduler")
@Api(value = "Scheduler", description = "Job Scheduler API")
@Slf4j
public class SchedulerController extends DefaultController {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    QuartzSchedulerService schedulerService;

    @Autowired
    @Qualifier("mapper")
    ObjectMapper mapper;

    /*완료*/
    @PostMapping("/job")
    @ApiOperation(value = "Job 등록", notes = "Job을 등록합니다.")
    public ResponseEntity add(HttpServletRequest request, @RequestBody Map params) throws SchedulerException {
        String jobName=params.get("jobName").toString();
        String userName=params.get("userName").toString();
        if (jobName==null) {
            log.warn("요청을 처리하기 위해서 필요한 jobId가 존재하지 않습니다.");
            return ResponseEntity.ok(_false());
        }
        schedulerService.addJob(userName, jobName);


        return ResponseEntity.ok(_true());
    }

    /*완료*/
    @PostMapping("/start/job")
    @ApiOperation(value = "Job 시작", notes = "Job을 한번만 실행합니다.")
    public ResponseEntity startJobRun(HttpServletRequest request,@RequestBody Map params) throws Exception  {

        String jobRunId=params.get("jobRunId").toString();
        StartJobRunRequest startJobRunRequest = mapper.readValue(params.get("body").toString(), StartJobRunRequest.class);

        Run startJobRun = Run.builder()
                .jobRunId(jobRunId)
                .jobName(startJobRunRequest.getJobName())
                .jobRunState(JobRunStateEnum.STARTING.name())
                .userName(params.get("userName").toString()).build();

        //TODO
        // 아래에서 JOB 실행시키고 STATE 코드 RUNNING FINISH 업데이트 시키는 로직추가
        try {

            schedulerService.startJobRun(startJobRun);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok(jobRunId);
    }

    /*완료*/
    @PostMapping("/trigger")
    @ApiOperation(value = "Trigger 등록", notes = "Trigger을 등록합니다.")
    public ResponseEntity addTrigger(HttpServletRequest request, @RequestBody Map<String,String> params) throws SchedulerException, JsonProcessingException {

        String triggerId=schedulerService.addTrigger(params);
        String jobRunId = "JOB_" + System.currentTimeMillis();
        Run startJobRun = Run.builder()
                .jobRunId(jobRunId)
                .jobName(params.get("jobName"))
                .jobRunState(JobRunStateEnum.RUNNING.name())
                .triggerId(triggerId)
                .triggerName(params.get("triggerName"))
                .userName(params.get("userName"))
                .build();

        return ResponseEntity.ok(_true());
    }

    @DeleteMapping("/trigger/{triggerId}")
    @ApiOperation(value = "Trigger 삭제", notes = "Trigger을 삭제합니다.")
    public ResponseEntity delTrigger(HttpServletRequest request,  @PathVariable("triggerId") String triggerId) throws SchedulerException, JsonProcessingException {
        schedulerService.deleteTrigger(triggerId);
        return ResponseEntity.ok(_true());
    }

    @PutMapping("/trigger/stop/{triggerId}")
    @ApiOperation(value = "Trigger 정지", notes = "Trigger을 정지합니다.")
    public ResponseEntity stopTrigger(HttpServletRequest request,  @PathVariable("triggerId") String triggerId) throws SchedulerException, JsonProcessingException {
        schedulerService.stopTrigger(triggerId);
        return ResponseEntity.ok(_true());
    }





    /*
     *job 수정은 glue table만 한다.
     * 수정컬럼이 존재하지 않음.
     * */
    @PostMapping("/job/update/{jobId}")
    @ApiOperation(value = "Job 수정", notes = "Job을 수정합니다.")
    public ResponseEntity update(HttpServletRequest request, @PathVariable("jobId") Long jobId) throws SchedulerException {
        return ResponseEntity.ok(_true());
    }

    /*
     * job에 해당하는 모든 스케중 중지
     */
    @DeleteMapping("/job/kill/{jobId}")
    @ApiOperation(value = "Job 강제 중지", notes = "실행중인 Job을 강제 중지합니다.")
    public ResponseEntity kill(HttpServletRequest request,   @PathVariable("jobId") Long jobId) {

        if (jobId==null) {
            log.warn("요청을 처리하기 위해서 필요한 jobId가 존재하지 않습니다.");
            return ResponseEntity.ok(_false());
        }
        String returnVal=schedulerService.killJob(jobId);
        if(returnVal.equals("JOB_NOT_FOUND")){
            return ResponseEntity.ok(_false());
        }

        return ResponseEntity.ok(_true());
    }

    @DeleteMapping("/job/{jobId}")
    @ApiOperation(value = "Job 삭제", notes = "등록되어 있는 Job을 삭제합니다.")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable("jobId") Long jobId) throws SchedulerException {

        if (jobId==null) {
            log.warn("요청을 처리하기 위해서 필요한 jobId가 존재하지 않습니다.");
            return ResponseEntity.ok(_false());
        }
        schedulerService.deleteJob(jobId);
        return ResponseEntity.ok(_true());
    }

    @GetMapping("/job/{jobId}")
    @ApiOperation(value = "Job 정보 확인", notes = "등록되어 있는 Job을 확인합니다.")
    public ResponseEntity get(HttpServletRequest request, @PathVariable("jobId") String jobId) {
        return ResponseEntity.ok(_true());
    }



    /*
     * job에 해당하는 모든 스케중 중지
     */
    @DeleteMapping("/Trigger/kill/{triggerId}")
    @ApiOperation(value = "Trigger 강제 중지", notes = "실행중인 Trigger을 강제 중지합니다.")
    public ResponseEntity killTrigger(HttpServletRequest request,   @PathVariable("jobId") Long jobId) {

        if (jobId==null) {
            log.warn("요청을 처리하기 위해서 필요한 triggerId가 존재하지 않습니다.");
            return ResponseEntity.ok(_false());
        }
        String returnVal=schedulerService.killJob(jobId);
        if(returnVal.equals("JOB_NOT_FOUND")){
            return ResponseEntity.ok(_false());
        }

        return ResponseEntity.ok(_true());
    }
}
