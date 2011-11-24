package org.reunionemu.jreunion.events.map;

import org.reunionemu.jreunion.game.Player;

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
