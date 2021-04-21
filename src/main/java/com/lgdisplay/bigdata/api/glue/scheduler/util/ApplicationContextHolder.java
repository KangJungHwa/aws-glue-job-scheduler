package com.lgdisplay.bigdata.api.glue.scheduler.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Application Context의 Singleton.
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    public static ApplicationContext applicationCont;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationCont = applicationContext;
    }

    public static ApplicationContext get() {
        return applicationCont;
    }

}
