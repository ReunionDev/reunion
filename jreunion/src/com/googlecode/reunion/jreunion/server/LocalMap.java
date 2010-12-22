package com.googlecode.reunion.jreunion.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.NpcSpawn;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.PlayerSpawn;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.game.Spawn;
import com.googlecode.reunion.jreunion.game.WorldObject;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class LocalMap extends Map implements Runnable{

	
	private List<Spawn> mobSpawnList = new Vector<Spawn>();

	private List<Spawn> npcSpawnList = new Vector<Spawn>();
	
	private List<Spawn> playerSpawnList = new Vector<Spawn>();

	private Area playerArea = new Area();

	private Area mobArea = new Area();
	
	private SessionList<Session>  sessions = new SessionList<Session>();
	
	private java.util.Map<Integer, WorldObject> entities = new HashMap<Integer, WorldObject>();
		
	private Area pvpArea = new Area();

	private Parser playerSpawnReference;

	Thread thread;

	private Parser mobSpawnReference;

	private Parser npcSpawnReference;
	
	private PlayerSpawn defaultSpawn;

	World world;

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
	}
	
	public WorldObject getEntity(int id) {		
		synchronized(entities){			
			return (WorldObject) entities.get(id);
		}
		
	}
	
	private void createMobSpawns() {

		mobSpawnList.clear();
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
			double rotation = item.getMemberValue("Rotation") == null ? Double.NaN : Double.parseDouble(item.getMemberValue("Rotation"));
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
			
			mobSpawnList.add(spawn);
			
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

		npcSpawnList.clear();

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
	
	public Area getMobArea() {
		return mobArea;
	}

	public Area getPlayerArea() {
		return playerArea;
	}

	public Parser getPlayerSpawnReference() {
		return playerSpawnReference;
	}

	public Area getPvpArea() {
		return pvpArea;
	}

	public void load() {
		super.load();
		thread.setName("Map: "+getName());
		
		Logger.getLogger(LocalMap.class).info("Loading "+getName());
		if(!Server.getInstance().getNetwork().register(getAddress())){
			return;
		}
		
		playerSpawnReference = new Parser();
		mobSpawnReference = new Parser();
		npcSpawnReference = new Parser();
		loadFromReference(getId());
		createMobSpawns();
		createNpcSpawns();
		createPlayerSpawns();
		
		synchronized(entities){
		
			List<RoamingItem> roamingItems = DatabaseUtils.getInstance().loadRoamingItems(this);
			for(RoamingItem roamingItem : roamingItems){				
				entities.put(roamingItem.getId(), roamingItem);
			}
		}
		
		
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

		playerArea.load(Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("PlayerArea"));
		mobArea.load(Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("MobArea"));
		pvpArea.load(Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("PvpArea"));
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
					
					WorldObject object = entities.put(entity.getId(), entity);
					list.enter(entity, false);		
					if(object!=null) 
						throw new RuntimeException("This should never happen! 1 id:"+entity.getId());
				}
				
				if(entity instanceof Player){
					Player player = (Player)entity;
					Session session = player.getSession();
					sessions.add(session);
					
					player.getClient().setState(Client.State.INGAME);
					
					player.sendStatus(Player.Status.HP);
					player.sendStatus(Player.Status.MANA);
					player.sendStatus(Player.Status.STAMINA);
					player.sendStatus(Player.Status.ELECTRICITY);
					
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
					entities.put(roamingItem.getId(), roamingItem);
					list.enter(roamingItem, false);					
				}
								
				list.sendPacket(Type.DROP, roamingItem);
			} else
			if(event instanceof ItemPickupEvent){
				
				ItemPickupEvent itemPickupEvent = (ItemPickupEvent)event;
				
				RoamingItem roamingItem = itemPickupEvent.getRoamingItem();
				
				synchronized(entities) {
					
					roamingItem = (RoamingItem) this.entities.remove(roamingItem.getId());
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
					entities.remove(player.getId());
					
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
		Timer timer = new Timer();
		while(true){
			try {
				synchronized(this){				
					this.wait();
				}
				
				//Logger.getLogger(LocalMap.class).info(this+" work");
				
				timer.Start();
				
				List<WorldObject> objects = null;
				SessionList<Session> sessionList = null;
				synchronized(this.entities){			
					objects = new Vector<WorldObject>(this.entities.values());
				}
				synchronized(this.sessions){			
					sessionList = (SessionList<Session>) this.sessions.clone();
				}
				
				for(Session session: sessionList){
					
					Player owner = session.getOwner();
					for(WorldObject entity: objects){
						try{
						if(owner.equals(entity))
							continue;
						
						boolean within = owner.getPosition().within(entity.getPosition(), owner.getSessionRadius());
						boolean contains = session.contains(entity);
						
						if(contains && !within) {
							
							session.exit(entity);	
							
						} else if(!contains && within) {
												
							session.enter(entity);
													
						}			
						}catch(Exception e ){
							Logger.getLogger(this.getClass()).warn("Exception in mapworker", e);
							
							
						}
					}
				}
				timer.Stop();
				//Logger.getLogger(LocalMap.class).info(timer.getTimeElapsedSeconds());
			
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).warn("Exception",e);
				throw new RuntimeException(e);
			}
			finally{				
				timer.Reset();
				
			}
		}
	}
}
