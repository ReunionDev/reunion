package org.reunionemu.jreunion.game.npc;

import org.reunionemu.jreunion.game.Npc;

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