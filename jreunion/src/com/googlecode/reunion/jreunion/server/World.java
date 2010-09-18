package com.googlecode.reunion.jreunion.server;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;

/**
 * @author Autumn
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class World extends ClassModule {
	private Command worldCommand;

	private PlayerManager playerManager;

	private SessionManager sessionManager;

	private MobManager mobManager;

	private Map mapManager;
	
	private TeleportManager teleportManager;
	
	java.util.Map<Integer,Map> maps = new Hashtable<Integer,Map>();	

	private NpcManager npcManager;

	private Timer serverTime = new Timer();

	private int serverHour;

	private boolean mobMoving = false;

	static public ServerSetings serverSetings;

	public World(Module parent) {
		super(parent);
		worldCommand = new Command(this);
		playerManager = new PlayerManager();
		sessionManager = new SessionManager(this);
		mobManager = new MobManager();
		mapManager = new Map(4);
		npcManager = new NpcManager();
		serverHour = 4;
		teleportManager = new TeleportManager();
		serverSetings = new ServerSetings();
	}

	/**
	 * @return Returns the mapManager.
	 */
	public Collection<Map> getMaps() {
		return maps.values();
	}
	public Map getMap(int mapId){		
		return maps.get(mapId);	
	}
	
	public TeleportManager getTeleportManager() {
		return teleportManager;
	}

	/**
	 * @return Returns the mobManager.
	 */
	public MobManager getMobManager() {
		return mobManager;
	}

	/**
	 * @return Returns the npcManager.
	 */
	public NpcManager getNpcManager() {
		return npcManager;
	}

	/**
	 * @return Returns the playerManager.
	 */
	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	/**
	 * @return Returns the serverSetings.
	 */
	public ServerSetings getServerSetings() {
		return serverSetings;
	}

	/**
	 * @return Returns the sessionManager.
	 */
	public SessionManager getSessionManager() {
		return sessionManager;
	}

	/**
	 * @return Returns the worldCommand.
	 */
	public Command getWorldCommand() {
		return worldCommand;
	}

	@Override
	public void start() {
		
		Iterator<ParsedItem> iter = Reference.getInstance().getMapConfigReference().getItemListIterator();
		while(iter.hasNext()){
			ParsedItem item = iter.next();			
			int mapId = Integer.parseInt(item.getMemberValue("Id"));
			Map map = new Map(mapId);
			map.load();
			maps.put(mapId, map);
		}
	}

	@Override
	public void stop() {

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
			Iterator<Mob> mobsIter = Server.getInstance().getWorldModule()
					.getMobManager().getMobListIterator();
			while (mobsIter.hasNext()) {
				Server.getInstance().getWorldModule().getMobManager()
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

			Iterator<Player> iter = playerManager.getPlayerListIterator();
			while (iter.hasNext()) {
				Player player = iter.next();
				Client client = Server.getInstance().getNetworkModule()
						.getClient(player);

				if (client == null) {
					continue;
				}

				if (client.getState() == Client.State.INGAME) {
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
