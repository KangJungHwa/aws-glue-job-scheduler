package com.lgdisplay.bigdata.api.glue.scheduler.config;

import com.lgdisplay.bigdata.api.glue.scheduler.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/*
이클래스에 의해 job을 구현한 클래스인 startJob이 빈으로 등록되고
job instance를 생성한다.

 */
@Slf4j
public final class AutoWiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

    private transient AutowireCapableBeanFactory beanFactory;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + job);
        return job;
    }

}