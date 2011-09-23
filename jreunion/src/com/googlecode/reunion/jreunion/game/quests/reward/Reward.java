package com.googlecode.reunion.jreunion.game.quests.reward;

public class Reward {

	private int id;
	
	private int ammount;
	
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

}