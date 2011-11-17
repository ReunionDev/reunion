package com.googlecode.reunion.jreunion.game;

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