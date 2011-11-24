package com.googlecode.reunion.jreunion.game;

public class InventoryPosition extends ItemPosition {
	
	private int posX;
	private int posY;
	private int tab; // 1,2 or 3

	public InventoryPosition(int posX, int posY, int tab) {
		this.posX = posX;
		this.posY = posY;
		this.tab = tab;
	}
	
	public int getTab(){
		return tab;
	}
	
	public void setTab(int tab){
		this.tab = tab;
	}
	
	public int getPosX(){
		return posX;
	}
	
	public void setPosX(int posX){
		this.posX = posX;
	}
	
	public int getPosY(){
		return posY;
	}
	
	public void setPosY(int posY){
		this.posY = posY;
	}
}