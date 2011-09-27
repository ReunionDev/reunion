package com.googlecode.reunion.jreunion.game.quests.objective;

public class PointsObjective extends Objective{
	
	private int currentPoints; //obtained points
	
	public PointsObjective(int id, int ammount) {
		super(id, ammount);
	}
	
	public int getCurrentPoints() {
		return currentPoints;
	}
	
	public void setCurrentPoints(int currentPoints) {
		this.currentPoints = currentPoints;
	}
}