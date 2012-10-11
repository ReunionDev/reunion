package org.reunionemu.jreunion.server;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Quest;

public class QuestManager {

	private java.util.Map<Integer,Quest> quests = new HashMap<Integer,Quest>();
	
	public QuestManager(){
		
	}
	
	public void loadQuests(){
		quests = DatabaseUtils.getStaticInstance().loadQuests();
		if(quests != null){
			LoggerFactory.getLogger(QuestManager.class).info("Loaded "+quests.size()+" quests");
		}
	}
	
	public Quest getQuest(int id){
		
		return quests.get(id);
	}
	
	public boolean isEmpty(){
		return quests.isEmpty();
	}
	
	public Quest getRandomQuest(Player player){
		if (player == null) return null;
		if (quests.isEmpty()) return null;
		
		//a quests list that will only contain quests of the player level
		List<Quest> questsList = new Vector<Quest>();
		int randQuestId = -1;
		
		for(int questId: quests.keySet()){
			Quest quest = getQuest(questId);
			
			if(player.getLevel() >= quest.getMinLevel() && player.getLevel() <= quest.getMaxLevel() && quest.isRepeatable())
				questsList.add(quest);
		} 
		
		if(questsList.isEmpty()){
			LoggerFactory.getLogger(QuestManager.class).debug("No quests found for the player level!");
			return null;
		}
		
		//gets a random position from the available quests list.
		while( randQuestId > questsList.size()-1 || randQuestId == -1){
			randQuestId = (int)(Math.random()*100);
		}
		
		return questsList.get(randQuestId);
	}
}
