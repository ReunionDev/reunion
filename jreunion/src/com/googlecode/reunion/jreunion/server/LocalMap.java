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
import com.googlecode.reunion.jreunion.events.map.MapEvent;
import com.googlecode.reunion.jreunion.game.Merchant;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.game.Spawn;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class LocalMap extends Map{

	
	private List<Spawn> mobSpawnList = new Vector<Spawn>();

	private List<Npc> npcSpawnList = new Vector<Npc>();

	private Area playerArea = new Area();

	private Area mobArea = new Area();
	
	private java.util.List<Session> sessions = new Vector<Session>();	
	
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

			spawn.setCenterX(Integer.parseInt(item.getMemberValue("X")));
			spawn.setCenterY(Integer.parseInt(item.getMemberValue("Y")));
			spawn.setRadius(Integer.parseInt(item.getMemberValue("Radius")));
			spawn.setMobType(Integer.parseInt(item.getMemberValue("Type")));
			spawn.setRespawnTime(Integer.parseInt(item
					.getMemberValue("RespawnTime")));
			spawn.setMap(this);
			
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
			newNpc.setSpawnId(Integer.parseInt(i.getMemberValue("ID")));
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
	
	@Override
	public void handleEvent(Event event) {
		
		
		if(event instanceof MapEvent){
			LocalMap map = ((MapEvent)event).getMap();
			
			
			
		}
		
	}
}
