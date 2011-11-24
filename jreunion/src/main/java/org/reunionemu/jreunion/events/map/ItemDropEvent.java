package org.reunionemu.jreunion.events.map;

import org.reunionemu.jreunion.game.RoamingItem;

public class ItemDropEvent extends MapEvent {

	RoamingItem roamingItem;
	public ItemDropEvent(RoamingItem roamingItem) {
		super(roamingItem.getPosition().getLocalMap());
		this.roamingItem = roamingItem;
	}
	public RoamingItem getRoamingItem() {
		return roamingItem;
	}
}
