package com.googlecode.reunion.jreunion.server;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jcommon.S_ParsedItem;
import com.googlecode.reunion.jcommon.S_Parser;
import com.googlecode.reunion.jreunion.game.Merchant;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Spawn;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Map {

	private int id;
	
	private String name;

	private List<Spawn> mobSpawnList = new Vector<Spawn>();

	private List<Npc> npcSpawnList = new Vector<Npc>();

	private Area playerArea = new Area();

	private Area mobArea = new Area();
	
	private boolean local = false;
	
	

	private Area pvpArea = new Area();

	private S_Parser playerSpawnReference;

	public boolean isLocal() {
		return local;
	}

	private void setLocal(boolean local) {
		this.local = local;
	}

	private S_Parser mobSpawnReference;

	private S_Parser npcSpawnReference;

	private InetSocketAddress address;

	public InetSocketAddress getAddress() {
		return address;
	}

	public Map(int id) {
		
		this.id = id;
		
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
		Iterator<S_ParsedItem> iter = mobSpawnReference.getItemListIterator();

		while (iter.hasNext()) {

			S_ParsedItem item = iter.next();

			if (!item.checkMembers(new String[] { "ID", "X", "Y",
					"Radius", "RespawnTime", "Type" })) {
				System.out.println("Error loading a mob spawn on map: "
						+ getId());
				continue;
			}

			Spawn g = new Spawn();

			g.setCenterX(Integer.parseInt(item.getMemberValue("X")));
			g.setCenterY(Integer.parseInt(item.getMemberValue("Y")));
			g.setRadius(Integer.parseInt(item.getMemberValue("Radius")));
			g.setMobType(Integer.parseInt(item.getMemberValue("Type")));
			g.setRespawnTime(Integer.parseInt(item
					.getMemberValue("RespawnTime")));
			g.setMap(this);

			addMobSpawn(g);
			g.spawnMob();
		}
	}

	public void createNpcSpawns() {

		if (npcSpawnReference == null) {
			return;
		}

		npcSpawnList.clear();

		Iterator<S_ParsedItem> iter = npcSpawnReference.getItemListIterator();

		while (iter.hasNext()) {

			S_ParsedItem i = iter.next();

			if (!i.checkMembers(new String[] { "ID", "X", "Y",
					"Rotation", "Type" })) {
				System.out.println("Error loading a npc spawn on map: "
						+ getId());
				continue;
			}
			Npc newNpc = Server.getInstance().getWorldModule()
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
	public int getId() {
		return id;
	}

	public Area getMobArea() {
		return mobArea;
	}

	public Area getPlayerArea() {
		return playerArea;
	}

	public S_Parser getPlayerSpawnReference() {
		return playerSpawnReference;
	}

	public Area getPvpArea() {
		return pvpArea;
	}

	public Spawn getSpawnByMob(int entityID) {
		Iterator<Spawn> mobSpawnIter = mobSpawnListIterator();

		while (mobSpawnIter.hasNext()) {
			Spawn spawn = mobSpawnIter.next();
			if (spawn.getMob().getEntityId() == entityID) {
				return spawn;
			}
		}
		return null;
	}

	public void load() {
		
		S_ParsedItem config = Reference.getInstance().getMapConfigReference().getItemById(id);
		S_ParsedItem map = Reference.getInstance().getMapReference().getItemById(id);
		String location = config.getMemberValue("Location");
		String ip= config.getMemberValue("Ip");
		int port = Integer.parseInt(config.getMemberValue("Port"));		
		setName(map.getName());
		address = new InetSocketAddress(ip, port);
		setLocal(location.equals("Local"));
		
		if(isLocal()) {
			
			System.out.println("Loading "+this.getName());
			Server.getInstance().getNetworkModule().register(getAddress());
			playerSpawnReference = new S_Parser();
			mobSpawnReference = new S_Parser();
			npcSpawnReference = new S_Parser();
			loadFromReference(id);
			createMobSpawns();
			createNpcSpawns();
			System.out.println(getName()+" running on "+getAddress());
			
		} else {
			System.out.println("Remote server registered on "+address.getHostName()+":"+address.getPort()+" for "+this.getName());
		}
	}
	
	@Override
	public String toString() {
		return "S_Map [id=" + id + ", name=" + name + "]";
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

	public void workSpawns() {
		Iterator<Spawn> mobSpawnIter = mobSpawnListIterator();

		while (mobSpawnIter.hasNext()) {
			Spawn spawn = mobSpawnIter.next();
			if (spawn == null) {
				continue;
			}

			if (spawn.readyToSpawn()) {
				spawn.spawnMob();

				Iterator<Player> playerIter = Server.getInstance()
						.getWorldModule().getPlayerManager()
						.getPlayerListIterator();

				while (playerIter.hasNext()) {
					Player player = playerIter.next();

					if (player.getPosition().getMap() != spawn.getMob().getPosition().getMap()) {
						continue;
					}

					Client client = Server.getInstance().getNetworkModule()
							.getClient(player);

					if (client == null) {
						continue;
					}

					double xcomp = Math.pow(player.getPosition().getX()
							- spawn.getMob().getPosition().getX(), 2);
					double ycomp = Math.pow(player.getPosition().getY()
							- spawn.getMob().getPosition().getY(), 2);
					double distance = Math.sqrt(xcomp + ycomp);

					if (distance < player.getSessionRadius()) {
						player.getSession().enter(spawn.getMob()); //TODO: fix spawn
					}
				}
			}
		}
	}

	/**
	 * @param name the name to set
	 */
	private void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
