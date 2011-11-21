package com.googlecode.reunion.jreunion.game.npc;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class RangedMob extends Mob {
	public RangedMob(int type) {
		super(type);
		loadFromReference(type);
	}

	@Override
	public void loadFromReference(int type) {
		super.loadFromReference(type);
	}
}