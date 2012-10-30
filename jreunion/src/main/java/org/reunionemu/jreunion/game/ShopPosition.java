package org.reunionemu.jreunion.game;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class ShopPosition extends ItemPosition {

	private int slot;

	public ShopPosition(int slot) {
		setSlot(slot);
	}

	public int getSlot() {
		return slot;
	}

	private void setSlot(int slot) {
		this.slot = slot;
	}
}