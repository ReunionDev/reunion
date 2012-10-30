package org.reunionemu.jreunion.game;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class QuickSlotItem {

	private QuickSlotPosition position;

	private Item<?> item;

	public QuickSlotItem(Item<?> item, QuickSlotPosition position) {
		item.setPosition(position);
		setPosition(position);
		setItem(item);
	}

	public Item<?> getItem() {
		return item;
	}

	public QuickSlotPosition getPosition() {
		return position;
	}

	public void setItem(Item<?> item) {
		this.item = item;
	}

	public void setPosition(QuickSlotPosition position) {
		this.position = position;
	}
}
