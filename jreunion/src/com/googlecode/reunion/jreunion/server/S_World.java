package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;

import com.googlecode.reunion.jreunion.game.G_Mob;
import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.server.S_Enums.S_ClientState;

/**
 * @author Autumn
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_World extends S_ClassModule {
	private S_Command worldCommand;

	private S_PlayerManager playerManager;

	private S_SessionManager sessionManager;

	private S_MobManager mobManager;

	private S_Map mapManager;

	private S_NpcManager npcManager;

	private S_Timer serverTime = new S_Timer();

	private int serverHour;

	private boolean mobMoving = false;

	static public S_ServerSetings serverSetings;

	public S_World(S_Module parent) {
		super(parent);
		worldCommand = new S_Command(this);
		playerManager = new S_PlayerManager();
		sessionManager = new S_SessionManager(this);
		mobManager = new S_MobManager();
		mapManager = new S_Map(4);
		npcManager = new S_NpcManager();
		serverHour = 4;
		serverSetings = new S_ServerSetings();
	}

	/**
	 * @return Returns the mapManager.
	 */
	public S_Map getMapManager() {
		return mapManager;
	}

	/**
	 * @return Returns the mobManager.
	 */
	public S_MobManager getMobManager() {
		return mobManager;
	}

	/**
	 * @return Returns the npcManager.
	 */
	public S_NpcManager getNpcManager() {
		return npcManager;
	}

	/**
	 * @return Returns the playerManager.
	 */
	public S_PlayerManager getPlayerManager() {
		return playerManager;
	}

	/**
	 * @return Returns the serverSetings.
	 */
	public S_ServerSetings getServerSetings() {
		return serverSetings;
	}

	/**
	 * @return Returns the sessionManager.
	 */
	public S_SessionManager getSessionManager() {
		return sessionManager;
	}

	/**
	 * @return Returns the worldCommand.
	 */
	public S_Command getWorldCommand() {
		return worldCommand;
	}

	@Override
	public void Start() {

		mapManager.load();
	}

	@Override
	public void Stop() {

	}

	@Override
	public void Work() {

		sessionManager.workSessions();
		mapManager.workSpawns();

		/*
		 * Iterator mobsIter =
		 * S_Server.getInstance().getWorldModule().getMobManager
		 * ().getMobListIterator();
		 * 
		 * while(mobsIter.hasNext()){
		 * 
		 * G_Mob mob = (G_Mob)mobsIter.next();
		 * if(mob.getTimer().getTimeElapsedSeconds() > 2){ mob.setIsMoving(1);
		 * mob.getTimer().Stop(); mob.getTimer().Reset(); } if(mob.getIsMoving()
		 * == 0){ mob.setIsMoving(1); mob.getTimer().Start();
		 * S_Server.getInstance().getWorldModule().getMobManager().workMob(mob);
		 * }
		 * 
		 * }
		 */

		if ((int) (serverTime.getTimeElapsedSeconds() % 2) == 0
				&& mobMoving == false) {
			Iterator<G_Mob> mobsIter = S_Server.getInstance().getWorldModule()
					.getMobManager().getMobListIterator();
			while (mobsIter.hasNext()) {
				S_Server.getInstance().getWorldModule().getMobManager()
						.workMob(mobsIter.next());
			}
			mobMoving = true;
		}

		if ((int) (serverTime.getTimeElapsedSeconds() % 2) != 0
				&& mobMoving == true) {
			mobMoving = false;
		}

		if ((int) serverTime.getTimeElapsedSeconds() >= 60) {
			serverTime.Stop();
			serverTime.Reset();

			serverHour = (serverHour + 1) % 5;

			Iterator<G_Player> iter = playerManager.getPlayerListIterator();
			while (iter.hasNext()) {
				G_Player player = iter.next();
				S_Client client = S_Server.getInstance().getNetworkModule()
						.getClient(player);

				if (client == null) {
					continue;
				}

				if (client.getState() == S_ClientState.INGAME) {
							client.SendData(
									"hour " + serverHour + "\n");
				}
			}
		}

		if (!serverTime.isRunning()) {
			serverTime.Start();
		}
	}

}
