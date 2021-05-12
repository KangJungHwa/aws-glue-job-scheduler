package com.lgdisplay.bigdata.api.glue.scheduler.jobs;

import com.lgdisplay.bigdata.api.glue.scheduler.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Slf4j
public class StartJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //Object bean = ApplicationContextHolder.get().getBean("");
        long startTime = System.currentTimeMillis();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~여기가 호출되면 된다.~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");


        long endTime = System.currentTimeMillis();
        log.info(">>>>>>>>>>>>> Running Job has been completed , cost time : "+(endTime - startTime)+"ms\n ");
    }

}