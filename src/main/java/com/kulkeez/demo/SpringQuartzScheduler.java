package com.kulkeez.demo;

import javax.annotation.PostConstruct;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * 
 * 
 * @author kulkarvi
 *
 */
public class SpringQuartzScheduler {
	
	Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        logger.info("SpringQuartzScheduler initialized.");
    }

    
    /**
     * We add auto-wiring support to SpringBeanJobFactory
     * 
     * @return
     */
    @Bean
    public SpringBeanJobFactory springBeanQuartzJobFactory() {
    	logger.debug("Configuring Quartz Job factory for auto-wiring support...");
    	AutoWiringSpringBeanJobFactory quartzJobFactory = new AutoWiringSpringBeanJobFactory();
       
    	quartzJobFactory.setApplicationContext(applicationContext);
    	return quartzJobFactory;
    }

    
    /**
     * 
     * Spring way for configuring a Scheduler
     * This method manages its life-cycle within the application context, and exposes the Scheduler as a bean 
     * for dependency injection
     * 
     * @param trigger
     * @param job
     * @return
     */
    @Bean
    public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

        logger.debug("Setting the Quartz Scheduler up");
        schedulerFactory.setJobFactory(springBeanQuartzJobFactory());
        schedulerFactory.setJobDetails(job);
        schedulerFactory.setTriggers(trigger);

        return schedulerFactory;
    }
    
	
    @Bean
    public JobDetailFactoryBean jobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        
        // TODO: make it more generic by passing Job name
        jobDetailFactory.setJobClass(SampleJob.class);
        jobDetailFactory.setName("Qrtz_Job_Detail");
        jobDetailFactory.setDescription("Invoke Sample Job service...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean trigger(JobDetail job) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);

        int frequencyInSec = 10;
        logger.info("Configuring trigger to fire every {} seconds", frequencyInSec);

        trigger.setRepeatInterval(frequencyInSec * 1000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setName("Qrtz_Trigger");
        return trigger;
    }

}