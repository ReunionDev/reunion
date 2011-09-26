package com.googlecode.reunion.jreunion.events.map;

import com.googlecode.reunion.jreunion.game.RoamingItem;

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
