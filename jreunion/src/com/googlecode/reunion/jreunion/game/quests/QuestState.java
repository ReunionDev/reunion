package com.googlecode.reunion.jreunion.game.quests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.reunion.jreunion.game.Quest;
import com.googlecode.reunion.jreunion.game.quests.objective.Objective;

public class QuestState {
	
	Quest quest;
	Map<Objective, Integer> progression = new HashMap <Objective, Integer>();
	
	public QuestState(Quest quest) {
		this.quest = quest;
		loadObjectives(quest.getObjectives());
	}
	
	public void loadObjectives(List<Objective> objectives){
		for(Objective objective: objectives){
			progression.put(objective, objective.getAmmount());
		}
	}
	
	public Quest getQuest(){
		return this.quest;
	}
	
	public int getProgression(Objective objective){
		return progression.get(objective); //if objective not found, it returns null
	}
}

