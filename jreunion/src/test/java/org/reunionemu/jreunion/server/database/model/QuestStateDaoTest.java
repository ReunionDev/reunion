package org.reunionemu.jreunion.server.database.model;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeNotNull;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.dao.QuestDao;
import org.reunionemu.jreunion.dao.QuestStateDao;
import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.jpa.QuestStateImpl;
import org.reunionemu.jreunion.model.jpa.test.CounterObjectiveStateEx;
import org.reunionemu.jreunion.model.jpa.test.ObjectiveState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml",
"classpath*:/spring/**/*-context-test.xml"
})
public class QuestStateDaoTest {
	
	@Autowired
	QuestDao questDao;		
	
	@Autowired
	QuestStateDao<QuestState> questStateDao;		
	
	@Test
	public void test() throws IOException {
		
		assumeNotNull(questDao);
		Quest quest = questDao.findById(1);
		assumeNotNull(quest);
		
		QuestStateImpl state = new QuestStateImpl(quest);
		
		assertNotNull(state);
		questStateDao.save(state);
		
		QuestState qs = questStateDao.findOne(state.getId());
		assertNotNull(qs);
	}

}
