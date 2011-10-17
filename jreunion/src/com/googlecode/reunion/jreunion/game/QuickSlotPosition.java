package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class QuickSlotPosition extends ItemPosition {

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
