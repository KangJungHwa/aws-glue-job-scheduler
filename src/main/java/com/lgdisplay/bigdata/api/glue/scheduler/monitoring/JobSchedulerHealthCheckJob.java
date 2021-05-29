package com.lgdisplay.bigdata.api.glue.scheduler.monitoring;

import com.lgdisplay.bigdata.api.glue.scheduler.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Job Scheduler의 리소스를 주기적으로 체크하는 Quartz Job.
 */
@Slf4j
public class JobSchedulerHealthCheckJob implements Job {


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
//        jobSchedulers.forEach(server -> {
//            restTemplate.getForObject(server, Health.class)
//        });
    }

}