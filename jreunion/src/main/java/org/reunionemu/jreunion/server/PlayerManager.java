package org.reunionemu.jreunion.server;

import java.util.Iterator;
import java.util.Vector;

import org.reunionemu.jreunion.game.Player;

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

	/**
	 * Returns a player by it's Entity Id.
	 * @param entityId
	 * @return Player
	 */
	public Player getPlayer(int entityId) {
		Iterator<Player> iter = getPlayerListIterator();
		while (iter.hasNext()) {
			Player player = iter.next();
			if (player.getEntityId() == entityId) {
				return player;
			}

		}
		return null;
	}
	
	/**
	 * Returns a player by it's DB Id.
	 * @param id
	 * @return Player
	 */
	public Player getPlayerByDbId(int id) {
		Iterator<Player> iter = getPlayerListIterator();
		while (iter.hasNext()) {
			Player player = iter.next();
			if (player.getPlayerId() == id) {
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
