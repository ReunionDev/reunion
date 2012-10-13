package org.reunionemu.jreunion.dao;

import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.Quest;
import org.springframework.data.repository.CrudRepository;

public interface QuestStateDao<A extends QuestState> extends CrudRepository<A, Long> {
	
	//public QuestState create(Quest quest);
		
}
