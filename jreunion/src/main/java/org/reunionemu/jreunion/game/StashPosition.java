package org.reunionemu.jreunion.game;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
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
