package org.reunionemu.jreunion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
@ComponentScan(basePackageClasses=Spring.class)
public class Spring implements Runnable {
	@Autowired
	private ApplicationContext context;

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(Spring.class);				
				//new ClassPathXmlApplicationContext("META-INF/context.xml");		
		Spring test = (Spring) context.getBean("spring");
		test.run();
	}

	public void run() {
		
		if(context==null){
			throw new IllegalStateException();
		}
		Spring instance = (Spring) context.getBean("spring");
		if(this!=instance){
			throw new IllegalStateException();
			
		}
		
		System.out.println("success");
		
	}

}

