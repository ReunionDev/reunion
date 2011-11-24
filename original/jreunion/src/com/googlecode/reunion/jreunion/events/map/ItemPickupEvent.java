package com.googlecode.reunion.jreunion.events.map;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.RoamingItem;

public class ItemPickupEvent extends MapEvent {

	Player player;
	RoamingItem roamingItem;
	public ItemPickupEvent(Player player, RoamingItem roamingItem) {
		super(roamingItem.getPosition().getLocalMap());
		this.player = player;
		this.roamingItem = roamingItem;
	}
	public Player getPlayer() {
		return player;
	}
	public RoamingItem getRoamingItem() {
		return roamingItem;
	}
}
