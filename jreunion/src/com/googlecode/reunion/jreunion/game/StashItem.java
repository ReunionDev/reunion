package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class StashItem {

	private int pos;

	private Item item;

	public StashItem(int pos, Item item) {
		setPos(pos);
		setItem(item);
	}

	public Item getItem() {
		return item;
	}

	public int getPos() {
		return pos;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
}