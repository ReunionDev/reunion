package com.googlecode.reunion.jreunion.game.quests.reward;

import com.googlecode.reunion.jreunion.game.quests.reward.type.RewardType;

public class Reward {

	private int id;
	
	private int ammount;
	
	private RewardType type;
	
	public Reward(int id, int ammount) {
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
	
	public RewardType getType(){
		return this.type;
	}
	
	public void setType(RewardType type){
		this.type = type;
	}

}