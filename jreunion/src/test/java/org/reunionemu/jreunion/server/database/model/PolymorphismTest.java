package org.reunionemu.jreunion.server.database.model;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.model.jpa.CounterObjectiveState;
import org.reunionemu.jreunion.model.jpa.ObjectiveState;
import org.reunionemu.jreunion.model.jpa.QuestState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml", 
"classpath*:/spring/**/*-context-test.xml"})
public class PolymorphismTest {
	
	
	private EntityManager entityManager;
	
	@Autowired
	private EntityManagerFactory emf;
	
	
	
	@Test
	public void test() {
		entityManager = emf.createEntityManager();
		
		QuestState state = new QuestState();
		
		entityManager.getTransaction().begin();
		state.setQuestId(1);
		ObjectiveState obj = new CounterObjectiveState(state, 10);
		entityManager.persist(obj);
		//state.objs.add(obj);
		entityManager.persist(state);
		entityManager.getTransaction().commit();
		entityManager = emf.createEntityManager();;
		state = entityManager.find(QuestState.class, state.id);
		assertNotNull(state);
		
	}

}
