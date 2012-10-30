package org.reunionemu.jreunion.test.hierachy;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

public class TestBeans {

	@Lazy(true)
	@Configurable(preConstruction=true)
	public static class TestBean{
		@Autowired
		ApplicationContext context;
		
		public TestBean(){
			System.out.println(context.getId());
		}
		
	}
	
	
	@Lazy(true)
	@Service
	public static class TestService{
		
		@PostConstruct
		public void init(){
			new TestBean();
			System.out.println("test");
			
		}
		
		
		
	}
	

}
