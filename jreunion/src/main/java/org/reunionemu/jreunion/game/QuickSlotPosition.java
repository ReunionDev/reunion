package org.reunionemu.jreunion.game;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class QuickSlotPosition implements ItemPosition {

	private QuickSlotBar quickSlotBar;
	private int slot;

	public QuickSlotPosition(QuickSlotBar quickSlotBar, int slot) {
		setQuickSlotBar(quickSlotBar);
		setSlot(slot);
	}
	
	public QuickSlotBar getQuickSlotBar() {
		return quickSlotBar;
	}

	private void setQuickSlotBar(QuickSlotBar quickSlotBar) {
		this.quickSlotBar = quickSlotBar;
	}

	public int getSlot() {
		return slot;
	}

	private void setSlot(int slot) {
		this.slot = slot;
	}
}
