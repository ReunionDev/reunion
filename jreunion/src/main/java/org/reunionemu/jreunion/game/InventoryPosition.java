package org.reunionemu.jreunion.game;

public class InventoryPosition implements ItemPosition {
	
	public static final int EXCHANGE_TAB = 3;
	public static final int HAND_TAB = -1;
	public static final int HAND_X = -1;
	public static final int HAND_Y = -1;

	
	private final int posX;
	private final int posY;
	private final int tab; // 1,2 or 3

	public InventoryPosition(int posX, int posY, int tab) {
		this.posX = posX;
		this.posY = posY;
		this.tab = tab;
	}
	
	public int getTab(){
		return tab;
	}
	
	public InventoryPosition setTab(int tab){
		return instanciate(getPosX(), getPosY(), tab);

	}
	
	public int getPosX(){
		return posX;
	}
	
	public InventoryPosition setPosX(int posX){
		return instanciate(posX, getPosY(), getTab());
	}
	
	public int getPosY(){
		return posY;
	}
	
	public InventoryPosition setPosY(int posY){
		return instanciate(getPosX(), posY, getTab());
	}
	
	protected InventoryPosition instanciate(int posX, int posY, int tab){
		return new InventoryPosition(posX, posY, tab);
	}
}