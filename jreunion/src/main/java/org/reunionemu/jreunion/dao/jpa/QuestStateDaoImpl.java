package org.reunionemu.jreunion.dao.jpa;

import org.reunionemu.jreunion.dao.QuestStateDao;
import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.jpa.QuestStateImpl;

public interface QuestStateDaoImpl extends QuestStateDao<QuestStateImpl> {
	
	/*
		@Override
		public QuestState create(Quest quest) {
			return new QuestStateImpl(quest);
		}
		*/
}
