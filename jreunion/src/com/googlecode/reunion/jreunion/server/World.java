package com.googlecode.reunion.jreunion.server;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventDispatcher;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.client.ClientConnectEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkAcceptEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDataEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDisconnectEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkSendEvent;
import com.googlecode.reunion.jreunion.events.server.ServerEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStartEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStopEvent;
import com.googlecode.reunion.jreunion.game.BulkanPlayer;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.skills.bulkan.RecoveryBoost;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

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
	
	private QuestManager questManager;
	
	private ItemManager itemManager;

	static public ServerSetings serverSetings;

	public World(Server server) {
		
		worldCommand = new Command(this);
		skillManager = new SkillManager();
		questManager = new QuestManager();
		playerManager = new PlayerManager();
		itemManager = new ItemManager();
		serverHour = 4;
		teleportManager = new TeleportManager();
		serverSetings = new ServerSetings();		
		server.addEventListener(ServerEvent.class, this);
	}

	public SkillManager getSkillManager() {
		return skillManager;
	}
	
	public QuestManager getQuestManager() {
		return questManager;
	}
	
	public ItemManager getItemManager() {
		return itemManager;
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
							float statusModifier = 0.1f; //increase 10% of status 
							
							int maxHp = player.getMaxHp();
							int hpModifier = (int)(maxHp * statusModifier); //increase 10% of Hp
					
							if(player instanceof BulkanPlayer){
								RecoveryBoost recoveryBoost = (RecoveryBoost)player.getSkill(19);
								hpModifier *= recoveryBoost.getRecoveryBoostModifier(player); //boost HP modifier;
							}
							
							player.setHp(player.getHp()+hpModifier);
							
							int maxMana = player.getMaxMana();
							int manaModifier = (int)(maxMana * statusModifier); //increase 10% of Mana
							player.setMana(player.getMana()+manaModifier);	
							
							int maxStamina = player.getMaxStamina();
							int staminaModifier = (int)(maxStamina * statusModifier); //increase 10% of Stamina
							player.setStamina(player.getStamina()+staminaModifier);	
							
							int maxElectricity = player.getMaxElectricity();
							int electricityModifier = (int)(maxElectricity * statusModifier); //increase 10% of Electricity
							player.setElectricity(player.getElectricity()+electricityModifier);	
						}
					}
				}				
			}
		}, 0, 10, TimeUnit.SECONDS);
	
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
				
				Logger.getLogger(World.class).info("Got connection from {local="
						+ socketChannel.socket().getLocalSocketAddress()+" remote="
						+socketChannel.socket().getRemoteSocketAddress()+"}\n");
				
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
