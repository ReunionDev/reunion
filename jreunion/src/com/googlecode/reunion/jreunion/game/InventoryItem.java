package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class InventoryItem {
	private Item item;

	private int posX;

	private int posY;

	private int tab;

	public InventoryItem(Item item, int posX, int posY, int tab) {
		this.item = item;
		this.posX = posX;
		this.posY = posY;
		this.tab = tab; // 1,2 or 3
	}

	public Item getItem() {
		return item;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int getSizeX() {
		return item.getSizeX();
	}

	public int getSizeY() {
		return item.getSizeY();
	}

	public int getTab() {
		return tab;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public void setTab(int tab) {
		this.tab = tab;
	}
}
