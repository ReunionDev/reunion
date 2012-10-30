package org.reunionemu.jreunion.test.hierachy;

import org.junit.Test;
import org.reunionemu.jreunion.test.hierachy.TestBeans.TestService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextHierarchyTest {

	
	public static void main(String [] args){
		
		ClassPathXmlApplicationContext parent = new ClassPathXmlApplicationContext("classpath*:/inheritancetest/parent.xml");
		ClassPathXmlApplicationContext child1 = new ClassPathXmlApplicationContext(parent);
		ClassPathXmlApplicationContext child2 = new ClassPathXmlApplicationContext(parent);

		child1.setConfigLocation("classpath*:/inheritancetest/child.xml");
		child2.setConfigLocation("classpath*:/inheritancetest/child.xml");
		child1.refresh();
		child2.refresh();

		TestService s2 = child1.getBean(TestService.class);
		TestService s1 = parent.getBean(TestService.class);
		StringBuilder b1 = child1.getBean(StringBuilder.class);
		
		StringBuilder b2 = child2.getBean(StringBuilder.class);
		b1.append("child1");
		b2.append("child2");
		
		System.out.println(parent.getBean(StringBuilder.class));

		System.out.println(b1);
		System.out.println(b2);

	}
	@Test
	public void test() {
		//ClassPathXmlApplicationContext parent = new ClassPathXmlApplicationContext("classpath*:/inheritancetest/parent.xml");
		//ClassPathXmlApplicationContext parent = new ClassPathXmlApplicationContext("classpath*:/inheritancetest/parent.xml");
		
		//parent.getBean(TestService.class); 
		
		//fail("Not yet implemented");
	}
	
	
}
