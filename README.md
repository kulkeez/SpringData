Congratulations! You have just developed a web application using Spring Boot and Spring JDBC using H2 in-memory database and Quartz scheduling. 

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://github.com/codespaces/new?hide_repo_select=true&ref=main&repo=582372347&machine=standardLinux32gb&devcontainer_path=.devcontainer%2Fdevcontainer.json&location=SouthEastAsia)


Note: Although it is possible to package this service as a traditional web application archive or WAR file 
for deployment to an external application server, the simpler approach demonstrated below creates a 
standalone application. You package everything in a single, executable JAR file, driven by 
a good old Java main() method. We use Spring’s support for embedding the Tomcat servlet container 
as the HTTP runtime, instead of deploying to an external instance.

A quick test can be performed using the link below to check if this Spring Boot application is UP: 
 * http://localhost:8080/
   

Various Actuator RESTful end points which are added to this application are: 
 * http://localhost:8080/actuator/health
 * http://localhost:8080/actuator/info 
 * http://localhost:8080/actuator/mappings 
 * http://localhost:8080/actuator/env
 * http://localhost:8080/actuator/metrics 
 
To shut down the application, make a POST request to /actuator/shutdown, as shown in the following curl-based example:
$ curl 'http://localhost:8080/actuator/shutdown' -i -X POST or using Postman. You would get a JSON response:

{
    "message": "Shutting down, bye..."
}

For more on Actuator end points documentation, check here: https://docs.spring.io/spring-boot/docs/2.7.6/actuator-api/html/#shutdown
For Spring Boot Documentation, refer here: https://docs.spring.io/spring-boot/docs/2.7.6/reference/htmlsingle/

### Want to run it?
This Spring Boot application can also be launched from command-line by the developer using the command: ```mvn spring-boot:run ```

