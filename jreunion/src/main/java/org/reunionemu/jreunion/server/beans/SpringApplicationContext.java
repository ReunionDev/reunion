package org.reunionemu.jreunion.server.beans;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/*
 * Helper class to port over legacy code to Spring
 * TODO: Remove the need for this class
 */
@Lazy(false)
@Service
public class SpringApplicationContext implements ApplicationContextAware{
	
	
	private static ApplicationContext applicationContext;
	
	public static ApplicationContext getApplicationContext(){
		return applicationContext;
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		if(SpringApplicationContext.applicationContext!=null){
			//throw new RuntimeException("Multiple Application Contexts not supported.");
		}
		SpringApplicationContext.applicationContext = applicationContext;
	}
	
	

}
