package com.googlecode.reunion.jreunion.game.quests;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.reunion.jreunion.game.Quest;
import com.googlecode.reunion.jreunion.game.quests.objective.Objective;

public class QuestState {
	
	Quest quest;
	Map<Objective, Integer> progression = new HashMap <Objective, Integer>();
	
	public QuestState(Quest quest) {
		this.quest = quest;
	}
	
	public void increase(Objective objective){
		
		if(!quest.getObjectives().contains(objective)){
			throw new RuntimeException("Quest "+quest.getId()+" does not have objective "+objective.getId());
		}
		if(!progression.containsKey(objective)){
			progression.put(objective, 0);
		}
		progression.put(objective, progression.get(objective) + 1);		
	}
	
	public Quest getQuest(){
		return this.quest;
	}
	
	public int getProgression(Objective objective){
		return progression.get(objective); //if objective not found, it returns null
	}
}

