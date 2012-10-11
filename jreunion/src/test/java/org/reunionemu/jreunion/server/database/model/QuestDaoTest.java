package org.reunionemu.jreunion.server.database.model;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.data.QuestDao;
import org.reunionemu.jreunion.data.quests.Quest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml", 
"classpath*:/spring/**/*-context-test.xml",
"classpath*:*-unit-test.xml"})
@DirtiesContext
public class QuestDaoTest {
	
	@Autowired
	QuestDao questDao;	
	
	@Test
	public void test() {
		Quest quest = questDao.findById(1);
		assertNotNull(quest);
	}

}
