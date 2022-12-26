package com.kulkeez;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContextListener;

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

import com.kulkeez.demo.Customer;
import com.kulkeez.demo.StudentJDBCRepository;

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
public class SpringDataApplication implements CommandLineRunner {

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
		//new SpringApplicationBuilder(SpringDataApplication.class).bannerMode(Mode.ON).run(args);
		log.info("Launched Spring Data application at time: " + tOne);
		
	}

	@Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    StudentJDBCRepository studentRepository;
       
    //@Override
    public void run(String... strings) throws Exception {
    	log.info("Creating tables...");

        // Fire some DDLs to create tables: STUDENT and CUSTOMERS
        jdbcTemplate.execute("DROP TABLE student IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE student(" +
                "id INTEGER not null, name VARCHAR(255) not null, passport_number VARCHAR(255) not null, primary key(id))");

        jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE customers(" +
                "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

        // Split up the array of whole names into an array of first/last names
        List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        // Use a Java 8 stream to print out each tuple of the list
        splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

        // Finally, use the query method to search your table for records matching the criteria.
        log.info("Querying for customer records where first_name = 'Josh':");
        jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[] { "Josh" },
                (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
        ).forEach(customer -> log.info(customer.toString()));
        
        log.info("STUDENT Table Count: ", studentRepository.getStudentCount());
    }	 
    
	
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
