package com.googlecode.reunion.jreunion.game.items;

import com.googlecode.reunion.jreunion.game.PlayerItem;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
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