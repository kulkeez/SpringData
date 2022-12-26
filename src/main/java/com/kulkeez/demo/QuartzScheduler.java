package com.kulkeez.demo;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.JobBuilder.*;

import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Non-Spring way of using Quartz
 *
 */
public class QuartzScheduler {
	
	private static final Logger log = LoggerFactory.getLogger(QuartzScheduler.class);
	
	private static String[] daysStringArray = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

	private static QuartzScheduler quartzScheduler = null ;
	
	private Scheduler scheduler = null;
	
	/**
	 * Default constructor
	 * 
	 */
	private QuartzScheduler() {
	}
	
	/**
	 * @return
	 */
	public static QuartzScheduler getInstance(){
		if(quartzScheduler == null) {
			quartzScheduler = new QuartzScheduler();
		}
		
		return quartzScheduler;
	}
	
	/**
	 * Start the Quartz scheduler
	 * 
	 * @throws SchedulerException
	 */
	public void startScheduler() throws SchedulerException {
		StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
		scheduler = schedulerFactory.getScheduler();
		
		if(!scheduler.isStarted()){
			log.debug("Quartz Scheduler " + scheduler.getSchedulerName() + " is starting ...");
			scheduler.start();
		}
	}
	
	/**
	 * Stop the Quartz scheduler
	 * 
	 * @throws SchedulerException
	 */
	public void stopScheduler() throws SchedulerException {
		if(scheduler != null && scheduler.isStarted()) {
			log.debug("Quartz Scheduler " + scheduler.getSchedulerName() + " is shutting down ...");
			scheduler.shutdown();
		}
	}

	
	
	/**
	 * To schedule printing of Random Thoughts where a
	 * Promotional Event is occurring every few minutes as per the frequency parameter
	 * 
	 * @param frequency
	 * @throws SchedulerException
	 */
	public void scheduleThoughtEventJob(int frequency, String eventName, String randomThought) throws SchedulerException {
		String scheduleName ="ThoughtsEventJob";
		JobKey jobKey = new JobKey(scheduleName, "Group1");
		
		log.debug("Configuring JobDetails for Thought Job...");
		
		// define the job and tie it to our QuartzJob class
		JobDetail job = newJob(ThoughtsJob.class).withIdentity(jobKey)
				.usingJobData("EVENT_NAME", eventName)
				.usingJobData("RANDOM_THOUGHT", randomThought)
				.requestRecovery(true).build();

		log.debug("Triggering the Thought Job to run now, and then repeat every 'frequency' minutes..");
		
        // Trigger the job to run now, and then repeat every 'frequency' minutes
        Trigger promotionalEventTrigger = newTrigger()
            .withIdentity("promoEventsTrigger", "wifi")
            .startNow()
            .withSchedule(simpleSchedule()
                    .withIntervalInMinutes(frequency)
                    .repeatForever())
            .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, promotionalEventTrigger);		
	}
	

	/**
	 * Schedule a Quartz Cron Job given a cron Expression
	 * Use this for unit testing 
	 * 
	 * @param cronExpression use this String which is a valid cron expression 
	 * 
	 * @throws SchedulerException
	 */
	public void scheduleCronJob(String cronExpression, String randomThought) throws SchedulerException {
		
		String scheduleName ="Quartz Cron Job - Thoughts";
		JobKey jobKey = new JobKey(scheduleName, "Group1");
				
		JobDetail jobDetails = JobBuilder.newJob(ThoughtsJob.class).withIdentity(jobKey)
										.usingJobData("RANDOM_THOUGHT", randomThought)
										.requestRecovery(true).build();
		
		TriggerKey tKey = new TriggerKey(scheduleName, "Group1");
		log.debug("cronExpression = " + cronExpression);
		
		TriggerBuilder<CronTrigger> triggerBuilder = newTrigger()
				.withIdentity(tKey)
				.withSchedule( cronSchedule(cronExpression));

		CronTrigger cronTrigger = triggerBuilder.build();
		
		// Tell quartz to schedule the job using our trigger
		scheduler.scheduleJob(jobDetails, cronTrigger);
		log.debug("Scheduled Quartz Job for cronTrigger = " + cronTrigger);
	}
	
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
        	
			log.debug("Grabbing the Quartz Scheduler instance...");
			
            // Grab the Scheduler instance from the Factory
            // Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            QuartzScheduler scheduler = QuartzScheduler.getInstance();
            
            // and start it off
            scheduler.startScheduler();
            log.debug("Quartz Scheduler started.");
    		
            //scheduled to run every one minutes but only between 8am and 11pm
            scheduler.scheduleCronJob("0 0/1 8-23 * * ?", "The pain you feel today is the strength you feel tomorrow.");
            //scheduler.scheduleThoughtEventJob(1, "testEvent");
            
            //scheduler.shutdown();
            //logger.debug("Quartz Scheduler stopped.");
            
            log.info("Do notice that the main thread that launched the Quartz scheduler is now exiting.");
        } 
        catch (SchedulerException se) {
            se.printStackTrace();
        }
    }
	

	/**
	 * For Unit testing 
	 * 
	 * @throws SchedulerException
	 */
	private void test() throws SchedulerException {
		// define the job and tie it to our QuartzJob class
		JobDetail job = newJob(ThoughtsJob.class)
			.withIdentity("job1", "group1")
            .build();

        // Trigger the job to run now, and then repeat every 5 minutes
        Trigger thoughtTrigger = newTrigger()
            .withIdentity("trigger1", "group1")
            .startNow()
            .withSchedule(simpleSchedule()
                    .withIntervalInMinutes(5)
                    .repeatForever())
            .build();
        
        // Trigger the job to run now, and then repeat every 1 hour
        Trigger thoughtTrigger2 = newTrigger()
            .withIdentity("trigger2", "group1")
            .startNow()
            .withSchedule(simpleSchedule()
                    .withIntervalInHours(1)
                    .repeatForever())
            .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, thoughtTrigger);
        scheduler.scheduleJob(job, thoughtTrigger2);
	}
}
