package com.lgdisplay.bigdata.api.glue.scheduler.controller;

import com.lgdisplay.bigdata.api.glue.scheduler.service.QuartzSchedulerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    QuartzSchedulerService schedulerService;

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
