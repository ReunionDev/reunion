package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_InventoryItem {
	private G_Item item;

	private int posX;

	private int posY;

	private int tab;

	public G_InventoryItem(G_Item item, int posX, int posY, int tab) {
		this.item = item;
		this.posX = posX;
		this.posY = posY;
		this.tab = tab; // 1,2 or 3
	}

	public G_Item getItem() {
		return item;
	}

	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX){
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY){
		this.posY = posY;
	}

	public int getTab() {
		return tab;
	}
	public void setTab(int tab){
		this.tab = tab;
	}

	public int getSizeX() {
		return item.getSizeX();
	}

	public int getSizeY() {
		return item.getSizeY();
	}
}
