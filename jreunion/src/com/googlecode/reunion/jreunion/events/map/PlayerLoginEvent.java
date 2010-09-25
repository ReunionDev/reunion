package com.googlecode.reunion.jreunion.events.map;

import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.server.LocalMap;

public class PlayerLoginEvent extends MapEvent {

	Player player;
	public PlayerLoginEvent(Player player) {
		super(player.getPosition().getMap());
		this.player = player;
	}
	public Player getPlayer() {
		return player;
	}
}
