package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 * Item wrapper for Inventory
 */
public class InventoryItem {
	
	private Item item;
	
	private InventoryPosition position;

	public InventoryItem(Item item, InventoryPosition position) {
		this.item = item;
		this.position = position;
	}

	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item){
		this.item = item;
	}

	public InventoryPosition getPosition() {
		return position;
	}
	
	public void setPosition(InventoryPosition position) {
		this.position = position;
	}

	public int getSizeX() {
		return item.getSizeX();
	}

	public int getSizeY() {
		return item.getSizeY();
	}
}
