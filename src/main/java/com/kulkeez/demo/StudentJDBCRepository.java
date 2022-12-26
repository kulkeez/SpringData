package com.kulkeez.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 
 * 
 * @author kulkeez
 *
 */
// The @Repository annotation is a specialization of the @Component annotation 
// and marks this java class as a bean so the component-scanning mechanism of Spring can pick it up 
// and pull it into the application context.
@Repository
public class StudentJDBCRepository {

	private static final Logger logger = LogManager.getLogger(StudentJDBCRepository.class);
	
	@Autowired
    JdbcTemplate jdbcTemplate;
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Student findById(long id) {
	    return jdbcTemplate.queryForObject("select * from student where id=?", new Object[] {
	            id
	        },
	        new BeanPropertyRowMapper < Student > (Student.class));
	}
	
	public int getStudentCount() {
        String sql = "SELECT count(*) FROM STUDENT";
        
        //The method queryForInt(String, Object...) from the type JdbcTemplate is deprecated
        int count = jdbcTemplate.queryForObject(
                sql, new Object[] { }, Integer.class);
			
        return count;
  }
}
