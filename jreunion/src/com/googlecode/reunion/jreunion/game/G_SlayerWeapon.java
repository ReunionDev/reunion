package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.S_ParsedItem;
import com.googlecode.reunion.jreunion.server.S_Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_SlayerWeapon extends G_SpecialWeapon {
	private double memoryDmg;

	public G_SlayerWeapon(int id) {
		super(id);
		loadFromReference(id);
	}

	public double getMemoryDmg() {
		return memoryDmg;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		S_ParsedItem item = S_Reference.getInstance().getItemReference()
				.getItemById(id);

		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setMemoryDmg(0);
		} else {
			if (item.checkMembers(new String[] { "MemoryDmg" })) {
				// use member from file
				setMemoryDmg(Integer.parseInt(item.getMemberValue("MemoryDmg")));
			} else {
				// use default
				setMemoryDmg(0);
			}
		}
	}

	public void setMemoryDmg(double memoryDmg) {
		this.memoryDmg = memoryDmg;
	}
}