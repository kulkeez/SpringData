package com.kulkeez.demo;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
   


/**
 * The Quartz Job to print interesting thoughts. This job is is triggered by 
 * the associated Quartz Trigger
 *
 */
@Component
public class ThoughtsJob implements Job {
	protected static ResourceBundle propBundle = ResourceBundle.getBundle("application");
	
	private static final Logger logger = LoggerFactory.getLogger(ThoughtsJob.class);
	
	@Override
	public void execute(JobExecutionContext jeContext) throws JobExecutionException {
		JobDataMap jobData = jeContext.getJobDetail().getJobDataMap();
		String randomThought = (String) jobData.get("RANDOM_THOUGHT");
		
		logger.info("Thought: " + randomThought.toUpperCase());
		 
	}

	
	
	/**
	 * To get a random integer between the specified range
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private static int getRandomInteger(int start, int end) {
		logger.debug("start = " + start + ", end = " + end);
		Random random = new Random();
	    if (start > end) {
	    	throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    
	    //get the range, casting to long to avoid overflow problems
	    long range = (long)end - (long)start + 1;
	    
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * random.nextDouble());
	    int randomNumber =  (int)(fraction + start);    
	    logger.debug("randomNumber = " + randomNumber);
	    
	    return randomNumber;
	}	
			
}
