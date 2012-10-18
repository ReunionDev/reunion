package org.reunionemu.jreunion.server.database.model;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.dao.QuestDao;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.jpa.test.CounterObjectiveStateEx;
import org.reunionemu.jreunion.model.jpa.test.DummyObjectiveState;
import org.reunionemu.jreunion.model.jpa.test.ObjectiveState;
import org.reunionemu.jreunion.model.jpa.test.QuestState;
import org.reunionemu.jreunion.model.quests.Objective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml", 
"classpath*:/spring/**/*-context-test.xml"})
public class PolymorphismTest {
		
	
	@Autowired
	private EntityManagerFactory emf;
	
	
	@Autowired
	QuestDao questDao;	
	
	@Test
	public void test() {
		EntityManager entityManager = emf.createEntityManager();
		
		Quest quest = questDao.findById(1);
		
		assumeNotNull(quest);
		System.out.println("test");
		
		QuestState state = new QuestState(quest);
		
		assertNotNull(state.questDao);		
		
		entityManager.getTransaction().begin();
		
		Objective objective = quest.getObjectives().get(0);
		assumeNotNull(objective);
		
		
		ObjectiveState obj1 = new CounterObjectiveStateEx(state, objective, 10);
		ObjectiveState obj2 = new DummyObjectiveState(state, objective, "dummy value");

		//entityManager.persist(obj);
		state.getObjectives().add(obj1);
		state.getObjectives().add(obj2);

		entityManager.persist(state);
		entityManager.getTransaction().commit();
		entityManager = emf.createEntityManager();;
		state = entityManager.find(QuestState.class, state.id);
		assertNotNull(state);
		
	}

}
