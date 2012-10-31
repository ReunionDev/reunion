package org.reunionemu.jreunion.game;

public class InventoryPosition extends ItemPosition {
	
	public static final int EXCHANGE_TAB = 3;
	public static final int HAND_TAB = -1;
	public static final int HAND_X = -1;
	public static final int HAND_Y = -1;

	
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