package org.reunionemu.jreunion.events.map;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.RoamingItem;

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
