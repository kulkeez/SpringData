package com.kulkeez.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

/**
 * 
 * The @Service annotation doesn’t currently provide any additional behavior over the @Component annotation, 
 * but it’s a good idea to use @Service over @Component in service-layer classes because 
 * it specifies intent better. 
 * 
 * @author kulkarvi
 *
 */
// The @Service annotation is also a specialization of the component annotation
// and marks this java class as a bean so that the component-scanning mechanism of Spring can pick it up 
// and pull it into the application context.
@Service
public class SampleJobService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void executeSampleJob() {
        try {
        	logger.info("Running Database health check...");
            Thread.sleep(5000);
        } 
        catch (InterruptedException e) {
            logger.error("Error while executing sample job", e);
        } 
        finally {
            logger.info("Scheduled Database health check finished.");
        }
    }
}