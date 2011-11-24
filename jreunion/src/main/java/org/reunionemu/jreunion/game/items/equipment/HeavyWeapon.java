package org.reunionemu.jreunion.game.items.equipment;

import org.reunionemu.jreunion.game.items.SpecialWeapon;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class HeavyWeapon extends SpecialWeapon {
	public HeavyWeapon(int id) {
		super(id);
		loadFromReference(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}

}