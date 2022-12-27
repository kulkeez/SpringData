package com.kulkeez.demo;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


/**
* 
* A Simple Filter to observe filter chaining - produces BLOOP and BLEEP in the logs
* Dedicated to Danny Kaye & Kishore da (Kishore Kumar) - Kishore lives !
* 
* @author kulkeez
*
*/
@Component
@Order(1)
//@WebFilter(filterName = "bloopBleepFilter")
@Slf4j
public class BloopBleepFilter implements Filter {

	public static final String BLOOP_HTTP_HEADER = "bloop";
	public static final String BLEEP_HTTP_HEADER = "bleep";
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		log.debug("bloopBleepFilter initialized!");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
       HttpServletRequest req = (HttpServletRequest) request;
       HttpServletResponse res = (HttpServletResponse) response;
       
       log.info("BLLOOOOOOOP >>> " + req.getRequestURI() + " <<< BLEEEEEPP !!!");
 
       try {
       	// send request to next filter in the filter chain
			chain.doFilter(request, response);
		} 
       catch (Exception e) {
			e.printStackTrace();
		}
       finally {
       	// adding a custom header to all responses
       	// check if these additional headers make it in the final HTTP response
           res.addHeader(BLOOP_HTTP_HEADER, "true");
           res.addHeader(BLEEP_HTTP_HEADER, "true");
       }
	
	}

	@Override
	public void destroy() {
		log.debug("bloopBleepFilter destroyed!");
	}

}
