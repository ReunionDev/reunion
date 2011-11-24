package com.googlecode.reunion.jreunion.game.items.equipment;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.items.SpecialWeapon;
import com.googlecode.reunion.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class DemolitionWeapon extends SlayerWeapon {

	public DemolitionWeapon(int id) {
		super(id);
		loadFromReference(id);
	}
}