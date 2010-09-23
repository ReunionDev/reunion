package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class SessionManager {

	private float sessionRadius = -1;

	private java.util.List<Session> sessionList = new Vector<Session>();

	private com.googlecode.reunion.jreunion.server.World world;

	private Timer statusUpdateTime = new Timer();

	public SessionManager(com.googlecode.reunion.jreunion.server.World world) {
		this.world = world;
	}

	public void addSession(Session session) {
		sessionList.add(session);
	}

	public int getNumberOfSessions() {
		return sessionList.size();
	}

	public Session getSession(int index) {
		return sessionList.get(index);
	}

	public Iterator<Session> getSessionListIterator() {
		return sessionList.iterator();
	}

	
	public Session newSession(Player player) {
		Session s = new Session(player);
		addSession(s);
		return s;

	}

	public void removeSession(Session session) {
		sessionList.remove(session);
	}

	public synchronized void workSessions() {
		/*
		 * Iterator iter = sessionList.iterator(); while (iter.hasNext()) {
		 * Session session = (Session) iter.next();
		 * session.WorkSession(getSessionRadius()); }
		 */
		
		PlayerManager manager = Server.getInstance()
		.getWorld().getPlayerManager();
		Iterator<Player> player1Iter = manager.getPlayerListIterator();

		while (player1Iter.hasNext()) {
			Player player1 = player1Iter.next();

			if (statusUpdateTime.getTimeElapsedSeconds() >= 10) {
				Client client = player1.getClient();
				if (client == null) {
					continue;
				}
				statusUpdateTime.Stop();
				statusUpdateTime.Reset();
				
				//TODO: Move regen somewhere sane!
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
				
			}
			if (!statusUpdateTime.isRunning()) {
				statusUpdateTime.Start();
			}

			Iterator<Player> player2Iter = world.getPlayerManager()
					.getPlayerListIterator();
			
			while (player2Iter.hasNext()) {
				Player player2 = player2Iter.next();

				if (player1 == player2) {
					continue;
				}
				if (player1.getPosition().getMap() != player2.getPosition().getMap()) {
					continue;
				}
			
				double distance = player1.getPosition().distance(player2.getPosition());

				if (distance <= player1.getSessionRadius()) {
					player1.getSession().enter(player2);
				}

				if (distance > player1.getSessionRadius()) {
					player1.getSession().exit(player2);
				}
			}

			Iterator<Mob> mobIter = world.getMobManager()
					.getMobListIterator();
			while (mobIter.hasNext()) {
				Mob mob = mobIter.next();

				if (mob == null) {
					continue;
				}

				if (mob.getPosition().getMap() != player1.getPosition().getMap()) {
					continue;
				}

				double distance = mob.getPosition().distance(player1.getPosition());

				if (distance > player1.getSessionRadius()) {
					player1.getSession().exit(mob);
				} else {
					player1.getSession().enter(mob);
					/*
					if (distance <= 150 && mob.getAttackType() != -1) {
						if (player1.getPosition().getMap()
								.getMobArea()
								.get((player1.getPosition().getX() / 10 - 300),
										(player1.getPosition().getY() / 10)) == true) {
							mob.moveToPlayer(player1, distance);
						}
					}
					*///TODO: Crashes server because of the broken session implementation
				}
			}

			Iterator<Npc> npcIter = world.getNpcManager()
					.getNpcListIterator();
			while (npcIter.hasNext()) {
				Npc npc = npcIter.next();

				if (npc == null) {
					continue;
				}

				if (npc.getPosition().getMap() != player1.getPosition().getMap()) {
					continue;
				}

				double distance = npc.getPosition().distance(player1.getPosition());

				if (distance > player1.getSessionRadius()) {
					player1.getSession().exit(npc);
				} else {
					player1.getSession().enter(npc);
				}
			}

		}
	}
}
