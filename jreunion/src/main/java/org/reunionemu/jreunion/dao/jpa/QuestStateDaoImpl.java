package org.reunionemu.jreunion.dao.jpa;

import org.reunionemu.jreunion.dao.QuestStateBaseDao;
import org.reunionemu.jreunion.dao.QuestStateDaoCustom;
import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.jpa.QuestStateImpl;
import org.springframework.beans.factory.annotation.Autowired;


public class QuestStateDaoImpl implements QuestStateDaoCustom 
{
	@Autowired
	QuestStateBaseDao<QuestState> dao;
	
	@Override
	public QuestState create(Quest quest) {
		QuestStateImpl state = new QuestStateImpl(quest);
		dao.save(state);
		return state;
	}

}
