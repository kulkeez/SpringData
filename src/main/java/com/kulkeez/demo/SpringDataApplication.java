package com.kulkeez.demo;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;

import org.springframework.jdbc.core.JdbcTemplate;

import com.kulkeez.demo.model.Customer;
import com.kulkeez.demo.model.Employee;
import com.kulkeez.demo.repository.EmployeeJDBCRepository;

/**
 * 
 * Notice below that the @EnableAutoConfiguration annotation used (indirectly) for this class tells 
 * Spring Boot to “guess” how you want to configure Spring, based on the jar dependencies that you have added. 
 * Since spring-boot-starter-web added Tomcat and Spring MVC, the auto-configuration assumes that 
 * you are developing a web application and sets up Spring accordingly. 
 * 
 * Note: This class implements Spring Boot’s CommandLineRunner, which means during startup, Spring will 
 * execute the run() method after the application context is loaded up.
 * 
 * Autodiscovery is performed by Spring using the @ComponentScan which looks at classes that use
 * special stereotype annotations: @Component, @Controller, @Repository, @Service, @Configuration
 * 
 * The H2 In-Memory Database Console is enable and can be reached here: http://localhost:8080/h2-console
 * 
 * Also note that multiple CommandLineRunner run() methods can be executed as demonstrated below
 * This Spring web Application also has a WebListener configured using @ServletComponentScan annotation
 * 
 * @author kulkeez
 *
 */
// To enable the scanning for @WebFilter, @WebListener and @WebServlet annotations (i.e. JEE annotations) 
// since embedded containers do not support these annotations 
// This annotation below can only be used when the application needs to run in embedded container.
@ServletComponentScan
//convenience annotation that adds @Configuration, @EnableAutoConfiguration, @ComponentScan
@SpringBootApplication
@Slf4j
public class SpringDataApplication {

	/**
	 * Our main method delegates to Spring Boot’s SpringApplication class by calling run. 
	 * SpringApplication bootstraps our application, starting Spring, which, in turn, starts the 
	 * auto-configured Tomcat web server.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("Launching the Spring Data application ...");
		Timestamp tOne = new Timestamp(System.currentTimeMillis());
		
		// Launch the application
        ApplicationContext ctx = SpringApplication.run(SpringDataApplication.class, args);
		//new SpringApplicationBuilder(SpringDataApplication.class).bannerMode(Mode.CONSOLE).run(args);
		log.info("Launched Spring Data application at time: {} ", tOne);
	}

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
    @Autowired
    EmployeeJDBCRepository employeeRepository;
       
	
	/**
	 * This method runs on start up, retrieves all the beans that were created either by your app or 
	 * were automatically added, thanks to Spring Boot. It sorts them and prints them out.
	 * 
	 * The @Bean annotation used below tells Spring that this method will return a CommandLineRunner object 
	 * that should be registered as a bean in the Spring application context.
	 * 
	 * @param ctx
	 * @return
	 */
	@Bean
	@Description("Upon Spring start up, retrieves all the beans that were created either by our app or were automatically added thanks to Spring Boot.")
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            log.info("Let's inspect the beans provided by Spring Boot for Quartz:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
            	if(beanName.startsWith("quartz") || beanName.contains("quartz") || beanName.contains("Quartz") 
            			|| beanName.contains("Job") || beanName.contains("schedule"))
            		log.debug(beanName);
                
            }

            log.info("Total Beans available in the Spring Container: " + ctx.getBeanDefinitionCount());
            
        	log.info("Initializing Database...");
        	jdbcTemplate.execute("DROP TABLE EMPLOYEE IF EXISTS");
        	
        	jdbcTemplate.execute("CREATE TABLE EMPLOYEE \r\n"
        			+ "(\r\n"
        			+ "	ID int NOT NULL PRIMARY KEY,\r\n"
        			+ "	FIRST_NAME varchar(255),\r\n"
        			+ "	LAST_NAME varchar(255),\r\n"
        			+ "	ADDRESS varchar(255)\r\n"
        			+ ");");
        	
        	List<Employee> employees = Arrays.asList(
                    new Employee(5L, "John", "McGinn", "Munich"),
                    new Employee(6L, "Ralf", "Reddin", "Dusseldorf"),
                    new Employee(7L, "Mark", "Greer", "Atlanta")
            );

            log.info("Populating EMPLOYEE table...");
            employees.forEach(emp -> {
                log.debug("Saving...[{}]", emp.getFirstName() + " " + emp.getLastName());
                employeeRepository.create(emp);
            });
            
            log.info("EMPLOYEE Table Count: {}", employeeRepository.count());
            log.info("Looking up Employee #7: {}", employeeRepository.findById(7L));
            
        };
    }


	/**  
	 * Configuring this bean to Enable Spring's built-in Request Logging
	 * 
	 * @return
	 */
	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
	    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
	    
	    loggingFilter.setIncludeClientInfo(true);
	    loggingFilter.setIncludeQueryString(true);
	    loggingFilter.setIncludePayload(true);
	    loggingFilter.setMaxPayloadLength(10000);
	    loggingFilter.setIncludeHeaders(false);
	    loggingFilter.setAfterMessagePrefix("REQUEST DATA : ");
	    
	    log.info("Configured Spring's built-in Request Logging using CommonsRequestLoggingFilter");
	    
	    return loggingFilter;
	}
	
}
