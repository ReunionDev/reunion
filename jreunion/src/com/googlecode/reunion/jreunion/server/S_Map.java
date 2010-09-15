package com.googlecode.reunion.jreunion.server;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jcommon.S_ParsedItem;
import com.googlecode.reunion.jcommon.S_Parser;
import com.googlecode.reunion.jreunion.game.G_Merchant;
import com.googlecode.reunion.jreunion.game.G_Npc;
import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.game.G_Spawn;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Map {

	private int id;
	
	private String name;

	private List<G_Spawn> mobSpawnList = new Vector<G_Spawn>();

	private List<G_Npc> npcSpawnList = new Vector<G_Npc>();

	private S_Area playerArea = new S_Area();

	private S_Area mobArea = new S_Area();
	
	private boolean local = false;
	
	

	private S_Area pvpArea = new S_Area();

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

	public S_Map(int id) {
		
		this.id = id;
		
	}

	public void addMobSpawn(G_Spawn spawn) {
		if (spawn == null) {
			return;
		}
		mobSpawnList.add(spawn);

	}

	public void addNpcSpawn(G_Npc npc) {
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

			if (!item.checkMembers(new String[] { "ID", "CenterX", "CenterY",
					"Radius", "RespawnTime", "Type" })) {
				System.out.println("Error loading a mob spawn on map: "
						+ getId());
				continue;
			}

			G_Spawn g = new G_Spawn();

			g.setCenterX(Integer.parseInt(item.getMemberValue("CenterX")));
			g.setCenterY(Integer.parseInt(item.getMemberValue("CenterY")));
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

			if (!i.checkMembers(new String[] { "ID", "CenterX", "CenterY",
					"Rotation", "Type" })) {
				System.out.println("Error loading a npc spawn on map: "
						+ getId());
				continue;
			}
			G_Npc newNpc = S_Server.getInstance().getWorldModule()
					.getNpcManager()
					.createNpc(Integer.parseInt(i.getMemberValue("Type")));

			newNpc.getPosition().setX(Integer.parseInt(i.getMemberValue("CenterX")));
			newNpc.getPosition().setY(Integer.parseInt(i.getMemberValue("CenterY")));
			newNpc.getPosition().setRotation(Double.parseDouble(i.getMemberValue("Rotation")));
			newNpc.setSpawnId(Integer.parseInt(i.getMemberValue("ID")));
			newNpc.getPosition().setMap(this);

			if (newNpc instanceof G_Merchant) {
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

	public S_Area getMobArea() {
		return mobArea;
	}

	public S_Area getPlayerArea() {
		return playerArea;
	}

	public S_Parser getPlayerSpawnReference() {
		return playerSpawnReference;
	}

	public S_Area getPvpArea() {
		return pvpArea;
	}

	public G_Spawn getSpawnByMob(int entityID) {
		Iterator<G_Spawn> mobSpawnIter = mobSpawnListIterator();

		while (mobSpawnIter.hasNext()) {
			G_Spawn spawn = mobSpawnIter.next();
			if (spawn.getMob().getEntityId() == entityID) {
				return spawn;
			}
		}
		return null;
	}

	public void load() {
		
		S_ParsedItem config = S_Reference.getInstance().getMapConfigReference().getItemById(id);
		S_ParsedItem map = S_Reference.getInstance().getMapReference().getItemById(id);
		String location = config.getMemberValue("Location");
		String ip= config.getMemberValue("Ip");
		int port = Integer.parseInt(config.getMemberValue("Port"));		
		setName(map.getName());
		address = new InetSocketAddress(ip,port);
		S_Server.getInstance().getNetworkModule().register(getAddress());
		
		if(location.equals("Local")) {
			setLocal(true);
			System.out.println("Loading "+this.getName());
			playerSpawnReference = new S_Parser();
			mobSpawnReference = new S_Parser();
			npcSpawnReference = new S_Parser();
			loadFromReference(id);
			
			createMobSpawns();
			createNpcSpawns();
			System.out.println(getName()+" running on "+getAddress());
			
		} else if(location.equals("Remote")) {
			setLocal(false);
			System.out.println("Remote server registered on "+address.getHostName()+":"+address.getPort()+" for "+this.getName());
			
		} else {
			
			throw new RuntimeException("Invalid Location("+location+") for"+this);
			
		}
	}
	
	@Override
	public String toString() {
		return "S_Map [id=" + id + ", name=" + name + "]";
	}

	public void loadFromReference(int id) {
		try{
		playerSpawnReference.Parse("data/"+S_Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("PlayerSpawn"));		
		mobSpawnReference.Parse("data/"+S_Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("MobSpawn"));
		npcSpawnReference.Parse("data/"+S_Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("NpcSpawn"));
		
		} catch(Exception e){			
			e.printStackTrace();			
		}

		playerArea.load(S_Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("PlayerArea"));
		mobArea.load(S_Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("MobArea"));
		pvpArea.load(S_Reference.getInstance().getMapReference()
				.getItemById(id).getMemberValue("PvpArea"));
	}

	public Iterator<G_Spawn> mobSpawnListIterator() {
		return mobSpawnList.iterator();
	}

	public Iterator<G_Npc> npcSpawnListIterator() {
		return npcSpawnList.iterator();
	}

	public void workSpawns() {
		Iterator<G_Spawn> mobSpawnIter = mobSpawnListIterator();

		while (mobSpawnIter.hasNext()) {
			G_Spawn spawn = mobSpawnIter.next();
			if (spawn == null) {
				continue;
			}

			if (spawn.readyToSpawn()) {
				spawn.spawnMob();

				Iterator<G_Player> playerIter = S_Server.getInstance()
						.getWorldModule().getPlayerManager()
						.getPlayerListIterator();

				while (playerIter.hasNext()) {
					G_Player player = playerIter.next();

					if (player.getPosition().getMap() != spawn.getMob().getPosition().getMap()) {
						continue;
					}

					S_Client client = S_Server.getInstance().getNetworkModule()
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
