package com.lgdisplay.bigdata.api.glue.scheduler.service;

import com.lgdisplay.bigdata.api.glue.scheduler.model.ActionLogging;
import com.lgdisplay.bigdata.api.glue.scheduler.repository.ActionLoggingRepository;
import com.lgdisplay.bigdata.api.glue.scheduler.util.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActionLoggingService {

    @Autowired
    ActionLoggingRepository repository;

    public void startUsage(ActionLogging logging) {
        repository.save(logging);
    }

    public void endUsage(ActionLogging logging, Exception e) {
        logging.setMessage(ExceptionUtils.getFullStackTrace(e));
        repository.save(logging);
    }

}