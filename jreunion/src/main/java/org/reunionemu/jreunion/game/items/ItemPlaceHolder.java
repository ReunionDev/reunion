package org.reunionemu.jreunion.game.items;

import org.reunionemu.jreunion.game.PlayerItem;


/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class ItemPlaceHolder extends PlayerItem{
	public ItemPlaceHolder(int id) {
		super(id);
		loadFromReference(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}
}