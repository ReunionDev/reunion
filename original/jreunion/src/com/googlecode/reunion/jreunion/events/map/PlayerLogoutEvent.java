package com.googlecode.reunion.jreunion.events.map;

import com.googlecode.reunion.jreunion.game.Player;

public class PlayerLogoutEvent extends MapEvent {

	Player player;
	public PlayerLogoutEvent(Player player) {
		super(player.getPosition().getLocalMap());
		this.player = player;
	}
	public Player getPlayer() {
		return player;
	}
}
