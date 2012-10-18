package org.reunionemu.jreunion.server;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy(false)
public class AspectJChecker {
	
	Logger logger = LoggerFactory.getLogger(AspectJChecker.class);
	
	@Configurable
	public class Checker{ 
		@Autowired
		AspectJChecker checker;
		public boolean check(){
			return checker != null; 
		}
	}
	
	@PostConstruct	
	public void load(){
		if(!new Checker().check()){
			logger.error("Not compiled with Aspectj"); 
			System.exit(-1);			
		}else{
			
			logger.debug("Aspectj check succeeded");
		}
		
	}
	
}
