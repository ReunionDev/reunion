package org.reunionemu.jreunion.server.database.model;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.dao.QuestDao;
import org.reunionemu.jreunion.model.Quest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml", 
"classpath*:/spring/**/*-context-test.xml"})
public class QuestDaoTest {
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	QuestDao questDao;		
	
	@Test
	public void test() throws IOException {
		
		Resource [] resources = context.getResources("*");
		for(Resource resource: resources){
			System.out.println(resource.getURL());
		}
		
		Quest quest = questDao.findById(1);
		assertNotNull(quest);
	}

}
