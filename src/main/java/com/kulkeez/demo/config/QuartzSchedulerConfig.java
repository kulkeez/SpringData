package com.kulkeez.demo.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import com.kulkeez.demo.SampleJob;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuring the Quartz beans using Spring
 * 
 * @author kulkeez
 *
 */
@Configuration
@Slf4j
@ConditionalOnProperty(name = "quartz.enabled")
public class QuartzSchedulerConfig {
	
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
    	log.info("Injecting & configuring Quartz Job factory for auto-wiring support...");
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }
	
    @Bean
    public Scheduler schedulerFactoryBean(DataSource dataSource, JobFactory jobFactory,
                                          @Qualifier("sampleJobTrigger") Trigger sampleJobTrigger) throws Exception {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        // this allows to update triggers in DB when updating settings in config file:
        schedulerFactory.setOverwriteExistingJobs(true);
        schedulerFactory.setDataSource(dataSource);
        schedulerFactory.setJobFactory(jobFactory);

        schedulerFactory.setQuartzProperties(quartzProperties());
        schedulerFactory.afterPropertiesSet();
        log.info("Configured Quartz Scheduler factory bean.");
        
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.setJobFactory(jobFactory);
        
//        scheduler.scheduleJob((JobDetail) sampleJobTrigger.getJobDataMap().get("jobDetail"), sampleJobTrigger);
       
        scheduler.start();
        log.info("Quartz Scheduler injected via Spring and started!!!");
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean(name = "sampleJobDetail")
    public JobDetailFactoryBean sampleJobDetail() {
    	log.debug("Creating SampleJob details...");
        return createJobDetail(SampleJob.class);
    }

    @Bean(name = "sampleJobTrigger")
    public SimpleTriggerFactoryBean sampleJobTrigger(@Qualifier("sampleJobDetail") JobDetail jobDetail,
                                                     @Value("${samplejob.frequency}") long frequency) {
    	log.debug("Creating SampleJob Trigger...");
        return createTrigger(jobDetail, frequency);
    }

    @Bean(name = "sampleCronJobTrigger")
    public CronTriggerFactoryBean sampleCronJobTrigger(@Qualifier("sampleJobDetail") JobDetail jobDetail,
                                                     @Value("${samplejob.cron.expression}") String cronExpression) {
    	log.debug("Creating SampleCronJob Trigger...");
        return createCronTrigger(jobDetail, cronExpression);
    }
    
    /**
     * 
     * @param jobClass
     * @return
     */
    private static JobDetailFactoryBean createJobDetail(Class jobClass) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        
        // job has to be durable to be stored in DB
        factoryBean.setDurability(true);
        return factoryBean;
    }

    
    /**
     * 
     * @param jobDetail
     * @param pollFrequencyMs
     * @return
     */
    private static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFrequencyMs) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        log.debug("Creating Trigger for job: " + jobDetail);
        
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setStartDelay(0L);
        factoryBean.setRepeatInterval(pollFrequencyMs);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        
        // in case of misfire, ignore all missed triggers and continue
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
        return factoryBean;
    }

    
    /**
     * 
     * Use this method for creating cron triggers instead of simple triggers
     * 
     * @param jobDetail
     * @param cronExpression
     * @return
     */
    private static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        log.debug("Creating Cron Trigger using cron expression: " + cronExpression + " for job: " + jobDetail);
        
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        return factoryBean;
    }
}
