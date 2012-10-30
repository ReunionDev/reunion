package org.reunionemu.jreunion.game;

import org.reunionemu.jreunion.game.items.equipment.Weapon;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public abstract class SummonWeapon extends Weapon {
	public SummonWeapon(int type) {
		super(type);
	}

	@Override
	public void loadFromReference(int type) {
		super.loadFromReference(type);
	}
}