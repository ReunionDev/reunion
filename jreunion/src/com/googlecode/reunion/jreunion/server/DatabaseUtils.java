package com.googlecode.reunion.jreunion.server;

import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Equipment;
import com.googlecode.reunion.jreunion.game.Equipment.Slot;
import com.googlecode.reunion.jreunion.game.ExchangeItem;
import com.googlecode.reunion.jreunion.game.InventoryItem;
import com.googlecode.reunion.jreunion.game.InventoryPosition;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Player.Race;
import com.googlecode.reunion.jreunion.game.Player.Sex;
import com.googlecode.reunion.jreunion.game.ItemType;
import com.googlecode.reunion.jreunion.game.PlayerSpawn;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.Quest;
import com.googlecode.reunion.jreunion.game.QuickSlotItem;
import com.googlecode.reunion.jreunion.game.QuickSlotPosition;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.StashItem;
import com.googlecode.reunion.jreunion.game.StashPosition;
import com.googlecode.reunion.jreunion.game.quests.LimeQuest;
import com.googlecode.reunion.jreunion.game.quests.QuestState;
import com.googlecode.reunion.jreunion.game.quests.objective.Objective;
import com.googlecode.reunion.jreunion.game.quests.reward.Reward;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class DatabaseUtils extends Service {
	
	private DatabaseUtils() {
		super();
		dinamicDatabase = null;
		staticDatabase = null;
	}
	
	private Database dinamicDatabase;
	
	private Database staticDatabase;
	
	public boolean checkDinamicDatabase() {
		if (dinamicDatabase != null)
			return true;
		else
			return false;
	}
	
	public boolean checkStaticDatabase() {
		if (staticDatabase != null)
			return true;
		else
			return false;
	}
	
	
	/**
	 * @param database
	 *            The database to set.
	 * @uml.property name="database"
	 */
	public void setDinamicDatabase(Database dinamicDatabase) {
		this.dinamicDatabase = dinamicDatabase;
	}
	
	public void setStaticDatabase(Database staticDatabase) {
		this.staticDatabase = staticDatabase;
	}
	
	private static DatabaseUtils _dinamicInstance = null;
	
	private static DatabaseUtils _staticInstance = null;

	private synchronized static void createDinamicInstance() {
		if (_dinamicInstance == null) {
			_dinamicInstance = new DatabaseUtils();
		}
	}
	
	private synchronized static void createStaticInstance() {
		if (_staticInstance == null) {
			_staticInstance = new DatabaseUtils();
		}
	}
	
	public static DatabaseUtils getDinamicInstance() {
		if (_dinamicInstance == null)
			createDinamicInstance();
		return _dinamicInstance;
	}
	
	public static DatabaseUtils getStaticInstance() {
		if (_staticInstance == null)
			createStaticInstance();
		return _staticInstance;
	}
	
	public int Auth(String username, String password) {
		if (!checkDinamicDatabase())
			return -1;
		
		Statement stmt;
		try {
			
			stmt = dinamicDatabase.dinamicConn.createStatement();
			ResultSet rs = stmt
			.executeQuery("SELECT id FROM accounts WHERE username='"
					+ username + "' and password='" + password + "'");
			if (rs.next()) {
				String s = rs.getString("id");
				return Integer.parseInt(s);
			}
			return -1;
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
			return -1;
		}
	}
	
	public Position getSavedPosition(Player player){
		if (!checkDinamicDatabase())
			return null;
		Position position = null;
		try {
			Statement stmt = dinamicDatabase.dinamicConn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT `x`, `y`, `z`, `mapId` FROM `characters` WHERE `characters`.`id`="+player.getPlayerId()+" AND `mapId` IS NOT NULL AND `x` IS NOT NULL AND `y` IS NOT NULL AND `z` IS NOT NULL");
			
			if(rs.next()){
				Map map = Server.getInstance().getWorld().getMap(rs.getInt("mapId"));
				if(map != null){
					position = new Position(rs.getInt("x"),rs.getInt("y"),rs.getInt("z"), map, 0.0d);
				}
			}
			rs.close();
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error("Exception", e);
		}
		return position;
	}
	
	public void setSavedPosition(Player player){
		if (!checkDinamicDatabase())
			return;
		try {
			Statement stmt = dinamicDatabase.dinamicConn.createStatement();
			Position position = player.getPosition();
			if(position!=null&&position.getMap()!=null)
				stmt.execute("UPDATE `characters` SET `x`="+position.getX()+", `y`="+position.getY()+", `z`="+position.getZ()+", `mapId`="+position.getMap().getId()+" WHERE `characters`.`id`="+player.getPlayerId());
			
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error("Exception", e);
		}
	}
	
	public String getCharList(Client client) {
		if (!checkDinamicDatabase())
			return null;
		
		int accountId = client.getAccountId();
		String charlist ="";
		int chars = 0;
		try {
			Statement stmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM `characters`,`slots` WHERE `characters`.`accountid`="+accountId+" AND `characters`.`id`=`slots`.`charid` ORDER BY `slot` ASC");
			while (rs.next()) {
				
				int slot = rs.getInt("slot");
				boolean alreadyLogged = false;
				java.util.Map<SocketChannel,Client> clients = Server.getInstance().getWorld().getClients();			
				synchronized(clients){
					for(Client cl: clients.values()){
						if(cl.equals(client))
							continue;
						if(cl.getAccountId()==client.getAccountId()){
							Player player = cl.getPlayer();
							
							if(player!=null&&player.getSlot()==slot){								
								
								alreadyLogged = true;
							}							
						}	
					}
				}
				if(alreadyLogged)
					continue;
				
				Equipment eq = loadEquipment(new Equipment(null), rs.getInt("id"));
		
				/*
				charlist += "chars_exist " + slot + " "
				+ rs.getString("name") + " " + rs.getString("race")
				+ " " + rs.getString("sex") + " "
				+ rs.getString("hair") + " "
				+ rs.getString("level") + " "
				+ rs.getString("currHp") + " "
				+ rs.getString("currStm") + " "
				+ rs.getString("currMana") + " "
				+ rs.getString("currElect") + " "
				+ rs.getString("maxHp") + " "
				+ rs.getString("maxStm") + " "
				+ rs.getString("maxMana") + " "
				+ rs.getString("maxElect") + " "
				+ rs.getString("str") + " " 
				+ rs.getString("wis") + " " 
				+ rs.getString("dex") + " "
				+ rs.getString("con") + " " 
				+ rs.getString("lea") + " "
				+ eq.getType(Slot.HELMET) + " " 
				+ eq.getType(Slot.CHEST) + " " 
				+ eq.getType(Slot.PANTS) + " " 
				+ eq.getType(Slot.SHOULDER)	+ " "
				+ eq.getType(Slot.BOOTS) + " " 
				+ eq.getType(Slot.OFFHAND) 
				+ " 1\n";
				*/
				charlist += "chars_exist " + slot + " "
				//+ rs.getString("id") + " " // the new version client have this extra value in the packet
				+ rs.getString("name") + " "
				+ rs.getString("race") + " "
				+ rs.getString("sex") + " "
				+ rs.getString("hair") + " "
				+ rs.getString("level") + " "
				+ 1 + " " //hp
				+ 1 + " " //hp max
				+ 1 + " " //mana
				+ 1 + " " //mana max
				+ 1 + " " //stamina
				+ 1 + " " //stamina max
				+ 1 + " " //electricity
				+ 1 + " " //electricity max
				+ rs.getString("strength") + " " 
				+ rs.getString("wisdom") + " " 
				+ rs.getString("dexterity") + " "
				+ rs.getString("constitution") + " " 
				+ rs.getString("leadership") + " "
				+ "0" + " " // the new version client have this extra value in the packet
				+ eq.getTypeId(Slot.HELMET) + " " 
				+ eq.getTypeId(Slot.CHEST) + " " 
				+ eq.getTypeId(Slot.PANTS) + " " 
				+ eq.getTypeId(Slot.SHOULDER)	+ " "
				+ eq.getTypeId(Slot.BOOTS) + " " 
				+ eq.getTypeId(Slot.OFFHAND) 
				+ " 0\n";
				
				//chars_exist 3 12341234 0 0 0 2 90 12 15 15 90 90 15 15 30 5 5 30 10 309 -1 -1 -1 -1 -1 1
				// chars_exist [SlotNumber] [Name] [Race] [Sex] [HairStyle]
				// [Level] [Vitality] [Stamina] [Magic] [Energy] [Vitality]
				// [Stamina] [Magic] [Energy] [Strength] [Wisdom]
				// [Dexterity] [Constitution] [Leadership] [HeadGear]
				// [Chest] [Pants] [SoulderMount] [Feet] [Shield] 0
				chars++;
			}
			
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
			return null;
		}
		
		Logger.getLogger(DatabaseUtils.class).info("found " + chars
				+ " char(s) for Account(" + accountId + ")");	
		
		
		//charlist += "chars_end\n"; // Old client version
		charlist += "chars_end 0 "+accountId+"\n"; //New client version
		return charlist;
	}
	
	public Equipment loadEquipment(Equipment equipment, int charid) {
		
		if (!checkDinamicDatabase())
			return null;
		
		Statement stmt;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM equipment WHERE charid="+ charid + ";");
			while(rs.next()) 
			{
				int slotId = rs.getInt("slot");
				
				Item<?> item = Item.load(rs.getInt("itemid"));
				
				Slot slot = Slot.byValue(slotId);
				equipment.setItem(slot, item);
			}
			
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			
		}
		return equipment;
		
	}

	public Equipment loadEquipment(Player player) {		
		return loadEquipment(player.getEquipment(), player.getPlayerId());
	}
	
	public Player loadCharStatus(Client client, int charId){
		Player player = null;
		if (!checkDinamicDatabase())
			return null;
		Statement stmt;		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			ResultSet rs = stmt
			.executeQuery("SELECT C.*,A.level AS userlevel FROM characters AS C, accounts AS A WHERE C.accountid=A.id AND C.id="
					+ charId + ";");
			if (rs.next()) {
				int raceId = rs.getInt("race");
				Race race = Race.values()[raceId];
				player = Player.createPlayer(client, race);
				player.setPlayerId(charId);				
				player.setStrength(rs.getInt("strength"));
				player.setWisdom(rs.getInt("wisdom"));
				player.setDexterity(rs.getInt("dexterity"));
				player.setConstitution(rs.getInt("constitution"));
				player.setLeadership(rs.getInt("leadership"));
				player.setLevel(rs.getInt("level"));
				player.setTotalExp(rs.getInt("totalExp"));
				player.setLevelUpExp(rs.getInt("levelUpExp"));
				player.setLime(rs.getInt("lime"));
				player.setStatusPoints(rs.getInt("statusPoints"));
				player.setPenaltyPoints(rs.getInt("penaltyPoints"));
				player.setSex(Sex.values()[rs.getInt("sex")]);
				player.setName(rs.getString("name"));
				player.setGuildId(rs.getInt("guildid"));
				player.setGuildLevel(rs.getInt("guildlvl"));
				player.setAdminState(rs.getInt("userlevel"));
				player.setHairStyle(rs.getInt("hair"));
							
				
				return player;
			} else
				return null;
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return null;
		}
	}
	
	public void saveCharacter(Player player){
		
		Client client = player.getClient();
		
		if(client == null)
			return;
		
		if (!checkDinamicDatabase())
			return;
			
		try {
			
			int charId = player.getPlayerId();
			Statement stmt = dinamicDatabase.dinamicConn.createStatement();
			
			if(charId!=-1){				
				stmt.execute("DELETE FROM characters WHERE id="+charId+";");				
			}
						
			String q = "INSERT INTO characters ("+(charId==-1?"":"id,")+"accountid,name,level,strength,wisdom,dexterity," +
												"constitution,leadership,race,sex,hair,totalExp,levelUpExp,lime," +
												"statusPoints,penaltyPoints,guildid,guildlvl)" +
						 " VALUES ("+(charId==-1?"":charId+",")+
								    +client.getAccountId()+ ",'"
								    +player.getName()+ "',"
								    +player.getLevel()+ ","
								    +player.getStrength()+ ","
								    +player.getWisdom()+ ","
								    +player.getDexterity()+ ","
								    +player.getConstitution()+ ","
								    +player.getLeadership()+ ","
								    +player.getRace().ordinal()+ ","
								    +player.getSex().ordinal()+ ","
								    +player.getHairStyle()+ ","								   
								    +player.getTotalExp()+ ","
								    +player.getLevelUpExp()+ ","
								    +player.getLime()+ ","
								    +player.getStatusPoints()+ ","
								    +player.getPenaltyPoints()+ ","
								    +player.getGuildId()+ ","
								    +player.getGuildLvl()+ ");";
			
			stmt.execute(q,Statement.RETURN_GENERATED_KEYS);
			
			ResultSet res = stmt.getGeneratedKeys();
			if (res.next())
			    player.setPlayerId(res.getInt(1));			
			
			if(player.getPosition().getMap() == null){ //used when player creates a new char
				//TODO: better way to handle with the player default map, after char creation
				int mapId = (int)player.getClient().getWorld().getServerSetings().getDefaultMapId();
				Map map = player.getClient().getWorld().getMap(mapId);
				Position position = new Position(7025,5225,106,map,0.00d);
				player.setPosition(position);
			}
			
			setSavedPosition(player);
						
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
			return;
		}
	}
	
	public void updateCharStatus(Player player, int id, long value)
	{
		if (!checkDinamicDatabase())
			return;

		String status = "";
			
		switch(id){ 
			case 4: {status = "level"; break; }
			case 10: {status = "lime"; break; }
			case 11: {status = "totalExp"; break; }
			case 12: {status = "lvlUpExp"; break; }
			case 13: {status = "statusPoints"; break; }
			case 14: {status = "strength"; break; }
			case 15: {status = "wisdom"; break; }
			case 16: {status = "dexterity"; break; }
			case 17: {status = "constitution"; break; }
			case 18: {status = "leadership"; break; }
			case 19: {status = "penaltyPoints"; break; }
			default: return;
		}
					
		Statement stmt;
		try {
			stmt  = dinamicDatabase.dinamicConn.createStatement();
			
			stmt.execute("UPDATE characters SET "+status+" = '"+value+"' WHERE id='"+player.getPlayerId()+"';");

		} 
		catch (SQLException e) 
		{
			
			Logger.getLogger(this.getClass()).warn("Exception",e);
			
		}
	}
			
	public boolean getCharNameFree(String charName) {
		if (!checkDinamicDatabase())
			return false;
		Statement stmt;
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			ResultSet rs = stmt
			.executeQuery("SELECT id FROM characters WHERE name='"
					+ charName + "';");
			if (rs.next()) {
				
				return false;
			} else
				return true;
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return false;
		}
	}
	

	
	public void createChar(Client client, int slot, String charName,
			Race race, Sex sex, int hairStyle, int str, int wis, int dex, int con,
			int lead) {
		if (!checkDinamicDatabase())
			return;
		Statement stmt;
		try {
						
			stmt = dinamicDatabase.dinamicConn.createStatement();			
								
			Player player = Player.createPlayer(client, race);
			ItemManager itemManager = player.getClient().getWorld().getItemManager();
						
			player.setLevel(1);
			player.setName(charName);
			player.setSex(sex);
			player.setHairStyle(hairStyle);
			player.setStrength(str);
			player.setWisdom(wis);
			player.setDexterity(dex);
			player.setConstitution(con);
			player.setLeadership(lead);
			
			player.setHp(player.getMaxHp());
			player.setMana(player.getMaxMana());
			player.setElectricity(player.getMaxElectricity());			
			player.setStamina(player.getMaxStamina());
			
			player.setLime((int)client.getWorld().getServerSetings().getStartLime());
			
			client.setPlayer(player);
		
			saveCharacter(player);
			int charId = player.getPlayerId();
			
			Logger.getLogger(DatabaseUtils.class).info(charId);
			
			stmt.execute("INSERT INTO slots (charid, slot, accountid) VALUES ("
					+ charId + ","
					+ slot + ","
					+ client.getAccountId() + "); ");
			
			Item<?> hpPot1 = itemManager.create(145);
			Item<?> hpPot2 = itemManager.create(145);
			Item<?> hpPot3 = itemManager.create(145);
			Item<?> hpPot4 = itemManager.create(145);
			Item<?> hpPot5 = itemManager.create(145);
			Item<?> hpPot6 = itemManager.create(145);
			Item<?> weapon = null;
			
			switch(race){
				case BULKAN: {weapon = itemManager.create(48); break;}
				case KAILIPTON: {weapon = itemManager.create(171); break;}
				case AIDIA: {weapon = itemManager.create(431); break;}
				case HUMAN: {weapon = itemManager.create(204); break;}
				case HYBRIDER: {weapon = itemManager.create(168); break;}
				default: break;
			}
			Equipment equipment = player.getEquipment();
			Item<?> chest = itemManager.create(326);
			Item<?> pants = itemManager.create(343);
			
			equipment.setItem(Slot.CHEST, chest);
			equipment.setItem(Slot.PANTS, pants);
			saveEquipment(player);
			
			QuickSlotPosition quickSlotPosition = new QuickSlotPosition(player.getQuickSlotBar(),0);
			player.getQuickSlotBar().addItem(new QuickSlotItem(hpPot1,quickSlotPosition));
			saveQuickSlot(player);
			
			player.getInventory().storeItem(weapon, -1);
			player.getInventory().storeItem(hpPot2, -1);
			player.getInventory().storeItem(hpPot3, -1);
			player.getInventory().storeItem(hpPot4, -1);
			player.getInventory().storeItem(hpPot5, -1);
			player.getInventory().storeItem(hpPot6, -1);
			saveInventory(player);
						
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public Player loadChar(int slot, int accountId, Client client) {
		
		Player player=null;
		if (!checkDinamicDatabase())
			return null;
		int characterId = -1;
		Statement stmt;
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			ResultSet rs = stmt
			.executeQuery("(SELECT charid FROM slots WHERE accountid ="
					+ accountId
					+ " and slot = "
					+ slot + ");");
			if (rs.next()) {
				characterId = rs.getInt("charid");
			
			
				player = loadCharStatus(client, characterId);
				player.setSlot(slot);
				
				Logger.getLogger(DatabaseUtils.class).info("Loaded: " + player.getName());
			}
			
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).warn("Exception", e);
		}
		
		return player;
	}
	
	public Player loadInventory(Player player){
		if (!checkDinamicDatabase())
			return null;
		Statement invStmt;

		try {
			invStmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet invTable = invStmt.executeQuery("SELECT * FROM inventory WHERE charid="+player.getPlayerId()+";");
			
			while (invTable.next()) 
			{
				Item<?> item = Item.load(invTable.getInt("itemid"));	
				
				if (item!=null){
					InventoryItem inventoryItem = new InventoryItem(item,
							new InventoryPosition(invTable.getInt("x"), invTable.getInt("y"),invTable.getInt("tab")));
					player.getInventory().addInventoryItem(inventoryItem);
				}
			}
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return null;
		}
		return player;
	}
		
	public void saveInventory(Player player){
		if (!checkDinamicDatabase())
			return;
		
		//SaveInventory saveInventory = new SaveInventory(dinamicDatabase.dinamicConn);
		/*
		try {
			saveInventory.getDeleteStatement().setInt(1, player.getEntityId());
			
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		*/
	
		Statement stmt;
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			stmt.execute("DELETE FROM inventory WHERE charid="+player.getPlayerId()+";");
		
			String query = "INSERT INTO inventory (charid, itemid, tab, x, y) VALUES ";
			String data = "";
			
			Iterator<InventoryItem> iter = player.getInventory().getInventoryIterator();
			
			while(iter.hasNext())
			{
				InventoryItem invItem = iter.next();
				Item<?> item = invItem.getItem();
				saveItem(item);
				
				/*
				PreparedStatement statement = saveInventory.getInsertStatement();
				statement.setInt(1,player.getEntityId());
				statement.setInt(2,item.getEntityId());
				statement.setInt(3,invItem.getTab());
				statement.setInt(4,invItem.getPosX());
				statement.setInt(5,invItem.getPosY());
				statement.addBatch();				
				*/
				
				data+="("+player.getPlayerId()+ ",'"+item.getItemId()+"',"+invItem.getPosition().getTab()+
					","+invItem.getPosition().getPosX()+ ","+invItem.getPosition().getPosY()+ ")";			
				if(iter.hasNext())
					data+= ", ";			
			}
			if(!data.isEmpty()){
				stmt.execute(query+data);
				
			}
			
			//queue.add(saveInventory);
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	public List<Integer> getUsedIds()
	{
		List<Integer> idList = new Vector<Integer>();
		if (!checkDinamicDatabase())
		{
			
			return idList;
			
		}
		Statement stmt;
		try {
			stmt  = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT id FROM characters WHERE 1;");
			
			while (rs.next())
			{
				idList.add(rs.getInt("id"));	
				
			}
			
			rs = stmt.executeQuery("SELECT id FROM items WHERE 1;");
			
			while (rs.next())
			{
				
				idList.add(rs.getInt("id"));	
			}
				
		} catch (SQLException e) 
		{
			
			Logger.getLogger(this.getClass()).warn("Exception",e);
			return idList;
		}
		return idList;
	}
	public int getItemType(int uniqueid)
	{
		if (!checkDinamicDatabase())return -1;
				
		Statement stmt;
		try {
			stmt  = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT type FROM items WHERE id='"+uniqueid+"';");
			
			if (rs.next())
			{
				return rs.getInt("type");
			}
			
		} 
		catch (SQLException e) 
		{
			
			Logger.getLogger(this.getClass()).warn("Exception",e);
			
		}
		return -1;
	}
	
	public Item<?> loadItem(int itemId )
	{
		if (itemId==-1)return null;
		if (!checkDinamicDatabase())return null;
		
		Statement stmt;
		try {
			stmt  = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM items WHERE id='"+itemId+"';");
			
			if (rs.next())
			{				
				int type = rs.getInt("type");
				ItemType itemType = Server.getInstance().getWorld().getItemManager().getItemType(type);
				
				if (itemType == null) {
					Logger.getLogger(DatabaseUtils.class).error("Item type "+type+" load failed, no such item type!");
					Logger.getLogger(DatabaseUtils.class).info("Loading item type manually!");
					itemType = new ItemType(type);;
				} 
				
				Item<?> item = new Item(itemType);
				
				item.setItemId(itemId);
				item.setGemNumber(rs.getInt("gemnumber"));
				item.setExtraStats(rs.getInt("extrastats"));
				
				return item;
			}
		} 
		catch (SQLException e) 
		{
			
			Logger.getLogger(this.getClass()).warn("Exception",e);
			
		}
		return null;
	}
	
	public boolean deleteRoamingItem(Item<?> item){
		if (!checkDinamicDatabase())
			return false ;
		Statement stmt;
		try {
			stmt  = dinamicDatabase.dinamicConn.createStatement();		
			return stmt.execute("DELETE FROM `roaming` WHERE `itemid`="+item.getItemId()+";");
			
		}catch (Exception e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		return false;
	}
	
	public List<RoamingItem> loadRoamingItems(LocalMap map){
		
		List<RoamingItem> items = new Vector<RoamingItem>();
		synchronized(dinamicDatabase) {
		
			Statement stmt;
			try {
				stmt = dinamicDatabase.dinamicConn.createStatement();
				
				ResultSet rs = stmt.executeQuery("SELECT * FROM `roaming` WHERE `mapid` = "+map.getId()+";");
				
				while (rs.next()) 
				{
					int itemid = rs.getInt("itemid");
					Item<?> item = Item.load(itemid);
					
					if (item==null)
						stmt.execute("DELETE FROM `roaming` WHERE itemid="+itemid);
					else{
						RoamingItem roamingItem = new RoamingItem(item);
						Position position = new Position(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"), map, rs.getDouble("rotation"));
						roamingItem.setPosition(position);
						items.add(roamingItem);
					}
				}
				
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).warn("Exception",e);
				return null;
			}
		
		}
		return items;		
	}
	
	public void saveItem(RoamingItem roamingItem){
		if (!checkDinamicDatabase())
			return ;
		Item<?> item = roamingItem.getItem();
		Position position = roamingItem.getPosition();
		saveItem(item);
		
		int itemId = item.getItemId();
		Statement stmt;
		try {
				
			deleteRoamingItem(item);
			stmt  = dinamicDatabase.dinamicConn.createStatement();
			String q = 
			"INSERT INTO `roaming` (`itemid`,`mapid`,`x`,`y`,`z`,`rotation`) VALUES ("+
			itemId+","+
			position.getLocalMap().getId()+","+
			position.getX()+","+
			position.getY()+","+
			position.getZ()+","+
			(Double.isNaN(position.getRotation()) ? 0 : position.getRotation()) + ");";
			stmt.execute(q);
		
		} 
		catch (Exception e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
	}
	public synchronized void saveItem(Item<?> item){
		if (!checkDinamicDatabase())
			return ;
			
		Statement stmt;
		try {
			stmt  = dinamicDatabase.dinamicConn.createStatement();
			
			int itemId = item.getItemId();
			if(itemId!=-1){
				
				int res = stmt.executeUpdate("UPDATE `items` SET `type`="+item.getType().getTypeId()+", `gemnumber`="+item.getGemNumber()+", `extrastats`="+item.getExtraStats()+" WHERE `Id` = "+itemId);
				if(res==0){
					Logger.getLogger(DatabaseUtils.class).error("item not found: "+itemId);					
				}
			} else {
				stmt.execute("INSERT INTO items (type, gemnumber, extrastats)" +
						" VALUES ("+item.getType().getTypeId()+","
						+item.getGemNumber()+","+item.getExtraStats()+");",Statement.RETURN_GENERATED_KEYS);
				
				ResultSet res = stmt.getGeneratedKeys();
				if (res.next())
					item.setItemId(res.getInt(1));
			}

			
		} 
		catch (Exception e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		
	}
	
	public void deleteItem(int itemId)
	{
		if (itemId==-1)return;
		if (!checkDinamicDatabase())return ;
		
		Statement stmt;
		
		try {
			stmt  = dinamicDatabase.dinamicConn.createStatement();
			
			stmt.execute("DELETE FROM items WHERE id='"+itemId+"';");
				
		} 
		catch (SQLException e) 
		{
			
			Logger.getLogger(this.getClass()).warn("Exception",e);
			
		}
	}
	
	public  void saveSkills(Player player) {
		
		if (!checkDinamicDatabase())
			return;
		
		Statement stmt;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			int playerId = player.getPlayerId();
			stmt.execute("DELETE FROM `skills` WHERE `charid`="+playerId+";");

			String query = "INSERT INTO `skills` (`charid`, `id`, `level`) VALUES ";
			String data = "";
			
			for(Skill skill:player.getSkills().keySet())
			{
				int level = player.getSkillLevel(skill);
				if(level>0){
					if(!data.isEmpty())
						data+=", ";
					data+="("+playerId+","+skill.getId()+","+level+")";
				}
			}
			
			if(!data.isEmpty())
				stmt.execute(query+data);
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public  void loadSkills(Player player) {
		if (!checkDinamicDatabase())
			return;
		Statement stmt;
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id,level FROM skills WHERE charid="+player.getPlayerId()+";");			
			while (rs.next()) {
				int id = rs.getInt("id");
				int level = rs.getInt("level");
				
				Skill skill = Server.getInstance().getWorld().getSkillManager().getSkill(id);
				player.getSkills().put(skill, level);
				//player.getCharSkill().getSkill(id).setCurrLevel(level);
			}
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void saveEquipment(Player player){
		if (!checkDinamicDatabase())
			return;
		if (player.getEquipment()==null)return;
		
		Statement stmt;
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			stmt.execute("DELETE FROM equipment WHERE charid="+player.getPlayerId()+";");
			Equipment eq = player.getEquipment();
			
			String query = "INSERT INTO equipment (charid, slot, itemid) VALUES ";
			String data = "";
			int playerId = player.getPlayerId();
			for(Slot slot: Slot.values())
			{
				Item<?> item = eq.getItem(slot);
				if(item!=null){
					if(!data.isEmpty())
						data+= ", ";
					data+="("+playerId+","+slot.value()+","+item.getItemId()+")";		
				}
			}							
			if(!data.isEmpty())
				stmt.execute(query+data);
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void loadStash(Client client){
				
		if (!checkDinamicDatabase())
			return;
		
		try {
			Statement invStmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = invStmt.executeQuery("SELECT * FROM warehouse WHERE accountid="+client.getAccountId()+";");
			client.getPlayer().getStash().clearStash();
						
			while (rs.next()) 
			{
				Item<?> item = Item.load(rs.getInt("itemid"));
				StashItem stashItem = new StashItem(new StashPosition(rs.getInt("pos")), item);
				client.getPlayer().getStash().addItem(stashItem);
			}
						
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception ",e1);
			return;
		}
	}
	
	public void saveStash(Client client){
		
		if (!checkDinamicDatabase())
			return;
		
		try {
			Statement stmt = dinamicDatabase.dinamicConn.createStatement();
			stmt.execute("DELETE FROM warehouse WHERE accountid="+client.getAccountId()+";");
			
			Iterator<StashItem> stashIter = client.getPlayer().getStash().itemListIterator();
			
			while(stashIter.hasNext())
			{
				StashItem stashItem = (StashItem) stashIter.next();
				
				stmt.execute("INSERT INTO warehouse (accountid, pos, itemid)" +
						" VALUES ("+client.getAccountId()+ ","
						+stashItem.getStashPosition().getSlot()+ ","
						+stashItem.getItem().getItemId()+ ");");
			}
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception ",e1);
			return;
		  }
	}
	
	public void loadExchange(Player player){
		
		if (!checkDinamicDatabase())
			return;
		
		try {
			Statement invStmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet exchangeTable = invStmt.executeQuery("SELECT * FROM exchange WHERE charid="+player.getPlayerId()+";");
						
			while (exchangeTable.next()) 
			{
				Item<?> item = Item.load(exchangeTable.getInt("itemid"));
				ExchangeItem exchangeItem = new ExchangeItem(item,
						exchangeTable.getInt("x"), exchangeTable.getInt("y"));
				
				player.getExchange().addItem(exchangeItem);
			}
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void saveExchange(Player player){
		
		if (!checkDinamicDatabase())
			return;
		
		try {
			Statement stmt = dinamicDatabase.dinamicConn.createStatement();
			stmt.execute("DELETE FROM exchange WHERE charid="+player.getPlayerId()+";");
			
			Iterator<ExchangeItem> exchangeIter = player.getExchange().itemListIterator();
			
			while(exchangeIter.hasNext())
			{
				ExchangeItem exchangeItem = exchangeIter.next();
				
				stmt.execute("INSERT INTO exchange (charid, itemid, x, y)" +
						" VALUES ("+player.getPlayerId()+ ","
								   +exchangeItem.getItem().getItemId()+","
								   +exchangeItem.getPosition().getPosX()+","
								   +exchangeItem.getPosition().getPosY()+");");
			}
			
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).warn("Exception", e);
			return;
		}
	}
	
	public void deleteGuild(int id)
	{
		if (!checkDinamicDatabase())return ;
			
		Statement stmt;
		try {
			stmt  = dinamicDatabase.dinamicConn.createStatement();
			
			stmt.execute("DELETE FROM guilds WHERE id='"+id+"';");
				
		} 
		catch (SQLException e) 
		{
			
			Logger.getLogger(this.getClass()).warn("Exception",e);
			
		}
	}
	
	public void loadQuickSlot(Player player){
		
		if (!checkDinamicDatabase())
			return;
		
		try {
			Statement invStmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet quickSlotTable = invStmt.executeQuery("SELECT * FROM quickslot WHERE charid="+player.getPlayerId()+";");
						
			while (quickSlotTable.next()) 
			{
				Item<?> item = Item.load(quickSlotTable.getInt("itemid"));
				QuickSlotPosition quickSlotPosition = new QuickSlotPosition(player.getQuickSlotBar(),quickSlotTable.getInt("slot"));
				QuickSlotItem quickSlotItem = new QuickSlotItem(item,quickSlotPosition);
				player.getQuickSlotBar().addItem(quickSlotItem);
			}
			
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).warn("Exception", e);
			return;
		}
	}
	
	public void saveQuickSlot(Player player){
		
		if (!checkDinamicDatabase())
			return;
		
		try {
			Statement stmt = dinamicDatabase.dinamicConn.createStatement();
			stmt.execute("DELETE FROM quickslot WHERE charid="+player.getPlayerId()+";");
			
			Iterator<QuickSlotItem> qsIter = player.getQuickSlotBar().getQuickSlotIterator();
			
			while(qsIter.hasNext())
			{
				QuickSlotItem qsItem = qsIter.next();
				
				stmt.execute("INSERT INTO quickslot (charid, itemid, slot)" +
						" VALUES ("+player.getPlayerId()+ ","+qsItem.getItem().getItemId()+","
						+qsItem.getPosition().getSlot()+");");
			}
			
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
			return;
		  }
	}
	
	public java.util.Map<Integer,Quest> loadQuests(){
		if (!checkStaticDatabase()) return null;
		
		//a quests list that will only contain quests of the player level
		java.util.Map<Integer,Quest> questsList = new HashMap<Integer,Quest>();
		
		Statement stmt;
		try {
			stmt  = staticDatabase.staticConn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM quests;");
			
			if (rs.next()) {	
				do {
					Quest quest = loadQuest(rs.getInt("id"));
					questsList.put(quest.getId(), quest);
				} while(rs.next());
			} else {		
				Logger.getLogger(DatabaseUtils.class).error("Failed to get quests from the Static Database!");
				return null;
			}
		} 
		catch (SQLException e) 
		{
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		return questsList;
	}
	
	public Quest loadQuest(int questId )
	{
		if (questId < 0)return null;
		if (!checkStaticDatabase())return null;
		
		Statement questStmt;
		Statement questTypeStmt;
		try {
			questStmt  = staticDatabase.staticConn.createStatement();
			questTypeStmt  = staticDatabase.staticConn.createStatement();
			
			ResultSet questRs = questStmt.executeQuery("SELECT * FROM quests WHERE id='"+questId+"';");
			
			if (!questRs.next()) {				
				Logger.getLogger(DatabaseUtils.class).info("Quest loaded failed, no such quest ID!");
				return null;
			}
			
			
			ResultSet questTypeRs = questTypeStmt.executeQuery("SELECT * FROM quests_type WHERE id='"+questRs.getInt("typeid")+"';");
			
			if(!questTypeRs.next()) {				
				Logger.getLogger(DatabaseUtils.class).info("Quest Type loaded failed, no such quest type ID!");
				return null;
			}
			
			String className = "com.googlecode.reunion.jreunion.game.quests."+questTypeRs.getString("class");	
			
			Quest quest = (Quest)ClassFactory.create(className, questId);
			quest.setDescription(questRs.getString("name"));
			quest.setMinLevel(questRs.getInt("minlevel"));
			quest.setMaxLevel(questRs.getInt("maxlevel"));
			
			if(!loadQuestObjectives(quest) || !loadQuestRewards(quest)){
				return null;
			}
			
			return quest;
		} 
		catch (SQLException e) 
		{
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		return null;
	}
	
	public boolean loadQuestObjectives(Quest quest)
	{
		if (quest == null) return false;
		if (!checkStaticDatabase())return false;
		
		Statement objectiveStmt;
		Statement objectiveTypeStmt;
		try {
			objectiveStmt  = staticDatabase.staticConn.createStatement();
			objectiveTypeStmt  = staticDatabase.staticConn.createStatement();
			
			ResultSet objectiveRs = objectiveStmt.executeQuery("SELECT * FROM quests_objective WHERE questid='"+quest.getId()+"';");
			
			if(objectiveRs.next()){
				do {			
					
					ResultSet objectiveTypeRs = objectiveTypeStmt.executeQuery("SELECT * FROM quests_objective_type WHERE id='"+objectiveRs.getInt("objectivetype")+"';");
					
					if(!objectiveTypeRs.next()) {				
						Logger.getLogger(DatabaseUtils.class).info("Quest Objective Type loaded failed, no such objective type ID!");
						return false;
					}
					
					String className = "com.googlecode.reunion.jreunion.game.quests.objective."+objectiveTypeRs.getString("class");
					
					Objective objective = (Objective)ClassFactory.create(className, objectiveRs.getInt("objectiveid"), objectiveRs.getInt("ammount"));
					quest.addObjective(objective);
				} while(objectiveRs.next());
			} else {
				Logger.getLogger(DatabaseUtils.class).error("Quest Objectives loaded failed, no objectives found!");
				return false;
			}
		} 
		catch (SQLException e) 
		{
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		
		return true;
	}
	
	public boolean loadQuestRewards(Quest quest)
	{
		if (quest == null) return false;
		if (!checkStaticDatabase())return false;
		
		Statement rewardStmt;
		Statement rewardTypeStmt;
		try {
			rewardStmt  = staticDatabase.staticConn.createStatement();
			rewardTypeStmt  = staticDatabase.staticConn.createStatement();
			
			ResultSet rewardRs = rewardStmt.executeQuery("SELECT * FROM quests_reward WHERE questid='"+quest.getId()+"';");
			
			if(rewardRs.next()){
				do {	
					
					ResultSet rewardTypeRs = rewardTypeStmt.executeQuery("SELECT * FROM quests_reward_type WHERE id='"+rewardRs.getInt("rewardtype")+"';");
					
					if(!rewardTypeRs.next()) {				
						Logger.getLogger(DatabaseUtils.class).error("Quest Reward Type loaded failed, no such reward type ID!");
						return false;
					}					
					
					String className = "com.googlecode.reunion.jreunion.game.quests.reward."+rewardTypeRs.getString("class");		
					
					Reward reward = (Reward)ClassFactory.create(className, rewardRs.getInt("rewardid"), rewardRs.getInt("ammount"));
					quest.addReward(reward);
				} while(rewardRs.next());
			} else {
				Logger.getLogger(DatabaseUtils.class).error("Quest Rewards loaded failed, no rewards found!");
				return false;
			}
		} 
		catch (SQLException e) 
		{
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		
		return true;
	}
	
	public QuestState loadQuestState(Player player) {
		
		if (!checkDinamicDatabase()) return null;
		
		Statement stmt;
		QuestState questState = null;
		Client client = player.getClient();
		
		if(client == null) return null;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM queststate WHERE charid="+ player.getPlayerId() + ";");
			
			if(rs.next()) {
				Quest quest = player.getClient().getWorld().getQuestManager().getQuest(rs.getInt("questid"));
				if(quest == null) return null;
				
				questState = new QuestState(quest);
				client.sendPacket(Type.QT, "get "+quest.getId());
				
				for(Objective objective: quest.getObjectives()){
					int ammount = loadQuestObjectiveState(rs.getInt("id"), objective);
					questState.setProgression(objective, objective.getAmmount() - ammount);
					
					if(quest instanceof LimeQuest){
						client.sendPacket(Type.QT, "kill "+quest.getObjectiveSlot(objective.getId())+
												" "+ammount);
					}
				}
			}
			
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			
		}
		return questState;
	}
	
	public int loadQuestObjectiveState(int questStateId, Objective objective) {
		
		if (!checkDinamicDatabase()) return 0;
		
		Statement stmt;
		int ammount = 0;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM questobjectivestate WHERE queststateid='"+ questStateId + "' and objectiveid='"+ objective.getId() +"';");
			
			if(rs.next()) {
				ammount = rs.getInt("ammount");
			}
			
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			
		}
		return ammount;
	}
	
	public void saveQuest(Player player){
		
		int questStateId = deleteQuestState(player);
		deleteQuestObjectiveState(questStateId);
		
		questStateId = saveQuestState(player);	
		
		if(questStateId > 0)
			saveQuestObjectiveState(questStateId, player);
	}
	
	public int saveQuestState(Player player) {
		
		if (!checkDinamicDatabase()) return 0;
		
		Statement stmt;
		int questStateId = 0;
		QuestState questState = player.getQuestState();
		
		if(questState == null)	return 0;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			
			String query = "INSERT INTO queststate (charid, questid) VALUES ";
			String data = "('"+player.getPlayerId()+"','"+questState.getQuest().getId()+"')";

			stmt.execute(query+data,Statement.RETURN_GENERATED_KEYS);
			ResultSet res = stmt.getGeneratedKeys();
			
			if (res.next())
				questStateId = res.getInt(1);
				
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			
		}
		return questStateId;
	}
	
	public boolean saveQuestObjectiveState(int questStateId, Player player) {
		
		if (!checkDinamicDatabase()) return false;
		
		Statement stmt;
		QuestState questState = player.getQuestState();
		
		if(questState == null) return false;
		
		Quest quest = questState.getQuest();
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			
			String query = "INSERT INTO questobjectivestate (queststateid, objectiveid, ammount) VALUES ";
			String data = "";
			
			Iterator<Objective> iter = quest.getObjectives().listIterator();
			
			while(iter.hasNext()){
				Objective objective = (Objective)iter.next();
				int ammount = objective.getAmmount() - questState.getProgression(objective.getId());
				
				data += "('"+questStateId+"','"+objective.getId()+"','"+ammount+"')";
					
				if(iter.hasNext())
					data += ", ";
				
			}
			
			if(!data.isEmpty()){
				stmt.execute(query+data);
				
			}
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			
		}
		return true;
		
	}
	
	public int deleteQuestState(Player player) {
		
		if (!checkDinamicDatabase()) return 0;
		
		Statement stmt;
		int questStateId = 0;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			ResultSet res = stmt.executeQuery("SELECT * FROM queststate WHERE charid='"+player.getPlayerId()+"';");
			
			if(res.next())
				questStateId = res.getInt("id");
			
			res.close();
			stmt.execute("DELETE FROM queststate WHERE id="+questStateId+";");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			
		}
		return questStateId;
	}
	
	public boolean deleteQuestObjectiveState(int questStateId) {
		
		if (!checkDinamicDatabase()) return false;
		
		Statement stmt;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			stmt.execute("DELETE FROM questobjectivestate WHERE queststateid='"+questStateId+"';");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			
		}
		return true;
	}
	
	public void deleteCharEquipment(int charId) {
		if (!checkDinamicDatabase())
			return;
		Statement stmt;
		Statement itemStmt;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			itemStmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = itemStmt.executeQuery("SELECT * FROM equipment WHERE charid = "+charId+ ";");
			if(rs.next()){
				do {
					deleteItem(rs.getInt("itemid"));
				} while(rs.next());
			} else return;
			
			stmt
			.execute("DELETE FROM equipment WHERE charid = "+charId+ ";");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharExchange(int charId) {
		if (!checkDinamicDatabase())
			return;
		Statement stmt;
		Statement itemStmt;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			itemStmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = itemStmt.executeQuery("SELECT * FROM exchange WHERE charid = "+charId+ ";");
			if(rs.next()){
				do {
					deleteItem(rs.getInt("itemid"));
				} while(rs.next());
			} else return;
			
			stmt
			.execute("DELETE FROM exchange WHERE charid = "+charId+ ";");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharInventory(int charId) {
		if (!checkDinamicDatabase())
			return;
		Statement stmt;
		Statement itemStmt;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			itemStmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = itemStmt.executeQuery("SELECT * FROM inventory WHERE charid = "+charId+ ";");
			if(rs.next()){
				do {
					deleteItem(rs.getInt("itemid"));
				} while(rs.next());
			} else return;
			
			stmt
			.execute("DELETE FROM inventory WHERE charid = "+charId+ ";");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharQuestState(int charId) {
		if (!checkDinamicDatabase())
			return;
		Statement stmt;
		Statement questStmt;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			questStmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = questStmt.executeQuery("SELECT * FROM queststate WHERE charid = "+charId+ ";");
			if(rs.next()){
				do {
					deleteQuestObjectiveState(rs.getInt("id"));
				} while(rs.next());
			} else return;
			
			stmt
			.execute("DELETE FROM queststate WHERE charid = "+charId+ ";");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharQuickSlot(int charId) {
		if (!checkDinamicDatabase())
			return;
		Statement stmt;
		Statement itemStmt;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			itemStmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = itemStmt.executeQuery("SELECT * FROM quickslot WHERE charid = "+charId+ ";");
			if(rs.next()){
				do {
					deleteItem(rs.getInt("itemid"));
				} while(rs.next());
			} else return;
			
			stmt
			.execute("DELETE FROM quickslot WHERE charid = "+charId+ ";");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharSkills(int charId) {
		if (!checkDinamicDatabase())
			return;
		Statement stmt;
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			stmt
			.execute("DELETE FROM skills WHERE charid = "+charId+ ";");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharacter(int charId) {
		if (!checkDinamicDatabase())
			return;
		Statement stmt;
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			stmt
			.execute("DELETE FROM characters WHERE id = "+charId+ ";");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharSlot(int charId) {
		if (!checkDinamicDatabase())
			return;
		Statement stmt;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			
			stmt.execute("DELETE FROM slots WHERE charid = "+ charId + ";");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public int getCharId(int slotNumber, int accountId) {
		if (!checkDinamicDatabase())
			return -1;
		
		Statement stmt;
		int charId = -1;
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT charid FROM slots WHERE slot = "
					+ slotNumber
					+ " and accountid = "
					+ accountId
					+ ";");
			
			if(rs.next())
				charId = rs.getInt("charid");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return -1;
		}
		
		return charId;
	}
	
	public String getCharName(int charId) {
		if (!checkDinamicDatabase())
			return "";
		
		Statement stmt;
		String charName = "";
		
		try {
			stmt = dinamicDatabase.dinamicConn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM characters WHERE id = "+ charId	+ ";");
			
			if(rs.next())
				charName = rs.getString("name");
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return "";
		}
		
		return charName;
	}
}
