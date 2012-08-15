package org.reunionemu.jreunion.events.map;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.RoamingItem;

public class ItemDropEvent extends MapEvent {

	RoamingItem roamingItem;
	
	Player player;
	
	public ItemDropEvent(RoamingItem roamingItem, Player player) {
		super(roamingItem.getPosition().getLocalMap());
		this.roamingItem = roamingItem;
		this.player = player;
	}
	public RoamingItem getRoamingItem() {
		return roamingItem;
	}
	
	public Player getPlayer(){
		return this.player;
	}
}
