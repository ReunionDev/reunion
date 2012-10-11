package org.reunionemu.jreunion.data;

import org.reunionemu.jreunion.data.quests.Quest;

public interface QuestDao {
	Quest findById(int id);

}
