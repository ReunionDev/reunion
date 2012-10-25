package org.reunionemu.jreunion.server.test.model;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.dao.QuestDao;
import org.reunionemu.jreunion.model.Quest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml", 
"classpath*:/spring/**/*-context-test.xml"})
public class QuestDaoTest {
	
	@Autowired
	QuestDao questDao;		
	
	@Test
	public void test() throws IOException {
		
		Quest quest = questDao.findById(1);
		assertNotNull(quest);
	}

}
