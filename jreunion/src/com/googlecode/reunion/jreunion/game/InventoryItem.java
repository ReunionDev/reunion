package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class InventoryItem {
	private Item item;

	private int x;

	private int y;

	private int tab;

	public InventoryItem(Item item, int x, int y, int tab) {
		this.item = item;
		this.x = x;
		this.y = y;
		this.tab = tab; // 1,2 or 3
	}

	public Item getItem() {
		return item;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
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

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setTab(int tab) {
		this.tab = tab;
	}
}
