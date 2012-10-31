package org.reunionemu.jreunion.game;

import org.reunionemu.jreunion.model.jpa.InventoryItemImpl;

public class HandInventoryItem extends InventoryItemImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public HandInventoryItem(Item<?> item, Player player) {
		super(item, new HandPosition() , player);
	}	
	
}