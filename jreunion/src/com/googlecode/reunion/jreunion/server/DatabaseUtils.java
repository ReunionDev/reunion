package com.googlecode.reunion.jreunion.server;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Entity;
import com.googlecode.reunion.jreunion.game.Equipment;
import com.googlecode.reunion.jreunion.game.Equipment.Slot;
import com.googlecode.reunion.jreunion.game.ExchangeItem;
import com.googlecode.reunion.jreunion.game.InventoryItem;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Player.Race;
import com.googlecode.reunion.jreunion.game.Player.Sex;
import com.googlecode.reunion.jreunion.game.items.equipment.Armor;
import com.googlecode.reunion.jreunion.game.items.equipment.Axe;
import com.googlecode.reunion.jreunion.game.items.equipment.GunWeapon;
import com.googlecode.reunion.jreunion.game.items.equipment.RingWeapon;
import com.googlecode.reunion.jreunion.game.items.equipment.StaffWeapon;
import com.googlecode.reunion.jreunion.game.items.equipment.Sword;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.game.items.potion.Potion;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.QuickSlotItem;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.StashItem;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.database.DatabaseAction;
import com.googlecode.reunion.jreunion.server.database.SaveInventory;
import com.mysql.jdbc.MySQLConnection;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class DatabaseUtils extends Service {
	
	private DatabaseUtils() {
		super();
		database = null;
		
	}
	
	private Queue<DatabaseAction> queue = new LinkedList<DatabaseAction>();
	
	private Database database;
	
	public boolean checkDatabase() {
		if (database != null)
			return true;
		else
			return false;
	}
	
	/**
	 * @param database
	 *            The database to set.
	 * @uml.property name="database"
	 */
	public void setDatabase(Database db) {
		database = db;
	}
	
	private static DatabaseUtils _instance = null;

	private synchronized static void createInstance() {
		if (_instance == null) {
			_instance = new DatabaseUtils();
		}
	}
	
	public static DatabaseUtils getInstance() {
		if (_instance == null)
			createInstance();
		return _instance;
	}
	
	public int Auth(String username, String password) {
		if (!checkDatabase())
			return -1;
		
		Statement stmt;
		try {
			
			stmt = database.conn.createStatement();
			ResultSet rs = stmt
			.executeQuery("SELECT id FROM accounts WHERE username='"
					+ username + "' and password='" + password + "'");
			if (rs.next()) {
				String s = rs.getString("id");
				return Integer.parseInt(s);
			}
			return -1;
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return -1;
		}
		
	}
	
	public String getCharList(Client client) {
		if (!checkDatabase())
			return null;
		
		int accountId = client.getAccountId();
		String charlist ="";
		int chars = 0;
		try {
			Statement stmt = database.conn.createStatement();
			
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
							Player p1 = cl.getPlayer();
							
							if(p1!=null&&p1.getSlot()==slot){								
								
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
				+ rs.getString("name") + " " + rs.getString("race")
				+ " " + rs.getString("sex") + " "
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
				+ eq.getType(Slot.HELMET) + " " 
				+ eq.getType(Slot.CHEST) + " " 
				+ eq.getType(Slot.PANTS) + " " 
				+ eq.getType(Slot.SHOULDER)	+ " "
				+ eq.getType(Slot.BOOTS) + " " 
				+ eq.getType(Slot.OFFHAND) 
				+ " 1\n";
				
				//chars_exist 3 12341234 0 0 0 2 90 12 15 15 90 90 15 15 30 5 5 30 10 309 -1 -1 -1 -1 -1 1
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
		
		charlist += "chars_end\n";
		return charlist;
	}
	
	public Equipment loadEquipment(Equipment equipment, int charid) {
		
		if (!checkDatabase())
			return null;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM equipment WHERE charid="+ charid + ";");
			while(rs.next()) 
			{
				int slotId = rs.getInt("slot");
				
				Slot slot = Slot.byValue(slotId);
				equipment.setItem(slot,ItemFactory.loadItem(rs.getInt("itemid")));
			}
			
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			
		}
		return equipment;
		
	}

	public void loadEquipment(Player player) {		
		loadEquipment(player.getEquipment(), player.getId());
	}
	
	public String createUnique(){
		return null;
		
		
		
		/*
		return "
		SELECT Rank FROM(SELECT Id,@rownum:=@rownum+1 `rank` FROM (SELECT Id FROM characters
				UNION
				SELECT ID FROM items) t,(SELECT @rownum:=0) r) t2
				WHERE Rank <> Id
				LIMIT 1
				UNION
				SELECT MAX(`Id`)+1
				FROM (
				SELECT Id FROM characters
				UNION
				SELECT Id FROM items) t3
				LIMIT 1;
		";" +
		*/
		
	}
	
	
	public Player loadCharStatus(Client client, int characterId){
		Player player = null;
		if (!checkDatabase())
			return null;
		Statement stmt;		
		try {
			stmt = database.conn.createStatement();
			ResultSet rs = stmt
			.executeQuery("SELECT C.*,A.level AS userlevel FROM characters AS C, accounts AS A WHERE C.accountid=A.id AND C.id="
					+ characterId + ";");
			if (rs.next()) {
				int raceId = rs.getInt("race");
				Race race = Race.values()[raceId];
				player = Player.createPlayer(client, race);
				player.setId(characterId);				
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
				
				player.setHp(player.getMaxHp());				
				player.setStamina(player.getMaxStamina());				
				player.setMana(player.getMaxMana());				
				player.setElectricity(player.getMaxElectricity());
				
				
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
		
		if (!checkDatabase())
			return;
			
		try {
			
			int charid = player.getId();
			Statement stmt = database.conn.createStatement();
			
			if(charid!=-1){
				
				stmt.execute("DELETE FROM characters WHERE id="+charid+";");
				
			}
			
			String key = "(SELECT Rank FROM(SELECT Id,@rownum:=@rownum+1 `rank` FROM (SELECT Id FROM characters UNION SELECT `Id` FROM items Order By `Id`) t,(SELECT @rownum:=0) r) t2 WHERE Rank <> Id LIMIT 1 UNION SELECT IF(MAX(`Id`) IS NULL, 1 ,MAX(`Id`)+1) FROM (SELECT Id FROM characters UNION SELECT Id FROM items) t3 LIMIT 1)";
			
						
			String q = "INSERT INTO characters (id,accountid,name,level,strength,wisdom,dexterity,constitution,leadership,race,sex,hair," +
												  "totalExp,levelUpExp,lime,statusPoints,penaltyPoints," +
												  "guildid,guildlvl)" +
						 " VALUES ("+(charid==-1?key:player.getId())+ ","
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
			System.out.println(q); 
			stmt.execute(q);
			if(charid==-1){
				
				
				ResultSet rs = stmt.executeQuery("SELECT Id from characters where name ='"+player.getName()+"'");
				rs.next();
				charid = rs.getInt(1);
				if(charid==-1)
					throw new Exception("key is -1!");
				player.setId(charid);
			}
			
						
		} catch (Exception e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		  }
	}
	
	public void updateCharStatus(Player player, int id, int value)
	{
			if (!checkDatabase())
			return ;

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
			stmt  = database.conn.createStatement();
			
			stmt.execute("UPDATE characters SET "+status+" = '"+value+"' WHERE id='"+player.getId()+"';");

		} 
		catch (SQLException e) 
		{
			
			Logger.getLogger(this.getClass()).warn("Exception",e);
			
		}
	}
			
	public boolean getCharNameFree(String charName) {
		if (!checkDatabase())
			return false;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
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
		if (!checkDatabase())
			return;
		Statement stmt;
		try {
						
			stmt = database.conn.createStatement();			
								
			Player player = Player.createPlayer(client, race);
			
			player.setId(-1);
			
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
			
			player.setLime(Integer.parseInt(Reference.getInstance().getServerReference().getItem("Server").getMemberValue("StartLime")));
			
			client.setPlayer(player);
		
			saveCharacter(player);
			int characterId = player.getId();
			
			Logger.getLogger(DatabaseUtils.class).info(characterId);
			
			stmt.execute("INSERT INTO slots (charid, slot, accountid) VALUES ("
					+ characterId + ","
					+ slot + ","
					+ client.getAccountId() + "); ");
			
			Potion hpPot1 = (Potion)ItemFactory.create(145);
			Potion hpPot2 = (Potion)ItemFactory.create(145);
			Potion hpPot3 = (Potion)ItemFactory.create(145);
			Potion hpPot4 = (Potion)ItemFactory.create(145);
			Potion hpPot5 = (Potion)ItemFactory.create(145);
			Potion hpPot6 = (Potion)ItemFactory.create(145);
			Weapon weapon = null;
			
			switch(race){
				case BULKAN: {weapon = (Axe)ItemFactory.create(48); break;}
				case KAILIPTON: {weapon = (StaffWeapon)ItemFactory.create(171); break;}
				case AIDIA: {weapon = (RingWeapon)ItemFactory.create(431); break;}
				case HUMAN: {weapon = (GunWeapon)ItemFactory.create(204); break;}
				case HYBRIDER: {weapon = (Sword)ItemFactory.create(168); break;}
				default: break;
			}
			Equipment equipment = player.getEquipment();
			Armor chest = (Armor)ItemFactory.create(326);
			Armor pants = (Armor)ItemFactory.create(343);
			
			equipment.setItem(Slot.CHEST, chest);
			equipment.setItem(Slot.PANTS, pants);
			saveEquipment(player);
			
			player.getQuickSlot().addItem(new QuickSlotItem(hpPot1,0));
			saveQuickSlot(player);
			
			player.getInventory().addItem(weapon);
			player.getInventory().addItem(hpPot2);
			player.getInventory().addItem(hpPot3);
			player.getInventory().addItem(hpPot4);
			player.getInventory().addItem(hpPot5);
			player.getInventory().addItem(hpPot6);
			saveInventory(player);
						
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void delChar(int slotNumber, int accountId) {
		if (!checkDatabase())
			return;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			stmt
			.execute("DELETE FROM slots WHERE slot = "
					+ slotNumber
					+ " and accountid = "
					+ accountId
					+ ";");
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public Player loadChar(int slot, int accountId, Client client) {
		
		Player player=null;
		if (!checkDatabase())
			return null;
		int characterId = -1;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
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
			
		} catch (SQLException e1) {
			
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			
		}
		
		return player;
	}
	
	public Player loadInventory(Player player){
		if (!checkDatabase())
			return null;
		Statement invStmt;
		try {
			invStmt = database.conn.createStatement();
			
			ResultSet invTable = invStmt.executeQuery("SELECT * FROM inventory WHERE charid="+player.getId()+";");
			
			while (invTable.next()) 
			{
				Item item = ItemFactory.loadItem(invTable.getInt("itemid"));
				if (item!=null)
				player.getInventory().addItem(invTable.getInt("x"), invTable.getInt("y"), item,invTable.getInt("tab"));
			}
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return null;
		}
		return player;
	}
		
	public void saveInventory(Player player){
		if (!checkDatabase())
			return;
		
		//SaveInventory saveInventory = new SaveInventory(database.conn);
		/*
		try {
			saveInventory.getDeleteStatement().setInt(1, player.getEntityId());
			
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		*/
	
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM inventory WHERE charid="+player.getId()+";");
		
			String query = "INSERT INTO inventory (charid, itemid, tab, x, y) VALUES ";
			String data = "";
			
			Iterator<InventoryItem> iter = player.getInventory().getInventoryIterator();
			
			while(iter.hasNext())
			{
				InventoryItem invItem = iter.next();
				Item item = invItem.getItem();
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
				
				data+="("+player.getId()+ ",'"+item.getId()+"',"+invItem.getTab()+
					","+invItem.getX()+ ","+invItem.getY()+ ")";			
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
		if (!checkDatabase())
		{
			
			return idList;
			
		}
		Statement stmt;
		try {
			stmt  = database.conn.createStatement();
			
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
		if (!checkDatabase())return -1;
				
		Statement stmt;
		try {
			stmt  = database.conn.createStatement();
			
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
	
	public Item loadItem(int itemId )
	{
		if (itemId==-1)return null;
		if (!checkDatabase())return null;
		
		
		
		Statement stmt;
		try {
			stmt  = database.conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM items WHERE id='"+itemId+"';");
			
			if (rs.next())
			{
				
				int type = rs.getInt("type");
				ParsedItem parseditem = Reference.getInstance().getItemReference()
				.getItemById(type);
				
				if (parseditem == null) {
					Logger.getLogger(DatabaseUtils.class).info("Item loaded failed, no such item type!");
					return null;
				}
				
				String className = "com.googlecode.reunion.jreunion.game.items." + parseditem.getMemberValue("Class");		
				
				Item item = (Item)ClassFactory.create(className, type);
				if(item==null)
					return null;
				item.setId(itemId);
				
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
	
	public boolean deleteRoamingItem(Item item){
		if (!checkDatabase())
			return false ;
		Statement stmt;
		try {
			
			stmt  = database.conn.createStatement();		
			return stmt.execute("DELETE FROM `roaming` WHERE `itemid`="+item.getId()+";");
			
		}catch (Exception e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		return false;
	}
	
	public List<RoamingItem> loadRoamingItems(LocalMap map){
		
		List<RoamingItem> items = new Vector<RoamingItem>();
		synchronized(database) {
		
			Statement stmt;
			try {
				stmt = database.conn.createStatement();
				
				ResultSet rs = stmt.executeQuery("SELECT * FROM `roaming` WHERE `mapid` = "+map.getId()+";");
				
				while (rs.next()) 
				{
					int itemid = rs.getInt("itemid");
					Item item = ItemFactory.loadItem(itemid);
					
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
		if (!checkDatabase())
			return ;
		Item item = roamingItem.getItem();
		Position position = roamingItem.getPosition();
		saveItem(item);
		
		int itemId = item.getId();
		Statement stmt;
		try {
				
			deleteRoamingItem(item);
			stmt  = database.conn.createStatement();
			String q = 
			"INSERT INTO `roaming` (`itemid`,`mapid`,`x`,`y`,`z`,`rotation`) VALUES ("+
			itemId+","+
			position.getMap().getId()+","+
			position.getX()+","+
			position.getY()+","+
			position.getZ()+","+
			position.getRotation()+");";
			stmt.execute(q);
		
		} 
		catch (Exception e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		
	}
	public synchronized void saveItem(Item item){
		if (!checkDatabase())
			return ;
			
		Statement stmt;
		try {
			stmt  = database.conn.createStatement();
			
			int itemId = item.getId();
			if(itemId!=-1){
				
				int res = stmt.executeUpdate("UPDATE `items` SET `type`="+item.getType()+", `gemnumber`="+item.getGemNumber()+", `extrastats`="+item.getExtraStats()+" WHERE `Id` = "+itemId);
				if(res==0){
					Logger.getLogger(DatabaseUtils.class).error("item not found: "+itemId);					
				}
				return;
			}
			
			//stmt.execute("LOCK TABLES characters WRITE, items WRITE;");
			ResultSet rs = stmt.executeQuery("SELECT Rank FROM(SELECT `Id`,@rownum:=@rownum+1 `rank` FROM (SELECT `Id` FROM characters UNION SELECT `Id` FROM items Order By `Id`) t,(SELECT @rownum:=0) r) t2 WHERE Rank <> Id LIMIT 1 UNION SELECT IF(MAX(`Id`) IS NULL, 1 ,MAX(`Id`)+1) FROM (SELECT `Id` FROM characters UNION SELECT `Id` FROM items) t3 LIMIT 1");
			rs.next();
			itemId = rs.getInt(1);
			System.out.println("itemId: "+itemId);
			
			stmt.execute("INSERT INTO items (id, type, gemnumber, extrastats)" +
					" VALUES ("+itemId+","+item.getType()+","
					+item.getGemNumber()+","+item.getExtraStats()+");");
			
			item.setId(itemId);
			
			
			//stmt.execute("UNLOCK TABLES");
			
		} 
		catch (Exception e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		
		
	}
	
	public void deleteItem(Item item)
	{
		if (item==null)return;
		if (!checkDatabase())return ;
		
		Statement stmt;
		
		try {
			stmt  = database.conn.createStatement();
			
			stmt.execute("DELETE FROM items WHERE id='"+item.getId()+"';");
				
		} 
		catch (SQLException e) 
		{
			
			Logger.getLogger(this.getClass()).warn("Exception",e);
			
		}
	}
	
	public  void saveSkills(Player player) {
		
		if (!checkDatabase())
			return;
		
		Statement stmt;
		
		try {
			stmt = database.conn.createStatement();
			int playerId = player.getId();
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
		if (!checkDatabase())
			return;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id,level FROM skills WHERE charid="+player.getId()+";");			
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
		if (!checkDatabase())
			return;
		if (player.getEquipment()==null)return;
		
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM equipment WHERE charid="+player.getId()+";");
			Equipment eq = player.getEquipment();
			
			String query = "INSERT INTO equipment (charid, slot, itemid) VALUES ";
			String data = "";
			int playerId = player.getId();
			for(Slot slot: Slot.values())
			{
				Item item = eq.getItem(slot);
				if(item!=null){
					if(!data.isEmpty())
						data+= ", ";
					data+="("+playerId+","+slot.value()+","+item.getId()+")";		
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
				
		if (!checkDatabase())
			return;

		try {
			Statement invStmt = database.conn.createStatement();
			
			ResultSet stashTable = invStmt.executeQuery("SELECT * FROM warehouse WHERE accountid="+client.getAccountId()+";");
			client.getPlayer().getStash().clearStash();
			Item item = null;
						
			while (stashTable.next()) 
			{
				if(stashTable.getInt("pos") == 12){
					/*
					item = new Item(0);
					com.googlecode.reunion.jreunion.server.ItemManager.getEntityManager().loadEntity(item,stashTable.getInt("uniqueitemid"));
					DatabaseUtils.getInstance().loadItemInfo(item);
					*/
				}
				else{ 
					item = ItemFactory.loadItem(stashTable.getInt("itemid"));
				}
				StashItem stashItem =	new StashItem(stashTable.getInt("pos"), item);
				client.getPlayer().getStash().addItem(stashItem);
			}
						
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void saveStash(Client client){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM warehouse WHERE accountid="+client.getAccountId()+";");
			Iterator<StashItem> stashIter = client.getPlayer().getStash().itemListIterator();
			
			while(stashIter.hasNext())
			{
				StashItem stashItem = stashIter.next();
				
				stmt.execute("INSERT INTO stash (accountid, pos, itemid)" +
						" VALUES ("+client.getAccountId()+ ","
						+stashItem.getPos()+ ","
						+stashItem.getItem().getId()+ ");");
			}
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		  }
	}
	
	public void loadExchange(Player player){
		
		if (!checkDatabase())
			return;

		try {
			Statement invStmt = database.conn.createStatement();
			
			ResultSet exchangeTable = invStmt.executeQuery("SELECT * FROM exchange WHERE charid="+player.getId()+";");
						
			while (exchangeTable.next()) 
			{
				Item item = ItemFactory.loadItem(exchangeTable.getInt("itemid"));
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
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM exchange WHERE charid="+player.getId()+";");
			
			Iterator<ExchangeItem> exchangeIter = player.getExchange().itemListIterator();
			
			while(exchangeIter.hasNext())
			{
				ExchangeItem exchangeItem = exchangeIter.next();
				
				stmt.execute("INSERT INTO exchange (charid, itemid, x, y)" +
						" VALUES ("+player.getId()+ ","
								   +exchangeItem.getItem().getId()+","
								   +exchangeItem.getX()+","
								   +exchangeItem.getY()+");");
			}
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		  }
	}
	
	public void deleteGuild(int id)
	{
		if (!checkDatabase())return ;
			
		Statement stmt;
		try {
			stmt  = database.conn.createStatement();
			
			stmt.execute("DELETE FROM guilds WHERE id='"+id+"';");
				
		} 
		catch (SQLException e) 
		{
			
			Logger.getLogger(this.getClass()).warn("Exception",e);
			
		}
	}
	
	public void loadQuickSlot(Player player){
		
		if (!checkDatabase())
			return;

		try {
			Statement invStmt = database.conn.createStatement();
			
			ResultSet quickSlotTable = invStmt.executeQuery("SELECT * FROM quickslot WHERE charid="+player.getId()+";");
						
			while (quickSlotTable.next()) 
			{
				Item item = ItemFactory.loadItem(quickSlotTable.getInt("itemid"));
				QuickSlotItem quickSlotItem = new QuickSlotItem(item,quickSlotTable.getInt("slot"));
				
				player.getQuickSlot().addItem(quickSlotItem);
			}
			
		} catch (SQLException e1) {
			Logger.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void saveQuickSlot(Player player){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM quickslot WHERE charid="+player.getId()+";");
			
			Iterator<QuickSlotItem> qsIter = player.getQuickSlot().getQuickSlotIterator();
			
			while(qsIter.hasNext())
			{
				QuickSlotItem qsItem = qsIter.next();
				
				stmt.execute("INSERT INTO quickslot (charid, itemid, slot)" +
						" VALUES ("+player.getId()+ ","+qsItem.getItem().getId()+","
						+qsItem.getSlot()+");");
			}
			
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
			return;
		  }
	}
	
	
	
}
