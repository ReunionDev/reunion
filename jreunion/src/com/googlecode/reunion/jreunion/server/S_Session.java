package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.G_LivingObject;
import com.googlecode.reunion.jreunion.game.G_Mob;
import com.googlecode.reunion.jreunion.game.G_Npc;
import com.googlecode.reunion.jreunion.game.G_Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Session {
	private List<G_Player> playerList = new Vector<G_Player>();

	private List<G_Npc> npcList = new Vector<G_Npc>();

	private List<G_Mob> mobList = new Vector<G_Mob>();

	private boolean sessionActive = false;

	private G_Player sessionOwner;

	// private double currDistance = 0;

	public S_Session(G_Player player) {

		super();
		player.setSession(this);
		sessionOwner = player;
		setActive(true);

	}

	public void close() {
		playerList.clear();
		npcList.clear();
		mobList.clear();
		setActive(false);
	}

	public boolean contains(G_LivingObject object) {
		if (playerList.contains(object)) {
			return true;
		}
		if (mobList.contains(object)) {
			return true;
		}
		if (npcList.contains(object)) {
			return true;
		}
		return false;
	}

	public void enterMob(G_Mob mob, int spawning) {
		if (mobList.contains(mob)) {
			return;
		}
		mobList.add(mob);
		S_Server.getInstance().getWorldModule().getWorldCommand()
				.mobIn(sessionOwner, mob, spawning);
	}

	public void enterNpc(G_Npc npc) {
		if (npcList.contains(npc)) {
			return;
		}
		npcList.add(npc);
		S_Server.getInstance().getWorldModule().getWorldCommand()
				.npcIn(sessionOwner, npc);
	}

	public void enterPlayer(G_Player player, int warping) {

		if (player == sessionOwner) {
			return;
		}
		if (playerList.contains(player)) {
			return;
		}
		playerList.add(player);
		S_Server.getInstance().getWorldModule().getWorldCommand()
				.charIn(sessionOwner, player, warping);
	}

	public void exitMob(G_Mob mob) {
		if (!mobList.contains(mob)) {
			return;
		}
		while (mobList.contains(mob)) {
			mobList.remove(mob);
		}
		S_Server.getInstance().getWorldModule().getWorldCommand()
				.mobOut(sessionOwner, mob);
	}

	public void exitNpc(G_Npc npc) {
		if (!npcList.contains(npc)) {
			return;
		}
		while (npcList.contains(npc)) {
			npcList.remove(npc);
		}
		S_Server.getInstance().getWorldModule().getWorldCommand()
				.npcOut(sessionOwner, npc);
	}

	public void exitPlayer(G_Player player) {
		if (player == sessionOwner) {
			return;
		}
		if (!playerList.contains(player)) {
			return;
		}
		while (playerList.contains(player)) {
			playerList.remove(player);
		}
		S_Server.getInstance().getWorldModule().getWorldCommand()
				.charOut(sessionOwner, player);
	}

	public boolean getActive() {
		return sessionActive;
	}

	public G_Player getPlayer(int index) {
		return playerList.get(index);
	}

	public Iterator<G_Player> getPlayerListIterator() {
		return playerList.iterator();
	}
	
	public Iterator<G_Npc> getNpcListIterator() {
		return npcList.iterator();
	}

	public int getPlayerListSize() {
		return playerList.size();
	}

	/**
	 * @return Returns the sessionOwner.
	 * @uml.property name="sessionOwner"
	 */
	public G_Player getSessionOwner() {
		return sessionOwner;
	}

	public void open() {
		setActive(true);
	}

	public void setActive(boolean sessionActive) {
		this.sessionActive = sessionActive;
	}

	public void workSession(float radius) {
		List<G_Player> removeList = new Vector<G_Player>();
		Iterator<G_Player> iter = playerList.iterator();

		// S_Server.getInstance().getWorldModule().worldCommand.serverTell(sessionOwner,"Entered");

		while (iter.hasNext()) {

			G_Player player = iter.next();

			if (player.getMap() != sessionOwner.getMap()) {

				S_Client client = S_Server.getInstance().getNetworkModule()
						.getClient(sessionOwner);
				if (client != null) {

					String packetData = "say 1 S_Server (NOTICE) "
							+ player.getName()
							+ "left your sesson (out of range)" + " 1\n";
							client.SendData( packetData);// send
				}
				removeList.add(player);
				continue;
			}
			double xcomp = Math.pow(sessionOwner.getPosX() - player.getPosX(),
					2);

			double ycomp = Math.pow(sessionOwner.getPosY() - player.getPosY(),
					2);

			double distance = Math.sqrt(xcomp + ycomp);

			/*
			 * if(currDistance != distance) { //System.out.println("Session
			 * Radius: "+getSessionRadius()); //System.out.println("Distance:
			 * "+distance);
			 * S_Server.getInstance().getWorldModule().worldCommand.
			 * serverTell(sessionOwner,"Distance: "+(int)distance);
			 * S_Server.getInstance
			 * ().getWorldModule().worldCommand.serverTell(sessionOwner,"Players
			 * in Your session:
			 * "+sessionOwner.getPlayerSession().getPlayerListSize());
			 * currDistance = distance; }
			 */

			if (distance > radius) {
				S_Client client = S_Server.getInstance().getNetworkModule()
						.getClient(sessionOwner);
				if (client != null) {

					String packetData = "say 1 S_Server (NOTICE) "
							+ player.getName()
							+ "left your sesson (out of range)" + " 1\n";
							client.SendData( packetData);// send
				}
				removeList.add(player);
				S_Server.getInstance()
						.getWorldModule()
						.getWorldCommand()
						.serverTell(sessionOwner,
								"Removing Player: " + player.getName());
				// System.out.println("Exit moved out of range " +distance+ " /
				// "+radius );

			}

		}

		Iterator<G_Player> rIter = removeList.iterator();
		while (rIter.hasNext()) {
			G_Player player = rIter.next();
			exitPlayer(player);
			// System.out.println(player.getPlayerName()+ " exits
			// "+sessionOwner.getPlayerName()+" (out of range)");
			// for(int i=0;i<removeList.size();i++)
			// exitPlayer((Player)removeList.get(i));

		}

	}
}