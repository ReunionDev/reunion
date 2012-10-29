package org.reunionemu.jreunion.server;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.ApplicationContext;

@Configurable(preConstruction=true)
public class GenericLoader{

	@Autowired 
	ApplicationContext context;
	
	public <T> T getObject (Class<T> clazz){
		return context.getBean(clazz);		
	}

}
