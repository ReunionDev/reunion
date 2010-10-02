package com.googlecode.reunion.jreunion.game.items.equipment;

import com.googlecode.reunion.jreunion.game.RangedWeapon;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class StaffWeapon extends RangedWeapon {
	public StaffWeapon(int id) {
		super(id);
		loadFromReference(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}
}