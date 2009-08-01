package com.googlecode.reunion.jreunion.server;


import java.util.Iterator;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.G_Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_PlayerManager {
	private java.util.List<G_Player> playerList = new Vector<G_Player>();

	public S_PlayerManager() {

	}

	public void addPlayer(G_Player player) {
		playerList.add(player);
	}
	public void removePlayer(G_Player player) {
		playerList.remove(player);
	}
	public boolean containsPlayer(G_Player player) {
		return playerList.contains(player);
	}

	public int getNumberOfPlayers() {
		return playerList.size();
	}

	public G_Player getPlayer(String charName) {
		Iterator<G_Player> iter = getPlayerListIterator();
		while (iter.hasNext()) {
			G_Player player = iter.next();
			if (player.getName().equals(charName))
				return player;

		}
		return null;
	}
	public G_Player getPlayer(int id) {
		Iterator<G_Player> iter = getPlayerListIterator();
		while (iter.hasNext()) {
			G_Player player = iter.next();
			if (player.getEntityId() == id)
				return player;

		}
		return null;
	}

	public Iterator<G_Player> getPlayerListIterator() {
		return playerList.iterator();
	}
}
