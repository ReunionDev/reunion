package com.googlecode.reunion.jreunion.server;

import java.net.Socket;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.Vector;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.client.ClientConnectEvent;
import com.googlecode.reunion.jreunion.events.client.ClientDisconnectEvent;
import com.googlecode.reunion.jreunion.events.client.ClientSendEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkAcceptEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDataEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDisconnectEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkSendEvent;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Autumn
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class World extends ClassModule implements EventListener{
	private Command worldCommand;

	private PlayerManager playerManager;

	private SessionManager sessionManager;

	private MobManager mobManager;
	
	private TeleportManager teleportManager;
	
	java.util.Map<Integer,Map> maps = new Hashtable<Integer,Map>();	

	java.util.Map<Socket,Client> clients = new Hashtable<Socket, Client>();

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
	public Command getCommand() {
		return worldCommand;
	}

	@Override
	public void start() {

		Server.getInstance().getNetwork().addEventListener(NetworkAcceptEvent.class, this);
		Server.getInstance().getNetwork().addEventListener(NetworkDisconnectEvent.class, this);
		this.addEventListener(ClientConnectEvent.class, Server.getInstance().getPacketParser());
		
		Iterator<ParsedItem> iter = Reference.getInstance().getMapConfigReference().getItemListIterator();
		while(iter.hasNext()){
			ParsedItem item = iter.next();			
			int mapId = Integer.parseInt(item.getMemberValue("Id"));
			boolean isLocal = item.getMemberValue("Location").equalsIgnoreCase("Local");
			Map map = null;
			if(isLocal){
				map = new LocalMap(this, mapId);
			}
			else {
				map = new RemoteMap(mapId);				
			}
			map.load();
			maps.put(mapId, map);
		}
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				serverHour = (serverHour + 1) % 5;
				
				synchronized(playerManager){
					Iterator<Player> iter = playerManager.getPlayerListIterator();
					while (iter.hasNext()) {
						Player player = iter.next();
						Client client = player.getClient();
						client.SendPacket(Type.HOUR, serverHour);
					}
				}				
			}
		}, 0, 60 * 1000);
	}

	@Override
	public void stop() {
		Server.getInstance().getNetwork().removeEventListener(NetworkAcceptEvent.class, this);
	}

	@Override
	public void Work() {

		sessionManager.workSessions();
		//mapManager.workSpawns();

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
		 * }
		 */

		if ((int) (serverTime.getTimeElapsedSeconds() % 2) == 0
				&& mobMoving == false) {
			Iterator<Mob> mobsIter = Server.getInstance().getWorld()
					.getMobManager().getMobListIterator();
			while (mobsIter.hasNext()) {
				Server.getInstance().getWorld().getMobManager()
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
	
		}

		if (!serverTime.isRunning()) {
			serverTime.Start();
		}
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof NetworkEvent){
			
			Socket socket = ((NetworkEvent)event).getSocket();
			
			if(event instanceof NetworkAcceptEvent){
				NetworkAcceptEvent networkAcceptEvent = (NetworkAcceptEvent) event;
				Network network = (Network) networkAcceptEvent.getSource();
				Client client = new Client(this);
				
				client.addEventListener(NetworkSendEvent.class, network);
							
				client.setSocket(socket);
				
				network.addEventListener(NetworkDataEvent.class, client, new NetworkEvent.NetworkFilter(socket));
				
				System.out.print("Got connection from " + socket+"\n");
				
				client.setState(Client.State.ACCEPTED);
				
				clients.put(socket, client);
				
				fireEvent(ClientConnectEvent.class, client);
			}
			if(event instanceof NetworkDisconnectEvent){
				Client client = clients.remove(socket);
				
			}
		}
	}

	public java.util.Map<Socket, Client> getClients() {
		return clients;
	}

}
