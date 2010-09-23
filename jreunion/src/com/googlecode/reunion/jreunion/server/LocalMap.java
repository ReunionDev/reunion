package com.googlecode.reunion.jreunion.server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventBroadcaster;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.map.ItemDropEvent;
import com.googlecode.reunion.jreunion.events.map.MapEvent;
import com.googlecode.reunion.jreunion.events.map.MobSpawnEvent;
import com.googlecode.reunion.jreunion.events.session.NewSessionEvent;
import com.googlecode.reunion.jreunion.events.session.SessionEvent;
import com.googlecode.reunion.jreunion.game.Merchant;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.game.Spawn;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class LocalMap extends Map{

	
	private List<Spawn> mobSpawnList = new Vector<Spawn>();

	private List<Npc> npcSpawnList = new Vector<Npc>();

	private Area playerArea = new Area();

	private Area mobArea = new Area();
	
	private SessionList<Session>  sessions = new SessionList<Session>();	
	
	//<ItemID,ItemContainer>
	public java.util.Map<Integer,RoamingItem> roamingItems = new HashMap<Integer,RoamingItem>();
	
	private Area pvpArea = new Area();

	private Parser playerSpawnReference;


	private Parser mobSpawnReference;

	private Parser npcSpawnReference;

	World world;

	public World getWorld() {
		return world;
	}

	public LocalMap(World world, int id) {
		super(id);
		this.world = world;
		
		this.addEventListener(NewSessionEvent.class, this);
		this.addEventListener(ItemDropEvent.class, this);
	}

	public void addMobSpawn(Spawn spawn) {
		if (spawn == null) {
			return;
		}
		mobSpawnList.add(spawn);

	}

	public void addNpcSpawn(Npc npc) {
		if (npc == null) {
			return;
		}
		npcSpawnList.add(npc);

	}

	public void createMobSpawns() {

		if (mobSpawnReference == null) {
			return;
		}

		mobSpawnList.clear();
		Iterator<ParsedItem> iter = mobSpawnReference.getItemListIterator();

		while (iter.hasNext()) {

			ParsedItem item = iter.next();

			if (!item.checkMembers(new String[] { "ID", "X", "Y",
					"Radius", "RespawnTime", "Type" })) {
				System.out.println("Error loading a mob spawn on map: "
						+ getId());
				continue;
			}

			Spawn spawn = new Spawn();
			
			int posZ = item.getMemberValue("Z") == null ? 0 : Integer.parseInt(item.getMemberValue("Z"));
			double rotation = item.getMemberValue("Rotation") == null ? Server.getRand().nextDouble()*Math.PI * 2 : Double.parseDouble(item.getMemberValue("Rotation"));
			Position position = new Position(
					Integer.parseInt(item.getMemberValue("X")),
					Integer.parseInt(item.getMemberValue("Y")),
					posZ,
					this,
					rotation);
			
			spawn.setPosition(position);
			spawn.setRadius(Integer.parseInt(item.getMemberValue("Radius")));
			spawn.setMobType(Integer.parseInt(item.getMemberValue("Type")));
			spawn.setRespawnTime(Integer.parseInt(item
					.getMemberValue("RespawnTime")));
			
			addMobSpawn(spawn);
			spawn.spawnMob();
		}
	}

	public void createNpcSpawns() {

		if (npcSpawnReference == null) {
			return;
		}

		npcSpawnList.clear();

		Iterator<ParsedItem> iter = npcSpawnReference.getItemListIterator();

		while (iter.hasNext()) {

			ParsedItem i = iter.next();

			if (!i.checkMembers(new String[] { "ID", "X", "Y",
					"Rotation", "Type" })) {
				System.out.println("Error loading a npc spawn on map: "
						+ getId());
				continue;
			}
			Npc newNpc = Server.getInstance().getWorld()
					.getNpcManager()
					.createNpc(Integer.parseInt(i.getMemberValue("Type")));
			newNpc.getPosition().setX(Integer.parseInt(i.getMemberValue("X")));
			newNpc.getPosition().setY(Integer.parseInt(i.getMemberValue("Y")));
			newNpc.getPosition().setRotation(Double.parseDouble(i.getMemberValue("Rotation")));
			newNpc.getPosition().setMap(this);

			if (newNpc instanceof Merchant) {
				newNpc.setSellRate(Integer.parseInt(i
						.getMemberValue("SellRate")));
				newNpc.setBuyRate(Integer.parseInt(i.getMemberValue("BuyRate")));
				newNpc.setShop(i.getMemberValue("Shop"));
				newNpc.loadNpc();
			}
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
		
		System.out.println("Loading "+this.getName());
		Server.getInstance().getNetwork().register(getAddress());
		playerSpawnReference = new Parser();
		mobSpawnReference = new Parser();
		npcSpawnReference = new Parser();
		loadFromReference(getId());
		createMobSpawns();
		createNpcSpawns();
		System.out.println(getName()+" running on "+getAddress());
		
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
			e.printStackTrace();			
		}

		playerArea.load(Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("PlayerArea"));
		mobArea.load(Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("MobArea"));
		pvpArea.load(Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("PvpArea"));
	}

	public Iterator<Spawn> mobSpawnListIterator() {
		return mobSpawnList.iterator();
	}

	public Iterator<Npc> npcSpawnListIterator() {
		return npcSpawnList.iterator();
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
	
	@Override
	public void handleEvent(Event event) {
		System.out.println(event);
		if(event instanceof MapEvent){
			LocalMap map = ((MapEvent)event).getMap();
			
			if(event instanceof MobSpawnEvent){
				MobSpawnEvent mobSpawnEvent = (MobSpawnEvent)event;
				Mob mob = mobSpawnEvent.getMob();
				SessionList<Session> list = GetSessions(mob.getPosition());
				list.sendPacket(Type.IN_NPC, mob);
			}
			
			if(event instanceof ItemDropEvent){
				
				ItemDropEvent itemDropEvent = (ItemDropEvent)event;
				
				RoamingItem roamingItem = itemDropEvent.getRoamingItem();
				
				System.out.println(itemDropEvent.getRoamingItem().getItem().getId());
				SessionList<Session> list = GetSessions(roamingItem.getPosition());
				list.sendPacket(Type.DROP, roamingItem);
			}
			
		}else if(event instanceof SessionEvent){
			
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
