package org.reunionemu.jreunion.events.map;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Position;

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
