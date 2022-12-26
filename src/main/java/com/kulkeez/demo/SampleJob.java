package com.kulkeez.demo;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 
 * 
 * @author kulkeez
 *
 */
// Using the annotation below, marked this java class as a bean so the component-scanning mechanism 
// of spring can pick it up and pull it into the application context.
@Component
public class SampleJob implements Job {
 
	Logger logger = LoggerFactory.getLogger(getClass());
	
    @Autowired
    private SampleJobService sampleJobService;
 
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	logger.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());
    	sampleJobService.executeSampleJob();
        logger.info("Next job scheduled @ {}", context.getNextFireTime());
    }
}