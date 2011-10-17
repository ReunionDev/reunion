package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ExchangeItem extends InventoryItem {

	public ExchangeItem(Item<?> item, int x, int y) {
		super(item, new InventoryPosition(x, y, 3));
	}
	
}