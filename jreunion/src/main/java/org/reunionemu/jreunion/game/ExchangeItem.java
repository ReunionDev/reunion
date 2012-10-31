package org.reunionemu.jreunion.game;

import org.reunionemu.jreunion.model.jpa.InventoryItemImpl;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class ExchangeItem extends InventoryItemImpl {

	private static final long serialVersionUID = 1L;

	public ExchangeItem(Item<?> item, int x, int y, Player player) {
		super(item, new ExchangePosition(x, y), player);
	}
	
}