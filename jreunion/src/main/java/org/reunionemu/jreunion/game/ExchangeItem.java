package org.reunionemu.jreunion.game;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class ExchangeItem extends InventoryItem {

	public ExchangeItem(Item<?> item, int x, int y) {
		super(item, new ExchangePosition(x, y));
	}
	
}