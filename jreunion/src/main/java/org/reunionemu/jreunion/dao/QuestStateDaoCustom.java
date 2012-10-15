package org.reunionemu.jreunion.dao;

import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.Quest;

public interface QuestStateDaoCustom {
	public QuestState create(Quest quest);
}
