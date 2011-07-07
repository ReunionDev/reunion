package com.googlecode.reunion.jreunion.server;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventDispatcher;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.client.ClientConnectEvent;
import com.googlecode.reunion.jreunion.events.client.ClientDisconnectEvent;
import com.googlecode.reunion.jreunion.events.client.ClientSendEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkAcceptEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDataEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDisconnectEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkSendEvent;
import com.googlecode.reunion.jreunion.events.server.ServerEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStartEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStopEvent;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Server.State;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class World extends EventDispatcher implements EventListener, Sendable {
	
	private Command worldCommand;

	private PlayerManager playerManager;
	
	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	
	private TeleportManager teleportManager;
	
	private Hashtable<Integer, Map> maps = new Hashtable<Integer,Map>();	

	private Hashtable<SocketChannel, Client> clients = new Hashtable<SocketChannel, Client>();

	private int serverHour;

	private SkillManager skillManager;

	public SkillManager getSkillManager() {
		return skillManager;
	}

	static public ServerSetings serverSetings;

	public World(Server server) {
		
		worldCommand = new Command(this);
		skillManager = new SkillManager();
		playerManager = new PlayerManager();
		serverHour = 4;
		teleportManager = new TeleportManager();
		serverSetings = new ServerSetings();		
		server.addEventListener(ServerEvent.class, this);
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
	/*
	public MobManager getMobManager() {
		return mobManager;
	}
	*/

	/**
	 * @return Returns the npcManager.
	 */
	
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
	 * @return Returns the worldCommand.
	 */
	public Command getCommand() {
		return worldCommand;
	}

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
		
		executorService.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				serverHour = (serverHour + 1) % 5;
				
				synchronized(playerManager){
					Iterator<Player> iter = playerManager.getPlayerListIterator();
					while (iter.hasNext()) {
						Player player = iter.next();
						Client client = player.getClient();
						client.sendPacket(Type.HOUR, serverHour);
					}
				}				
			}
		}, 0, 60, TimeUnit.SECONDS);		
		
		executorService.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				synchronized(playerManager){
					Iterator<Player> iter = playerManager.getPlayerListIterator();
					while (iter.hasNext()) {
						Player player = iter.next();
						
						synchronized(player){
							int maxHp = player.getMaxHp();
							player.setHp(player.getHp()+Tools.between(maxHp/100, 1, maxHp));							
							
							int maxMana = player.getMaxMana();
							player.setMana(player.getMana()+Tools.between(maxMana/100, 1, maxMana));	
							
							int maxStamina = player.getMaxStamina();
							player.setStamina(player.getStamina()+Tools.between(maxStamina/100, 1, maxStamina));	
							
							int maxElectricity = player.getMaxElectricity();
							player.setElectricity(player.getElectricity()+Tools.between(maxElectricity/100, 1, maxElectricity));	
						}
					}
				}				
			}
		}, 0, 3, TimeUnit.SECONDS);
	
	}

	public void stop() {
		executorService.shutdown();
		Server.getInstance().getNetwork().removeEventListener(NetworkAcceptEvent.class, this);
	}

	@Override
	public void handleEvent(Event event) {
		
		if(event instanceof ServerEvent){
			
			if (event instanceof ServerStartEvent) {
				start();
			}
			if (event instanceof ServerStopEvent) {
				stop();
			}			
		}
		
		if(event instanceof NetworkEvent){
			
			SocketChannel socketChannel = ((NetworkEvent)event).getSocketChannel();
			
			if(event instanceof NetworkAcceptEvent) {
				
				NetworkAcceptEvent networkAcceptEvent = (NetworkAcceptEvent) event;
				Network network = (Network) networkAcceptEvent.getSource();
				Client client = new Client(this, socketChannel);
				
				client.addEventListener(NetworkSendEvent.class, network);
															
				network.addEventListener(NetworkDataEvent.class, client, new NetworkEvent.NetworkFilter(socketChannel));
				
				Logger.getLogger(World.class).debug("Got connection from " + socketChannel+"\n");
				
				client.setState(Client.State.ACCEPTED);
				
				clients.put(socketChannel, client);
				
				fireEvent(ClientConnectEvent.class, client);
								
			}
			if(event instanceof NetworkDisconnectEvent){
				Client client = clients.remove(socketChannel);
				
			}
		}
	}

	public java.util.Map<SocketChannel, Client> getClients() {
		return clients;
	}

	@Override
	public void sendPacket(Type packetType, Object... args) {
		synchronized(playerManager){
			
			Iterator<Player> playerIter =  playerManager.getPlayerListIterator();
			while(playerIter.hasNext()){
				Player player = playerIter.next();
				player.getClient().sendPacket(packetType, args);
			}
		}
		
	}

}
