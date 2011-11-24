package org.reunionemu.jreunion.game.items.equipment;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.RangedWeapon;

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
	
	@Override
	public boolean use(Player player) {
		
		return true;
	}
}