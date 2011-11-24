package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class StashPosition extends ItemPosition {

	private int slot;

	public StashPosition(int slot) {
		setSlot(slot);
	}

	public int getSlot() {
		return slot;
	}

	private void setSlot(int slot) {
		this.slot = slot;
	}
}
