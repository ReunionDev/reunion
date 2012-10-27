package org.reunionemu.jreunion.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jcommon.Parser;
import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.events.map.ItemDropEvent;
import org.reunionemu.jreunion.events.map.ItemPickupEvent;
import org.reunionemu.jreunion.events.map.MapEvent;
import org.reunionemu.jreunion.events.map.PlayerLoginEvent;
import org.reunionemu.jreunion.events.map.PlayerLogoutEvent;
import org.reunionemu.jreunion.events.map.SpawnEvent;
import org.reunionemu.jreunion.events.session.NewSessionEvent;
import org.reunionemu.jreunion.events.session.SessionEvent;
import org.reunionemu.jreunion.game.Entity;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Npc;
import org.reunionemu.jreunion.game.NpcSpawn;
import org.reunionemu.jreunion.game.NpcType;
import org.reunionemu.jreunion.game.Party;
import org.reunionemu.jreunion.game.Pet;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.PlayerSpawn;
import org.reunionemu.jreunion.game.Position;
import org.reunionemu.jreunion.game.RoamingItem;
import org.reunionemu.jreunion.game.Shop;
import org.reunionemu.jreunion.game.Spawn;
import org.reunionemu.jreunion.game.WorldObject;
import org.reunionemu.jreunion.game.npc.Mob;
import org.reunionemu.jreunion.server.Area.Field;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class LocalMap extends Map implements Runnable{

	private List<Spawn> npcSpawnList = new Vector<Spawn>();
	
	private List<Spawn> playerSpawnList = new Vector<Spawn>();
	
	private Area area = new Area();
	
	private SessionList<Session> sessions = new SessionList<Session>();
	
	private HashMap<Integer, Entity> entities = new HashMap<Integer, Entity>();
		
	private Parser playerSpawnReference;

	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	
	 private ScheduledFuture<?> mobsAI;

	private Thread thread;

	private Parser mobSpawnReference;

	private Parser npcSpawnReference;
	
	private PlayerSpawn defaultSpawn;

	private World world;
	
	private List<RoamingItem> roamingItemList = new Vector<RoamingItem>();
	
	private List<Party> parties = new Vector<Party>();
	
	private List<Shop> shops = new Vector<Shop>();

	public World getWorld() {
		return world;
	}

	public LocalMap(World world, int id) {
		super(id);
		this.world = world;
		
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
		this.addEventListener(SpawnEvent.class, this);
		this.addEventListener(PlayerLoginEvent.class, this);
		this.addEventListener(PlayerLogoutEvent.class, this);
		this.addEventListener(ItemDropEvent.class, this);
		this.addEventListener(ItemPickupEvent.class, this);
		
		//if(world.getQuestManager().isEmpty())
		//	world.getQuestManager().loadQuests();
	}
	
	public Entity getEntity(int id) {		
		synchronized(entities){			
			return (Entity) entities.get(id);
		}
	}
	
	public int getEntitiesListSize(){
		return entities.size();
	}
	
	private int entityIter = 0;
	
	public synchronized int createEntityId(Entity obj){
		synchronized(this.entities){
			if(!entities.containsValue(obj)){
				int counter = 0;
				
				while(entities.containsKey(entityIter)){
					entityIter++;
					if(entityIter >= Integer.MAX_VALUE) {
						entityIter=0;
					}
					counter++;
					if(counter >= Integer.MAX_VALUE)
						throw new RuntimeException("No more available entity ids");
				}
				
				int id = entityIter;
				obj.setEntityId(id);
				entities.put(id, obj);
				entityIter++;
							
				return id;
			
			} else {
				return obj.getEntityId();
			}
		}
	}
	
	private void createMobSpawns() {
		
		Iterator<ParsedItem> iter = mobSpawnReference.getItemListIterator();

		while (iter.hasNext()) {

			ParsedItem item = iter.next();

			if (!item.checkMembers(new String[] { "ID", "X", "Y",
					"Radius", "RespawnTime", "Type" })) {
				LoggerFactory.getLogger(LocalMap.class).info("Error loading a mob spawn on map: "
						+ getId());
				continue;
			}

			NpcSpawn spawn = new NpcSpawn();
			spawn.setId(Integer.parseInt(item.getMemberValue("Id")));
			
			int posZ = item.getMemberValue("Z") == null ? 0 : Integer.parseInt(item.getMemberValue("Z"));
			String rotValue = item.getMemberValue("Rotation");
			double rotation = rotValue == null ? Double.NaN : Double.parseDouble(rotValue);
			
			Position position = new Position(
					Integer.parseInt(item.getMemberValue("X")),
					Integer.parseInt(item.getMemberValue("Y")),
					posZ,
					this,
					rotation);
			
			spawn.setPosition(position);
			spawn.setRadius(Integer.parseInt(item.getMemberValue("Radius")));
			spawn.setNpcType(Integer.parseInt(item.getMemberValue("Type")));
			spawn.setRespawnTime(Integer.parseInt(item.getMemberValue("RespawnTime")));
			
			getNpcSpawnList().add(spawn);
			
			spawn.spawn();
		}
		
		LoggerFactory.getLogger(LocalMap.class).info("Loaded "+getNpcSpawnList().size()+" mob spawns in "+getName());
	}

	public Parser getMobSpawnReference() {
		return mobSpawnReference;
	}

	public Parser getNpcSpawnReference() {
		return npcSpawnReference;
	}
	
	public List<RoamingItem> getRoamingItemList() {
		return roamingItemList;
	}
	
	public RoamingItem getRoamingItem(int entityId){
		
		for(RoamingItem roamingItem: getRoamingItemList()){
			if(roamingItem.getItem().getEntityId() == entityId){
				return roamingItem;
			}
		}
		return null;
	}
	
	public void addRoamingItem(RoamingItem roamingItem){
		roamingItemList.add(roamingItem);	
	}
	
	public void removeRoamingItem(RoamingItem roamingItem){
		roamingItemList.remove(roamingItem);
	}
	
	public List<Spawn> getNpcSpawnList(){
		return npcSpawnList;
	}
	
	public List<Spawn> getPlayerSpawnList(){
		return playerSpawnList;
	}
	
	public List<Entity> getEntities(){
		return new Vector<Entity>(entities.values());
	}
	
	private void createNpcSpawns() {
		
		int mobSpawnAmmount = getNpcSpawnList().size();
		Iterator<ParsedItem> iter = npcSpawnReference.getItemListIterator();

		while (iter.hasNext()) {

			ParsedItem item = iter.next();

			if (!item.checkMembers(new String[] { "ID", "X", "Y",
					"Rotation", "Type" })) {
				LoggerFactory.getLogger(LocalMap.class).warn("Failed to load npc spawn {name:"+item.getName()+"} on map "
						+ this);
				continue;
			}
			
			NpcSpawn spawn = new NpcSpawn();
			spawn.setId(Integer.parseInt(item.getMemberValue("Id")));
			
			int posZ = item.getMemberValue("Z") == null ? 0 : Integer.parseInt(item.getMemberValue("Z"));
			double rotation = item.getMemberValue("Rotation") == null ? Double.NaN : Double.parseDouble(item.getMemberValue("Rotation"));
			Position position = new Position(
					Integer.parseInt(item.getMemberValue("X")),
					Integer.parseInt(item.getMemberValue("Y")),
					posZ,
					this,
					rotation);
			
			spawn.setPosition(position);
			spawn.setNpcType(Integer.parseInt(item.getMemberValue("Type")));
			getNpcSpawnList().add(spawn);
			
			spawn.spawn();
		}
		LoggerFactory.getLogger(LocalMap.class).info("Loaded "+(getNpcSpawnList().size()-mobSpawnAmmount)+" npc spawns in "+getName());
	}

	/**
	 * @return Returns the mapid.
	 */
	
	public Area getArea() {
		return area;
	}
	public Parser getPlayerSpawnReference() {
		return playerSpawnReference;
	}

	public void load() {
		super.load();
		thread.setName("Map: "+getName());
		
		LoggerFactory.getLogger(LocalMap.class).info("Loading "+getName()+"["+getId()+"]");
		if(!Server.getInstance().getNetwork().register(getAddress())){
			System.out.println("huh?");
			return;
		}
		
		playerSpawnReference = new Parser();
		mobSpawnReference = new Parser();
		npcSpawnReference = new Parser();
		loadFromReference(getId());
		getNpcSpawnList().clear();
		createMobSpawns();
		createNpcSpawns();
		createPlayerSpawns();
		
		synchronized(entities){
		
			roamingItemList = DatabaseUtils.getDinamicInstance().loadRoamingItems(this);
			for(RoamingItem roamingItem : roamingItemList){
				//TODO: A better way to manage items going in and out of the map
				int itemEntityId = createEntityId(roamingItem);
				
				roamingItem.getItem().setEntityId(itemEntityId);
				roamingItem.startDeleteTimer(true);
			}
		}
		
		LoggerFactory.getLogger(LocalMap.class).info("Loaded "+getRoamingItemList().size()+" roaming items in "+getName());
			
		//startMobsAI(1000);
		
		/*
		executorService.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				List<Entity> objects = null;
				synchronized(entities){			
					objects = new Vector<Entity>(entities.values());
				}
				
				for (Entity entity : objects) {
					if (entity instanceof Npc) {
						if (((Npc<?>) entity).getType() instanceof Mob) {
							((Npc<?>) entity).work();
						}
					}
				}
			}
		}, 0, 1500, TimeUnit.MILLISECONDS);
		*/
		
		LoggerFactory.getLogger(LocalMap.class).info(getName()+" running on "+getAddress());
		
	}
	
	public void startMobsAI(long period){
		mobsAI = executorService.scheduleAtFixedRate(createMobsAI(), 0, period, TimeUnit.MILLISECONDS);
	}

	public void stopMobsAI(){
		mobsAI.cancel(false);
	}
	
	public ScheduledFuture<?> getMobsAI(){
		return mobsAI;
	}
	
	public Runnable createMobsAI(){
		return new REHandler(new Runnable() {
			@Override
			public void run() {
				List<Entity> objects = null;
				synchronized (entities) {
					objects = new Vector<Entity>(entities.values());
				}
				for (Entity entity : objects) {
					if (entity instanceof Npc) {
						Npc<?> npc = (Npc<?>) entity;
						if (npc.getType() instanceof Mob) {
							npc.work();
						}
					}
				}
			}
		});
	}
	
	private void createPlayerSpawns() {
		int defaultSpawnId = Integer.parseInt(Reference.getInstance().getMapReference().getItemById(this.getId()).getMemberValue("DefaultSpawnId"));
		getPlayerSpawnList().clear();

		Iterator<ParsedItem> iter = playerSpawnReference.getItemListIterator();

		while (iter.hasNext()) {

			ParsedItem item = iter.next();

			if (!item.checkMembers(new String[] { "ID", "X", "Y"
					})) {
				LoggerFactory.getLogger(LocalMap.class).info("Error loading a player spawn on map: "
						+ getId());
				continue;
			}			
			PlayerSpawn spawn = new PlayerSpawn();
			spawn.setId(Integer.parseInt(item.getMemberValue("Id")));
			
			int posZ = item.getMemberValue("Z") == null ? 0 : Integer.parseInt(item.getMemberValue("Z"));
			double rotation = item.getMemberValue("Rotation") == null ? Double.NaN : Double.parseDouble(item.getMemberValue("Rotation"));

			Position position = new Position(
					Integer.parseInt(item.getMemberValue("X")),
					Integer.parseInt(item.getMemberValue("Y")),
					posZ,
					this,
					rotation);
			
			int targetX = item.getMemberValue("TargetX")==null?-1:Integer.parseInt(item.getMemberValue("TargetX"));
			int targetY = item.getMemberValue("TargetY")==null?-1:Integer.parseInt(item.getMemberValue("TargetY"));
			int targetWidth = item.getMemberValue("TargetWidth")==null?-1:Integer.parseInt(item.getMemberValue("TargetWidth")); 
			int targetHeight = item.getMemberValue("TargetHeight")==null?-1:Integer.parseInt(item.getMemberValue("TargetHeight"));
			
			int radius = item.getMemberValue("Radius")==null?0:Integer.parseInt(item.getMemberValue("Radius"));
			
			spawn.setRadius(radius);
			spawn.setTargetX(targetX);
			spawn.setTargetY(targetY);
			spawn.setTargetWidth(targetWidth);
			spawn.setTargetHeight(targetHeight);
			
			spawn.setPosition(position);
			
			if(Integer.parseInt(item.getMemberValue("ID"))==defaultSpawnId){
				
				this.defaultSpawn = spawn;
			}
			getPlayerSpawnList().add(spawn);
		}
	}


	public void loadFromReference(int id) {
		try{
			playerSpawnReference.Parse(Reference.getDataResource(Reference.getInstance().getMapReference()
					.getItemById(id).getMemberValue("PlayerSpawn")));
			//Reference.getDataResource(filename)
			mobSpawnReference.Parse(Reference.getDataResource(Reference.getInstance().getMapReference()
					.getItemById(id).getMemberValue("MobSpawn")));
			npcSpawnReference.Parse(Reference.getDataResource(Reference.getInstance().getMapReference()
					.getItemById(id).getMemberValue("NpcSpawn")));
			
		} catch(Exception e){			
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);			
		}
		Area area = getArea();
		area.load(Reference.getDataResource(Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("PlayerArea")),Field.PLAYER);
		area.load(Reference.getDataResource(Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("MobArea")),Field.MOB);
		area.load(Reference.getDataResource(Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("PvpArea")),Field.PVP);
	}

	
	public SessionList<Session> GetSessions(Position position){
		
		SessionList<Session> results = new SessionList<Session>();
		synchronized(sessions){			
			for(Session session:sessions){
				if(session.contains(position)){					
					results.add(session);
				}
			}
		}
		return results;
	}
	public SessionList<Session> GetSessions(WorldObject entity){
		
		SessionList<Session> results = new SessionList<Session>();
		synchronized(sessions){	
			
			for(Session session:sessions){
				if(session.contains(entity)){					
					results.add(session);
				}
			}
		}
		return results;
	}
	
	@Override
	public void handleEvent(Event event) {
		//LoggerFactory.getLogger(LocalMap.class).info(event);
		if(event instanceof MapEvent){
			LocalMap map = ((MapEvent)event).getMap();
			
			if(event instanceof SpawnEvent){
				SpawnEvent spawnEvent = (SpawnEvent)event;
				LivingObject entity = spawnEvent.getSpawnee();
				SessionList<Session> list = GetSessions(entity.getPosition());
				
				synchronized(entities) {
					list.enter(entity, false);
				}
				
				if(entity instanceof Player){
					Player player = (Player)entity;
					Session session = player.getSession();
					sessions.add(session);
					
					player.getClient().setState(Client.State.INGAME);
					
					if(list.contains(session)){
						//TODO: Gracefully handle respawn
						throw new RuntimeException("This should never happen! 2");
					}
					list.sendPacket(Type.IN_CHAR, player, true);
				
				} else if(entity instanceof Pet){
					Pet pet = (Pet)entity;
					if(pet.getState() == 12){
						list.sendPacket(Type.IN_PET, pet.getOwner(), true);
					}
				
				} else {
					list.sendPacket(Type.IN_NPC, entity, true);				
				}
				entity.update();
				
			} else
			if(event instanceof ItemDropEvent){
				
				ItemDropEvent itemDropEvent = (ItemDropEvent)event;
				RoamingItem roamingItem = itemDropEvent.getRoamingItem();
				//select all the players within roaming item session
				SessionList<Session> list = GetSessions(roamingItem.getPosition());
				Item<?> item = roamingItem.getItem();
				
				synchronized(entities) {
					createEntityId(roamingItem);
					item.setEntityId(roamingItem.getEntityId());
					addRoamingItem(roamingItem);
				}
				list.enter(roamingItem, false);	
				DatabaseUtils.getDinamicInstance().saveRoamingItem(roamingItem);
				list.sendPacket(Type.DROP, roamingItem); 
				
			} else
			if(event instanceof ItemPickupEvent){
				
				ItemPickupEvent itemPickupEvent = (ItemPickupEvent)event;
				RoamingItem roamingItem = itemPickupEvent.getRoamingItem();
				Player player = itemPickupEvent.getPlayer();
				// select other players within roaming item session
				SessionList<Session> otherPlayersList = player.getInterested().getSessions();
				Item<?> item = roamingItem.getItem();
				
				synchronized(entities) {
					removeRoamingItem(roamingItem);
					removeEntity(roamingItem);
				}
				player.getClient().sendPacket(Type.OUT, roamingItem); //sent to the client owner only
				player.pickItem(item, -1);	//sent to the client owner only
				player.getClient().sendPacket(Type.PICKUP, player);
				otherPlayersList.sendPacket(Type.PICKUP, player);
				otherPlayersList.exit(roamingItem, true); //sent to other clients
				DatabaseUtils.getDinamicInstance().deleteRoamingItem(item);
				
			} else	
			if(event instanceof PlayerLoginEvent){
				
				PlayerLoginEvent playerLoginEvent = (PlayerLoginEvent)event;
				
				Player player = playerLoginEvent.getPlayer();
				Session session = new Session(player);
				
				Position position = playerLoginEvent.getPosition();
				if(position==null){
					defaultSpawn.spawn(player);
				} else {
					new PlayerSpawn(position).spawn(player);					
				}
				
				if(mobsAI == null){
					startMobsAI(1000);
				} else if(mobsAI.isCancelled()){
					startMobsAI(1000);
				}
				
			} else
			if(event instanceof PlayerLogoutEvent){
				
				PlayerLogoutEvent playerLogoutEvent = (PlayerLogoutEvent)event;
				
				Player player = playerLogoutEvent.getPlayer();
				
				Pet pet = player.getPet();
				
				Session playerSession = player.getSession();
				if(playerSession!=null){
					playerSession.close();
				}
				
				synchronized(entities) {
					sessions.remove(playerSession);
					entities.remove(player.getEntityId());
					if(pet != null && pet.getState() == 12){
						entities.remove(pet.getEntityId());
					}
				}	
				
				//remove player from other players session
				SessionList<Session> playerList = player.getInterested().getSessions();
				playerList.exit(player, false);
				playerList.sendPacket(Type.OUT, player);
				
				//remove pet from other players session
				if(pet != null && pet.getState() == 12){
					SessionList<Session> petList = pet.getInterested().getSessions();
					petList.exit(player, false);
					petList.sendPacket(Type.OUT, pet);
				}
				
				 //remove player from party
				if(player.getParty() != null){
					player.getParty().exit(player);
				}
				
				 //remove player shop from local map
				if(player.getShop() != null){
					player.getShop().close();
				}
				
				player.save();
				world.getPlayerManager().removePlayer(player);
				
				if(mobsAI != null && !mobsAI.isCancelled()){
					if(this.getPlayerList().size() == 0){
						stopMobsAI();
					}
				}
				
			} else if(event instanceof SessionEvent) {
			
				Session session = ((SessionEvent)event).getSession();
				if(event instanceof NewSessionEvent){	
					NewSessionEvent newSessionEvent = (NewSessionEvent)event;
					synchronized(sessions){
						this.sessions.add(session);	
					}				
				}	
			} 
		}
	}

	@Override
	public void run() {
		while(true){
			try {
				synchronized(this){				
					this.wait();
				}
				
				//LoggerFactory.getLogger(LocalMap.class).info(this+" work");
				
				List<Entity> objects = null;
				SessionList<Session> sessionList = null;
				synchronized(this.entities){			
					objects = new Vector<Entity>(this.entities.values());
				}
				synchronized(this.sessions){			
					sessionList = (SessionList<Session>) this.sessions.clone();
				}
				
				for(Entity entity: objects){
					try{
						for(Session session: sessionList){
							Player owner = session.getOwner();
							if(entity instanceof WorldObject){
								WorldObject object = (WorldObject)entity;
							
								if(owner.equals(entity))
									continue;
								
								
								if(entity instanceof Pet){
									if(((Pet)entity).getOwner() == owner){
										continue;
									}
								}
								
								
								boolean within = owner.getPosition().within(object.getPosition(), owner.getSessionRadius());
								boolean contains = session.contains(object);
									
								if(contains && !within) {
									session.exit(object);	
									
								} else if(!contains && within) {
									session.enter(object);
								}			
							}
						}
						
						/*
						if(entity instanceof Npc){
							if(((Npc<?>)entity).getType() instanceof Mob){
								Npc<?> npc = (Npc<?>)entity;
								SessionList<Session> list = GetSessions(npc.getPosition());
								if(list.size() > 0){
									scheduleServiceOnce(npc, 1000);
								}
							}
						}
						*/
						
					}catch(Exception e ){
						LoggerFactory.getLogger(this.getClass()).warn("Exception in mapworker", e);
					}
				}
				
				
			
			} catch (Exception e) {
				LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
				throw new RuntimeException(e);
			}
			finally{			
				
			}
		}
	}
	
	public void addEntity(Entity entity) {
		synchronized(entities){
			if (!entities.containsValue(entity)) {
				entities.put(entity.getEntityId(), entity);
			}
		}
	}

	public Entity removeEntity(Entity entity) {
		synchronized(entities){
			int entitiId = entity.getEntityId();
			if(entities.containsValue(entity)){
				Entity removedEntity = entities.remove(entitiId); 
				return removedEntity;
				
			}
		}
		return null;
	}
	
	public List<Player> getPlayerList(){
		List<Entity> entitiesList = new Vector<Entity> (entities.values());
		List<Player> playerList = new Vector<Player> ();
		
		for(Entity entity : entitiesList){
			if(entity instanceof Player){
				playerList.add((Player)entity);
			}
		}
		return playerList;
	}
	
	public PlayerSpawn getDefaultSpawn(){
		return defaultSpawn;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");

		buffer.append("id:");
		buffer.append(getId());
		buffer.append(", ");
		
		buffer.append("name:");
		buffer.append(getName());		
				
		buffer.append("}");
		return buffer.toString();
	}

	public List<Party> getPartiesList() {
		return parties;
	}

	public void inviteParty(Player member, Player newMember, int expOption, int itemOption){
		
		if(member.getParty() == null){
			addParty(new Party(member, expOption, itemOption));
		}
		member.getParty().request(newMember);
	}
	
	public void addParty(Party party){
		if(!parties.contains(party)){
			parties.add(party);
		}
	}
	
	public void removeParty(Party party){
		while(parties.contains(party)){
			parties.remove(party);
		}
	}
	
	public Party getParty(int memberEntityId){
		for(Party party : parties){
			for(Player member : party.getMembers()){
				if(member.getEntityId() == memberEntityId)
					return party;
			}
		}
		
		return null;
	}
	
	public List<Shop> getShopList() {
		return shops;
	}
	
	public void addShop(Shop shop){
		if(!shops.contains(shop)){
			shops.add(shop);
		}
	}
	
	public void removeShop(Shop shop){
		while(shops.contains(shop)){
			shops.remove(shop);
		}
	}
	
	public Shop getShop(Player player){
		for(Shop shop : shops){
			if(shop.getOwner() == player)
				return shop;
		}
		
		return null;
	}
	
	public Shop getShopBuying(Player buyer){
		for(Shop shop : shops){
			for(Shop buyerShop : shop.getPlayersBuying()){
				if(buyerShop.getOwner() == buyer)
					return shop;
			}
		}
		
		return null;
	}
}
