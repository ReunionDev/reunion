package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class QuickSlotItem {

	private int slot;

	private Item item;

	public QuickSlotItem(Item item, int slot) {
		this.item = item;
		this.slot = slot;
	}

	public Item getItem() {
		return item;
	}

	public int getSlot() {
		return slot;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}
}
