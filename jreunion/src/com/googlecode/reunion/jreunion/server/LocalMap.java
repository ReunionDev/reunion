package com.googlecode.reunion.jreunion.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.map.ItemDropEvent;
import com.googlecode.reunion.jreunion.events.map.ItemPickupEvent;
import com.googlecode.reunion.jreunion.events.map.MapEvent;
import com.googlecode.reunion.jreunion.events.map.SpawnEvent;
import com.googlecode.reunion.jreunion.events.map.PlayerLoginEvent;
import com.googlecode.reunion.jreunion.events.map.PlayerLogoutEvent;
import com.googlecode.reunion.jreunion.events.session.NewSessionEvent;
import com.googlecode.reunion.jreunion.events.session.SessionEvent;
import com.googlecode.reunion.jreunion.game.Entity;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.NpcSpawn;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.PlayerSpawn;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.game.Spawn;
import com.googlecode.reunion.jreunion.game.WorldObject;
import com.googlecode.reunion.jreunion.server.Area.Field;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

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

	Thread thread;

	private Parser mobSpawnReference;

	private Parser npcSpawnReference;
	
	private PlayerSpawn defaultSpawn;

	World world;

	public World getWorld() {
		return world;
	}
	
	public Npc createNpc(int typeId) {
		
		ParsedItem parsedNpc = Reference.getInstance().getMobReference().getItemById(typeId);
		if (parsedNpc == null) {
			parsedNpc = Reference.getInstance().getNpcReference().getItemById(typeId);
			if (parsedNpc == null) {
				return null;
			}
		}		
		String className = "com.googlecode.reunion.jreunion.game." + parsedNpc.getMemberValue("Class");		
		
		Npc npc = (Npc)ClassFactory.create(className, typeId);
		return npc;
		
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
	}
	
	public Entity getEntity(int id) {		
		synchronized(entities){			
			return (Entity) entities.get(id);
		}
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
				entities.put(id, obj);
				obj.setEntityId(id);
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
				Logger.getLogger(LocalMap.class).info("Error loading a mob spawn on map: "
						+ getId());
				continue;
			}

			NpcSpawn spawn = new NpcSpawn();
			spawn.setId(Integer.parseInt(item.getMemberValue("Id")));
			spawn.setType(NpcSpawn.Type.MOB);
			
			int posZ = item.getMemberValue("Z") == null ? 0 : Integer.parseInt(item.getMemberValue("Z"));
			String rotValue = item.getMemberValue("Rotation");
			double rotation = rotValue == null ? Double.NaN : Double.parseDouble(rotValue);
			if (rotation==Double.NaN){
				Logger.getLogger(LocalMap.class).info("Invalid rotation: "+rotValue+" for npc "+spawn.getId());
				
			}
			Position position = new Position(
					Integer.parseInt(item.getMemberValue("X")),
					Integer.parseInt(item.getMemberValue("Y")),
					posZ,
					this,
					rotation);
			
			spawn.setPosition(position);
			spawn.setRadius(Integer.parseInt(item.getMemberValue("Radius")));
			spawn.setNpcType(Integer.parseInt(item.getMemberValue("Type")));
			spawn.setRespawnTime(Integer.parseInt(item
					.getMemberValue("RespawnTime")));
			
			npcSpawnList.add(spawn);
			
			spawn.spawn();
		}
	}

	public Parser getMobSpawnReference() {
		return mobSpawnReference;
	}

	public Parser getNpcSpawnReference() {
		return npcSpawnReference;
	}
	
	private void createNpcSpawns() {
		
		Iterator<ParsedItem> iter = npcSpawnReference.getItemListIterator();

		while (iter.hasNext()) {

			ParsedItem item = iter.next();

			if (!item.checkMembers(new String[] { "ID", "X", "Y",
					"Rotation", "Type" })) {
				Logger.getLogger(LocalMap.class).info("Error loading a npc spawn on map: "
						+ getId());
				continue;
			}
			
			NpcSpawn spawn = new NpcSpawn();
			spawn.setId(Integer.parseInt(item.getMemberValue("Id")));
			spawn.setType(NpcSpawn.Type.NPC);
			
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
			npcSpawnList.add(spawn);
			
			spawn.spawn();
		}
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
		
		Logger.getLogger(LocalMap.class).info("Loading "+getName());
		if(!Server.getInstance().getNetwork().register(getAddress())){
			System.out.println("huh?");
			return;
		}
		
		playerSpawnReference = new Parser();
		mobSpawnReference = new Parser();
		npcSpawnReference = new Parser();
		loadFromReference(getId());
		npcSpawnList.clear();
		createMobSpawns();
		createNpcSpawns();
		createPlayerSpawns();
		
		synchronized(entities){
		
			List<RoamingItem> roamingItems = DatabaseUtils.getInstance().loadRoamingItems(this);
			for(RoamingItem roamingItem : roamingItems){				
				entities.put(roamingItem.getEntityId(), roamingItem);
			}
		}
		
		executorService.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {

				List<Entity> objects = null;
				synchronized(entities){			
					objects = new Vector<Entity>(entities.values());
				}
				for(Entity entity: objects){
					if(entity instanceof Mob){
						Mob mob = (Mob)entity;	
						//mob.workMob();
					}
				}
			}
			
		}, 0, 2, TimeUnit.SECONDS);
		
		Logger.getLogger(LocalMap.class).info(getName()+" running on "+getAddress());
		
	}

	private void createPlayerSpawns() {
		int defaultSpawnId = Integer.parseInt(Reference.getInstance().getMapReference().getItemById(this.getId()).getMemberValue("DefaultSpawnId"));
		playerSpawnList.clear();

		Iterator<ParsedItem> iter = playerSpawnReference.getItemListIterator();

		while (iter.hasNext()) {

			ParsedItem item = iter.next();

			if (!item.checkMembers(new String[] { "ID", "X", "Y"
					})) {
				Logger.getLogger(LocalMap.class).info("Error loading a player spawn on map: "
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
			
			playerSpawnList.add(spawn);
			
		}
	}

	public void loadFromReference(int id) {
		try{
			playerSpawnReference.Parse("data/"+Reference.getInstance().getMapReference()
					.getItemById(id).getMemberValue("PlayerSpawn"));		
			mobSpawnReference.Parse("data/"+Reference.getInstance().getMapReference()
					.getItemById(id).getMemberValue("MobSpawn"));
			npcSpawnReference.Parse("data/"+Reference.getInstance().getMapReference()
					.getItemById(id).getMemberValue("NpcSpawn"));
			
		} catch(Exception e){			
			Logger.getLogger(this.getClass()).warn("Exception",e);			
		}
		Area area = getArea();
		area.load("data/"+Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("PlayerArea"),Field.PLAYER);
		area.load("data/"+Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("MobArea"),Field.MOB);
		area.load("data/"+Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("PvpArea"),Field.PVP);
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
		//Logger.getLogger(LocalMap.class).info(event);
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
						
						throw new RuntimeException("This should never happen! 2");
					}

					list.sendPacket(Type.IN_CHAR, player, true);
				}else{
					
					list.sendPacket(Type.IN_NPC, entity);				
				}
				entity.update();
				
			} else
			if(event instanceof ItemDropEvent){
				
				ItemDropEvent itemDropEvent = (ItemDropEvent)event;
				
				RoamingItem roamingItem = itemDropEvent.getRoamingItem();
				
				SessionList<Session> list = GetSessions(roamingItem.getPosition());
				
				synchronized(entities) {
					entities.put(roamingItem.getEntityId(), roamingItem);
					list.enter(roamingItem, false);					
				}
								
				list.sendPacket(Type.DROP, roamingItem);
			} else
			if(event instanceof ItemPickupEvent){
				
				ItemPickupEvent itemPickupEvent = (ItemPickupEvent)event;
				
				RoamingItem roamingItem = itemPickupEvent.getRoamingItem();
				
				synchronized(entities) {
					
					roamingItem = (RoamingItem) this.entities.remove(roamingItem.getEntityId());
					if(roamingItem!=null) {
						
						Player player = itemPickupEvent.getPlayer();
						Item item = roamingItem.getItem();
						DatabaseUtils.getInstance().deleteRoamingItem(item);
						player.pickItem(roamingItem.getItem());
					}				
				}				
				roamingItem.getInterested().sendPacket(Type.OUT, roamingItem);
				
			}
			
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
			}
			if(event instanceof PlayerLogoutEvent){
				
				PlayerLogoutEvent playerLogoutEvent = (PlayerLogoutEvent)event;
				
				Player player = playerLogoutEvent.getPlayer();
				
				Session session = player.getSession();
				if(session!=null){
					session.close();
				}
				
				synchronized(entities) {
					sessions.remove(session);
					entities.remove(player.getEntityId());
					
				}	
				SessionList<Session> list = player.getInterested().getSessions();
				list.exit(player, false);
				list.sendPacket(Type.OUT, player);
				
				player.save();
				
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

	@Override
	public void run() {
		while(true){
			try {
				synchronized(this){				
					this.wait();
				}
				
				//Logger.getLogger(LocalMap.class).info(this+" work");
				
				
				List<Entity> objects = null;
				SessionList<Session> sessionList = null;
				synchronized(this.entities){			
					objects = new Vector<Entity>(this.entities.values());
				}
				synchronized(this.sessions){			
					sessionList = (SessionList<Session>) this.sessions.clone();
				}
				
				for(Session session: sessionList){
					
					Player owner = session.getOwner();
					for(Entity entity: objects){
						if(entity instanceof WorldObject){
							WorldObject object = (WorldObject)entity;
							try{
								if(owner.equals(entity))
									continue;
								
								boolean within = owner.getPosition().within(object.getPosition(), owner.getSessionRadius());
								boolean contains = session.contains(object);
								
								if(contains && !within) {
									
									session.exit(object);	
									
								} else if(!contains && within) {
														
									session.enter(object);
															
								}			
							}catch(Exception e ){
								Logger.getLogger(this.getClass()).warn("Exception in mapworker", e);
								
								
							}
						}
					}
				}
				//Logger.getLogger(LocalMap.class).info(timer.getTimeElapsedSeconds());
			
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).warn("Exception",e);
				throw new RuntimeException(e);
			}
			finally{			
				
			}
		}
	}

	public void removeEntity(Entity entity) {
		synchronized(entities){
			int entitiId = entity.getEntityId();
			if(entities.containsKey(entitiId))
				entities.remove(entitiId);
		}
	}
}
