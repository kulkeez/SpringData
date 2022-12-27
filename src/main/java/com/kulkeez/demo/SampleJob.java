package com.kulkeez.demo;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kulkeez.demo.service.SampleJobService;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 * 
 * @author kulkeez
 *
 */
// Using the annotation below, marked this java class as a bean so the component-scanning mechanism 
// of spring can pick it up and pull it into the application context.
@Component
@Slf4j
public class SampleJob implements Job {
 
    @Autowired
    private SampleJobService sampleJobService;
 
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	log.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());
    	sampleJobService.executeSampleJob();
        log.info("Next job scheduled @ {}", context.getNextFireTime());
    }
}