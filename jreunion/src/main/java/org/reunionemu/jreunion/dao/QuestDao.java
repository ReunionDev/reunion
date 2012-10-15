package org.reunionemu.jreunion.dao;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.model.Quest;

public interface QuestDao {
	Quest findById(int id);
	
	Quest getRandomQuest(Player player);

}
