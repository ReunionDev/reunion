package com.googlecode.reunion.jreunion.game.quests;

import com.googlecode.reunion.jreunion.game.Quest;

public class PointsQuest extends Quest{
	
	private int totalPoints;
	
	public PointsQuest(int questId) {
		super(questId);
	}
	
	public int getTotalPoints() {
		return totalPoints;
	}
	
	public void setTotalPoints(int totalPoints){
		this.totalPoints = totalPoints;
	}
}