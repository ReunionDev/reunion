package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 * Item wrapper for Inventory
 */
public class InventoryItem {
	
	private Item<?> item = null;
	
	private InventoryPosition position = null;

	public InventoryItem(Item<?> item, InventoryPosition position) {
		setItem(item);
		setPosition(position);
	}

	public Item<?> getItem() {
		return item;
	}
	
	public void setItem(Item<?> item){
		this.item = item;
	}

	public InventoryPosition getPosition() {
		return position;
	}
	
	public void setPosition(InventoryPosition position) {
		this.position = position;
	}

	public int getSizeX() {
		return item.getType().getSizeX();
	}

	public int getSizeY() {
		return item.getType().getSizeY();
	}
}
