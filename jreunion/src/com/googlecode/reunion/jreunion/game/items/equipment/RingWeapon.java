package com.googlecode.reunion.jreunion.game.items.equipment;

import com.googlecode.reunion.jreunion.game.RangedWeapon;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class RingWeapon extends RangedWeapon {
	public RingWeapon(int id) {
		super(id);
		loadFromReference(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}

}