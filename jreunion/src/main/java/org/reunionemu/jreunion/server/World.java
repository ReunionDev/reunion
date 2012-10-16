package org.reunionemu.jreunion.server;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.events.EventDispatcher;
import org.reunionemu.jreunion.events.EventListener;
import org.reunionemu.jreunion.events.client.ClientConnectEvent;
import org.reunionemu.jreunion.events.network.NetworkAcceptEvent;
import org.reunionemu.jreunion.events.network.NetworkDataEvent;
import org.reunionemu.jreunion.events.network.NetworkDisconnectEvent;
import org.reunionemu.jreunion.events.network.NetworkEvent;
import org.reunionemu.jreunion.events.network.NetworkSendEvent;
import org.reunionemu.jreunion.events.server.ServerEvent;
import org.reunionemu.jreunion.events.server.ServerStartEvent;
import org.reunionemu.jreunion.events.server.ServerStopEvent;
import org.reunionemu.jreunion.game.BulkanPlayer;
import org.reunionemu.jreunion.game.ItemType;
import org.reunionemu.jreunion.game.Pet;
import org.reunionemu.jreunion.game.Pet.PetStatus;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.skills.bulkan.RecoveryBoost;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
@DependsOn("database")
@Service
public class World extends EventDispatcher implements EventListener, Sendable {
	
	private Command worldCommand;

	private PlayerManager playerManager;
	
	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
	
	private TeleportManager teleportManager;
	
	private Hashtable<Integer, Map> maps = new Hashtable<Integer,Map>();	

	private Hashtable<SocketChannel, Client> clients = new Hashtable<SocketChannel, Client>();

	private int serverHour;

	private SkillManager skillManager;
		
	private ItemManager itemManager;
	
	private NpcManager npcManager;
	
	private PetManager petManager;

	static public ServerSetings serverSetings;
	
	@Autowired
	private Server server;

	public World() {
		
	
	}
	
	@PostConstruct
	public void init(){
		serverSetings = new ServerSetings();
		worldCommand = new Command(this);
		skillManager = new SkillManager();
		playerManager = new PlayerManager();
		itemManager = new ItemManager();
		new ItemType(229);
		npcManager = new NpcManager();
		petManager = new PetManager();
		petManager.loadPets();
		serverHour = 4;
		teleportManager = new TeleportManager();				
		server.addEventListener(ServerEvent.class, this);
		
	}

	public SkillManager getSkillManager() {
		return skillManager;
	}
	
	public ItemManager getItemManager() {
		return itemManager;
	}
	
	public NpcManager getNpcManager() {
		return npcManager;
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
		
		executorService.scheduleAtFixedRate(new REHandler(new Runnable() {
			
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
		}), 0, 60, TimeUnit.SECONDS);		
		
		//work player stats
		executorService.scheduleAtFixedRate(new REHandler(new Runnable() {
			
			@Override
			public void run() {
				synchronized(playerManager){
					//Iterator<Player> iter = playerManager.getPlayerListIterator();
					//while (iter.hasNext()) {
					//	Player player = iter.next();
					List<Player> playersList = playerManager.getPlayerList();	
					for(Player player : playersList){
						synchronized(player){
							float statusModifier = 0.1f; //increase 10% of status 
							
							long maxHp = player.getMaxHp();
							long hpModifier = (long)(maxHp * statusModifier); //increase 10% of Hp
					
							if(player instanceof BulkanPlayer){
								RecoveryBoost recoveryBoost = (RecoveryBoost)player.getSkill(19);
								hpModifier *= recoveryBoost.getRecoveryBoostModifier(player); //boost HP modifier;
							}
							
							player.setHp(player.getHp()+hpModifier);
							
							long maxMana = player.getMaxMana();
							long manaModifier = (long)(maxMana * statusModifier); //increase 10% of Mana
							player.setMana(player.getMana()+ manaModifier);	
							
							long maxStamina = player.getMaxStamina();
							long staminaModifier = (long)(maxStamina * statusModifier); //increase 10% of Stamina
							player.setStamina(player.getStamina()+staminaModifier);	
							
							long maxElectricity = player.getMaxElectricity();
							long electricityModifier = (long)(maxElectricity * statusModifier); //increase 10% of Electricity
							player.setElectricity(player.getElectricity()+electricityModifier);	
						}
					}
				}				
			}
		}), 0, 7, TimeUnit.SECONDS);
	
		// work pet stats
		if(playerManager.getNumberOfPlayers() > 0){
			executorService.scheduleAtFixedRate(new REHandler(new Runnable() {

				@Override
				public void run() {
					List<Pet> petList = null;
					synchronized (petManager) {
						petList = new Vector<Pet>(petManager.getList());
					}

					for (Pet pet : petList) {
						if (playerManager.isPetOwnerOnline(pet.id) && pet.getState() == 12) {
							long petMaxHp = pet.getMaxHp();
							long petHpModifier = (long) (petMaxHp * 0.1); // increase 10% of hp
							pet.setHp(pet.getHp() + petHpModifier);
							pet.setLoyalty(pet.getLoyalty());
							pet.setSatiety(pet.getSatiety());

							pet.sendStatus(PetStatus.HP);
							pet.sendStatus(PetStatus.LOYALTY);
							pet.sendStatus(PetStatus.SATIETY);
						}
					}
				}
			}), 0, 10, TimeUnit.SECONDS);
		}
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
				
				LoggerFactory.getLogger(World.class).info("Got connection from {local="
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

	public PetManager getPetManager() {
		return petManager;
	}

	public void setPetManager(PetManager petManager) {
		this.petManager = petManager;
	}

}
