package com.lgdisplay.bigdata.api.glue.scheduler.jobs;

import com.lgdisplay.bigdata.api.glue.scheduler.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@DisallowConcurrentExecution
public class StartJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //Object bean = ApplicationContextHolder.get().getBean("");
        long startTime = System.currentTimeMillis();
        ProcessBuilder processBuilder = new ProcessBuilder();

        try {
            //job 실행시 glue 트리거 테이블 조회해서
            // action 필드에 있는 json을 읽어서 리스트의 job 항목을 실행한다.
            log.info(">>>>>>>>>>>>> 여기가 호출되면 된다  : "+context.getJobDetail());
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>start");
            Thread.sleep(10000);
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>end");
        } catch ( InterruptedException e) {
            throw new JobExecutionException(e);
        }

        long endTime = System.currentTimeMillis();
        log.info(">>>>>>>>>>>>> Running Job has been completed , cost time : "+(endTime - startTime)+"ms\n ");
    }

}