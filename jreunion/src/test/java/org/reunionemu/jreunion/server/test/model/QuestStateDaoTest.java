package org.reunionemu.jreunion.server.test.model;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeNotNull;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.dao.QuestDao;
import org.reunionemu.jreunion.dao.QuestStateBaseDao;
import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.jpa.CounterObjectiveStateImpl;
import org.reunionemu.jreunion.model.jpa.QuestStateImpl;
import org.reunionemu.jreunion.model.quests.CounterObjectiveState;
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
	QuestStateBaseDao<QuestState> questStateDao;		
	
	@Test
	public void test() throws IOException {
		
		assumeNotNull(questDao);
		Quest quest = questDao.findById(1);
		assumeNotNull(quest);
		System.out.println("create");
		QuestState state = questStateDao.create(quest);
		//QuestStateImpl state = new QuestStateImpl(quest);
		
		assertNotNull(state);
		System.out.println("save");
		questStateDao.save(state);
		System.out.println("save");
		questStateDao.save(state);
		System.out.println("save");
		((CounterObjectiveState)state.getObjectives().get(0)).decrease();
		questStateDao.save(state);
		
		QuestState qs = questStateDao.findOne(((QuestStateImpl)state).getId());
		assertNotNull(qs);
	}

}
