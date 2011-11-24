package com.googlecode.reunion.jreunion.game;

public class HandPosition extends ItemPosition {

	private Item<?> item = null;
	
	public HandPosition(Item<?> item) {
		setItem(item);
	}

	public Item<?> getItem() {
		return item;
	}

	public void setItem(Item<?> item) {
		this.item = item;
	}
	
	
}