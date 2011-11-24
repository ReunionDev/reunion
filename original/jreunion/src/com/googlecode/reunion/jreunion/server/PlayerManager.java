package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PlayerManager {
	private final java.util.List<Player> playerList = new Vector<Player>();

	public PlayerManager() {

	}

	public void addPlayer(Player player) {
		playerList.add(player);
	}

	public boolean containsPlayer(Player player) {
		return playerList.contains(player);
	}

	public int getNumberOfPlayers() {
		return playerList.size();
	}

	public Player getPlayer(int id) {
		Iterator<Player> iter = getPlayerListIterator();
		while (iter.hasNext()) {
			Player player = iter.next();
			if (player.getEntityId() == id) {
				return player;
			}

		}
		return null;
	}

	public Player getPlayer(String charName) {
		Iterator<Player> iter = getPlayerListIterator();
		while (iter.hasNext()) {
			Player player = iter.next();
			if (player.getName().equals(charName)) {
				return player;
			}

		}
		return null;
	}

	public Iterator<Player> getPlayerListIterator() {
		return playerList.iterator();
	}

	public void removePlayer(Player player) {
		playerList.remove(player);
	}
}
