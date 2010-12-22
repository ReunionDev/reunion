package com.googlecode.reunion.jreunion.events.map;

import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.server.LocalMap;

public class PlayerLoginEvent extends MapEvent {

	Player player;
	Position position;
	public PlayerLoginEvent(Player player, Position position) {
		super(player.getPosition().getLocalMap());
		this.player = player;
		this.position = position;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public Player getPlayer() {
		return player;
	}
}
