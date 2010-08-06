package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.G_Mob;
import com.googlecode.reunion.jreunion.game.G_Npc;
import com.googlecode.reunion.jreunion.game.G_Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_SessionManager {

	private float sessionRadius = -1;

	private java.util.List<S_Session> sessionList = new Vector<S_Session>();

	private com.googlecode.reunion.jreunion.server.S_World world;

	private S_Timer statusUpdateTime = new S_Timer();

	public S_SessionManager(com.googlecode.reunion.jreunion.server.S_World world) {
		this.world = world;
	}

	public void addSession(S_Session session) {
		sessionList.add(session);
	}

	public int getNumberOfSessions() {
		return sessionList.size();
	}

	public S_Session getSession(int index) {
		return sessionList.get(index);
	}

	public Iterator<S_Session> getSessionListIterator() {
		return sessionList.iterator();
	}

	public float getSessionRadius() {
		if (sessionRadius < 0) {
			sessionRadius = S_DatabaseUtils.getInstance().getSessionRadius();
		}
		return sessionRadius;
	}

	public S_Session newSession(G_Player player) {
		S_Session s = new S_Session(player);
		addSession(s);
		return s;

	}

	public void removeSession(S_Session session) {
		sessionList.remove(session);
	}

	public void workSessions() {
		/*
		 * Iterator iter = sessionList.iterator(); while (iter.hasNext()) {
		 * Session session = (Session) iter.next();
		 * session.WorkSession(getSessionRadius()); }
		 */

		Iterator<G_Player> player1Iter = S_Server.getInstance()
				.getWorldModule().getPlayerManager().getPlayerListIterator();

		while (player1Iter.hasNext()) {
			G_Player player1 = player1Iter.next();

			if (statusUpdateTime.getTimeElapsedSeconds() >= 10) {
				S_Client client = S_Server.getInstance().getNetworkModule()
						.getClient(player1);
				if (client == null) {
					continue;
				}
				statusUpdateTime.Stop();
				statusUpdateTime.Reset();
				// if(client.getState() == S_Enums.CS_INGAME){
				player1.updateStatus(0,
						player1.getCurrHp() + (int) (player1.getMaxHp() * 0.1),
						player1.getMaxHp());
				player1.updateStatus(1,
						player1.getCurrMana()
								+ (int) (player1.getMaxMana() * 0.08),
						player1.getMaxMana());
				player1.updateStatus(2,
						player1.getCurrStm()
								+ (int) (player1.getMaxStm() * 0.08),
						player1.getMaxStm());
				player1.updateStatus(3,
						player1.getCurrElect()
								+ (int) (player1.getMaxElect() * 0.08),
						player1.getMaxElect());
				// }
			}
			if (!statusUpdateTime.isRunning()) {
				statusUpdateTime.Start();
			}

			Iterator<G_Player> player2Iter = world.getPlayerManager()
					.getPlayerListIterator();
			while (player2Iter.hasNext()) {
				G_Player player2 = player2Iter.next();

				if (player1 == player2) {
					continue;
				}
				if (player1.getMap() != player2.getMap()) {
					continue;
				}

				double xcomp = Math.pow(player1.getPosX() - player2.getPosX(),
						2);
				double ycomp = Math.pow(player1.getPosY() - player2.getPosY(),
						2);
				double distance = Math.sqrt(xcomp + ycomp);

				if (distance <= getSessionRadius()) {
					player1.getSession().enterPlayer(player2, 0);
				}

				if (distance > getSessionRadius()) {
					player1.getSession().exitPlayer(player2);
				}
			}

			Iterator<G_Mob> mobIter = world.getMobManager()
					.getMobListIterator();
			while (mobIter.hasNext()) {
				G_Mob mob = mobIter.next();

				if (mob == null) {
					continue;
				}

				if (mob.getMap() != player1.getMap()) {
					continue;
				}

				int distance = mob.getDistance(player1);

				if (distance > getSessionRadius()) {
					player1.getSession().exitMob(mob);
				} else {
					player1.getSession().enterMob(mob, 0);
					if (distance <= 150 && mob.getAttackType() != -1) {
						if (S_Server
								.getInstance()
								.getWorldModule()
								.getMapManager()
								.getMobArea()
								.get((player1.getPosX() / 10 - 300),
										(player1.getPosY() / 10)) == true) {
							mob.moveToPlayer(player1, distance);
						}
					}
				}
			}

			Iterator<G_Npc> npcIter = world.getNpcManager()
					.getNpcListIterator();
			while (npcIter.hasNext()) {
				G_Npc npc = npcIter.next();

				if (npc == null) {
					continue;
				}

				if (npc.getMap() != player1.getMap()) {
					continue;
				}

				int distance = npc.getDistance(player1);

				if (distance > getSessionRadius()) {
					player1.getSession().exitNpc(npc);
				} else {
					player1.getSession().enterNpc(npc);
				}
			}

		}
	}
}
