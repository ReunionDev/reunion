package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class MeleeWeapon extends Weapon {
	public MeleeWeapon(int id) {
		super(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}

	@Override
	public boolean use(Player player) {
		return true;
	}
}