package com.kulkeez.demo;

import java.io.File;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import lombok.extern.slf4j.Slf4j;

/*
 * This class is notified when this web application is loaded by the servlet container
 * or when it is destroyed.
 * 
 * @author kulkeez
 * 
 */
@WebListener
@Slf4j
public final class SpringDataAppListener implements ServletContextListener {

	protected static Scheduler scheduler = null;
	
	private static final String APPLICATION_NAME = "SpringData";

    
    /**
     * The servlet context with which we are associated.
     */
    private ServletContext servletContext = null;

    /**
     * Record the fact that this web application has been initialized.
     *
     * @param event The servlet context event
     */
    public void contextInitialized(ServletContextEvent event) {

        servletContext = event.getServletContext();
        servletContext.setAttribute("APP_NAME", APPLICATION_NAME);
                
        try {
        	log.info("===============================================================================");
            log.info("File Separator = " + System.getProperty("file.separator"));
            log.info("Temp Directory = " +  new File(System.getProperty("java.io.tmpdir")).getPath());
            log.info("Web Application Root Directory = " + servletContext.getRealPath(File.separator));
            log.info("User Home Directory = " +  new File(System.getProperty("user.home")).getPath());
            log.info("Application Server = " + servletContext.getServerInfo());
            
            if(servletContext.getServerInfo().contains("JBoss")) {
                log.info("JBoss AS Base Directory = " + System.getProperty("jboss.server.base.dir"));
            }
            
            log.info("Operating System = " + System.getProperty("os.name"));
            log.info("Operating System Version = " + System.getProperty("os.version"));
            log.info("Operating System CPU Architecture = " + System.getProperty("os.arch"));
        	log.info("===============================================================================");
            
            //System.getProperties().list(System.out);
            
/*            // Grab the Scheduler instance from the Factory
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            
	    	// start off the Quartz Scheduler
			scheduler.start();
		    log.debug("Quartz Job Scheduler started.");
        }
	    catch (SchedulerException e) {
			e.printStackTrace();*/
		}        
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception during web context initialization !");
        }
        log.info(APPLICATION_NAME + " Application Initialized !!! ");

    }
    
    
    /**
     * Record the fact that this web application has been destroyed.
     *
     * @param event The servlet context event
     */
    public void contextDestroyed(ServletContextEvent event) {
    	
    	try {
    		servletContext.removeAttribute("APP_NAME");
    		servletContext = null;

    		// shutdown the Quartz Scheduler
    		if(scheduler != null)
    			scheduler.shutdown();
    	}
		catch (SchedulerException e) {
			e.printStackTrace();
		}        
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception while destroying the web context !");
        }
    	log.info(APPLICATION_NAME + " Application Shut Down !!!"); 
    }


   /**
    * Returns WebApplicationListener in a string.
    * 
    * @return the string.
    */
   	public String toString() {
   		StringBuffer buf = new StringBuffer(APPLICATION_NAME + " WebApplicationListener");
   		return buf.toString();
   	}	
   	
}
