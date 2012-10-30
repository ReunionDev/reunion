package org.reunionemu.jreunion.game.items.equipment;

import org.reunionemu.jreunion.game.items.PersonalOrnament;


/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class Bracelet extends PersonalOrnament {
	public Bracelet(int id) {
		super(id);
		loadFromReference(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}
}