package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class RangedWeapon extends Weapon {
	public RangedWeapon(int type) {
		super(type);
	}

	@Override
	public void loadFromReference(int type) {
		super.loadFromReference(type);
	}
}