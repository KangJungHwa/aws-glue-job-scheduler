package com.lgdisplay.bigdata.api.glue.scheduler.service;

import com.lgdisplay.bigdata.api.glue.scheduler.model.ServerHealth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * API Server 및 Job Scheduler 서버의 리소스를 확인하여 리소스 사용을 결정하는 서비스.
 */
@Slf4j
@Service
public class ResourceService {

//    //String schServer="http://192.168.20.205:8889";
//    String schServer="http://localhost:8889";
//    @Value("${app.scheduler-urls}")
//    List<String> jobSchedulers;

    /**
     * API Server의 Health 정보
     */
    private ServerHealth apiServerHealth;

    /**
     * API Server의 Health 정보를 업데이트한다.
     *
     * @param apiServerHealth API Server의 Health
     */
    public void setApiServerHealth(ServerHealth apiServerHealth) {
        this.apiServerHealth = apiServerHealth;
    }

    /**
     * API Server의 Health 정보를 반환한다.
     */
    public ServerHealth getApiServerHealth() {
        return apiServerHealth;
    }


//    public String getJobStartUrl() {
//        return schServer+"/service/scheduler/start/job";
//    }
//    public String getJobUrl() {
//        return schServer+"/service/scheduler/job";
//    }
//    public String getTriggerUrl() {
//        return schServer+"/service/scheduler/trigger";
//    }

    public String copyPublicStorage(String userName)  {
        try {
            //TODO 배포시 아래경로 리눅스 스타일로 변경할 것
            String sourceDirectoryLocation = "C:/mnt/" + userName + "/Documents/";
            //TODO 아래부분의 경로는 확정되면 수정할 것
            String destinationDirectoryLocation = "C:/DEV/" + userName + "/Documents/";
            File sourceDirectory = new File(sourceDirectoryLocation);
            File destinationDirectory = new File(destinationDirectoryLocation);
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
            return "OK";
        } catch (IOException e) {
            log.error(e.toString());
            return "ERROR";
        }
    }
}
