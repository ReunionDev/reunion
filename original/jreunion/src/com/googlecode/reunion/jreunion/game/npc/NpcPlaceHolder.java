package com.googlecode.reunion.jreunion.game.npc;

import com.googlecode.reunion.jreunion.game.Npc;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NpcPlaceHolder extends Npc {
	public NpcPlaceHolder(int id) {
		super(id);
		loadFromReference(id);
	}
}