package com.kulkeez.demo.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class SampleJobService {

    public void executeSampleJob() {
        try {
        	log.info("Running Database health check...");
            Thread.sleep(5000);
        } 
        catch (InterruptedException e) {
            log.error("Error while executing sample job", e);
        } 
        finally {
            log.info("Scheduled Database health check finished.");
        }
    }
}