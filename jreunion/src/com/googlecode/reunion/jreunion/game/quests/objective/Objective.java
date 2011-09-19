package com.googlecode.reunion.jreunion.game.quests.objective;

import com.googlecode.reunion.jreunion.game.quests.objective.type.ObjectiveType;

public class Objective {

	private int id;
	
	private int ammount;
	
	private ObjectiveType type;
	
	public Objective(int id, int ammount) {
		this.id = id;
		this.ammount = ammount;
	}
	
	public int getId(){
		return this.id;
	}
	
	public int getAmmount(){
		return this.ammount;
	}
	
	public void setAmmount(int ammount){
		this.ammount = ammount;
	}
	
	public ObjectiveType getType(){
		return this.type;
	}
	
	public void setType(ObjectiveType type){
		this.type = type;
	}

}