package com.googlecode.reunion.jreunion.server;

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

import com.googlecode.reunion.jreunion.game.Armor;
import com.googlecode.reunion.jreunion.game.Axe;
import com.googlecode.reunion.jreunion.game.Entity;
import com.googlecode.reunion.jreunion.game.EntityManager;
import com.googlecode.reunion.jreunion.game.Enums.G_EquipmentSlot;
import com.googlecode.reunion.jreunion.game.Equipment;
import com.googlecode.reunion.jreunion.game.ExchangeItem;
import com.googlecode.reunion.jreunion.game.GunWeapon;
import com.googlecode.reunion.jreunion.game.InventoryItem;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Potion;
import com.googlecode.reunion.jreunion.game.QuickSlotItem;
import com.googlecode.reunion.jreunion.game.RingWeapon;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.StaffWeapon;
import com.googlecode.reunion.jreunion.game.StashItem;
import com.googlecode.reunion.jreunion.game.Sword;
import com.googlecode.reunion.jreunion.game.Weapon;
import com.googlecode.reunion.jreunion.server.database.DatabaseAction;
import com.googlecode.reunion.jreunion.server.database.SaveInventory;
import com.mysql.jdbc.MySQLConnection;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class DatabaseUtils {
	
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
			e1.printStackTrace();
			return -1;
		}
		
	}
	
	public String getCharList(int accountId) {
		if (!checkDatabase())
			return null;
		String charlist = new String();
		int slotlist[] = new int[5];
		for (int i=0;i<5;i++) slotlist[i]=-1;
		
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			ResultSet rs = stmt
			.executeQuery("SELECT slot, charid FROM slots WHERE accountid="
					+ accountId + " ;");
			int chars=0;
			while (rs.next()) {
				
				slotlist[rs.getInt("slot")]=rs.getInt("charid");
				chars++;
				
			}
			System.out.println("found " + chars
					+ " char(s) for Account(" + accountId + ")");
			
			
			for (int slotnr=0;slotnr<5;slotnr++)
			{
				
				if (slotlist[slotnr]==-1) continue;
				
				rs = stmt.executeQuery("SELECT * FROM characters WHERE id="
						+ slotlist[slotnr] + ";");
				
				if (rs.next()) {
					
					Equipment eq = loadEquipment(slotlist[slotnr]);
					
					int eqHelmet = -1;
					int eqArmor = -1;
					int eqPants = -1;
					int eqShoulderMount = -1;
					int eqBoots = -1;
					int eqShield = -1;
					if (eq.getHelmet()!=null) eqHelmet = eq.getHelmet().getType();
					if (eq.getArmor()!=null) eqArmor = eq.getArmor().getType();
					if (eq.getPants()!=null) eqPants = eq.getPants().getType();
					if (eq.getShoulderMount()!=null) eqShoulderMount = eq.getShoulderMount().getType();
					if (eq.getBoots()!=null) eqBoots = eq.getBoots().getType();
					if (eq.getOffHand()!=null) eqShield = eq.getOffHand().getType();
					
					charlist += "chars_exist " + slotnr + " "
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
					+ rs.getString("str") + " " + rs.getString("wis")
					+ " " + rs.getString("dex") + " "
					+ rs.getString("con") + " " + rs.getString("lea")
					+ " " + eqHelmet + " " + eqArmor
					+ " " + eqPants	+ " " + eqShoulderMount
					+ " " + eqBoots + " " + eqShield + " 1\n";
					// chars_exist [SlotNumber] [Name] [Race] [Sex] [HairStyle]
					// [Level] [Vitality] [Stamina] [Magic] [Energy] [Vitality]
					// [Stamina] [Magic] [Energy] [Strength] [Wisdom]
					// [Dexterity] [Constitution] [Leadership] [HeadGear]
					// [Chest] [Pants] [SoulderMount] [Feet] [Shield] 0
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}
		
		charlist += "chars_end\n";
		return charlist;
	}
	
	public Equipment loadEquipment(int charid) {
		
		Equipment equipment = new Equipment();
		if (!checkDatabase())
			return null;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM equipment WHERE charid="+ charid + ";");
			while(rs.next()) 
			{
				int slotId = rs.getInt("slot");
				
				G_EquipmentSlot slot = G_EquipmentSlot.byValue(slotId);
				equipment.setItem(slot,ItemFactory.loadItem(rs.getInt("itemid")));
			}
			
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			
		}
		return equipment;
		
	}

	public void loadEquipment(Player player) {
		Equipment equipment = loadEquipment(player.getEntityId());
		player.setEquipment(equipment);
	}
	
	
	public Player loadCharStatus(int characterId){
		Player player = null;
		if (!checkDatabase())
			return null;
		Statement stmt;		
		try {
			stmt = database.conn.createStatement();
			ResultSet rs = stmt
			.executeQuery("SELECT * FROM characters WHERE id="
					+ characterId + ";");
			if (rs.next()) {
				int race = rs.getInt("race");				
				player = Player.createPlayer(race);
				
				EntityManager.getEntityManager().loadEntity(player,characterId);
				
				player.setStrength(rs.getInt("str"));
				player.setWisdom(rs.getInt("wis"));
				player.setDexterity(rs.getInt("dex"));
				player.setConstitution(rs.getInt("con"));
				player.setLeadership(rs.getInt("lea"));
				player.setLevel(rs.getInt("level"));
				player.setCurrHp(rs.getInt("currHp"));
				player.setMaxHp(rs.getInt("maxHp"));
				player.setCurrStm(rs.getInt("currStm"));
				player.setMaxStm(rs.getInt("maxStm"));
				player.setCurrMana(rs.getInt("currMana"));
				player.setMaxMana(rs.getInt("maxMana"));
				player.setCurrElect(rs.getInt("currElect"));
				player.setMaxElect(rs.getInt("maxElect"));
				player.setTotalExp(rs.getInt("totalExp"));
				player.setLvlUpExp(rs.getInt("lvlUpExp"));
				player.setLime(rs.getInt("lime"));
				player.setStatusPoints(rs.getInt("statusPoints"));
				player.setPenaltyPoints(rs.getInt("penaltyPoints"));
				player.setSex(rs.getInt("sex"));
				player.setName(rs.getString("name"));
				player.setGuildId(rs.getInt("guildid"));
				player.setGuildLvl(rs.getInt("guildlvl"));
				player.setAdminState(rs.getInt("userlevel"));
				player.setHairStyle(rs.getInt("hair"));
				return player;
			} else
				return null;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public void saveCharStatus(Player player){
		
		Client client = Server.getInstance().getNetworkModule().getClient(player);
		
		if(client == null)
			return;
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM characters WHERE id="+player.getEntityId()+";");
						
			stmt.execute("INSERT INTO characters (id,accountid,name,level,str,wis,dex,con,lea,race,sex,hair," +
												  "currHp,MaxHp,currMana,maxMana,currElect,maxElect,currStm," +
												  "maxStm,totalExp,lvlUpExp,lime,statusPoints,penaltyPoints," +
												  "guildid,guildlvl)" +
						 " VALUES ("+player.getEntityId()+ ","
								    +client.getAccountId()+ ",'"
								    +player.getName()+ "',"
								    +player.getLevel()+ ","
								    +player.getStr()+ ","
								    +player.getWis()+ ","
								    +player.getDexterity()+ ","
								    +player.getConstitution()+ ","
								    +player.getLeadership()+ ","
								    +player.getRace()+ ","
								    +player.getSex()+ ","
								    +player.getHairStyle()+ ","
								    +player.getCurrHp()+ ","
								    +player.getMaxHp()+ ","
								    +player.getCurrMana()+ ","
								    +player.getMaxMana()+ ","
								    +player.getCurrElect()+ ","
								    +player.getMaxElect()+ ","
								    +player.getCurrStm()+ ","
								    +player.getMaxStm()+ ","
								    +player.getTotalExp()+ ","
								    +player.getLvlUpExp()+ ","
								    +player.getLime()+ ","
								    +player.getStatusPoints()+ ","
								    +player.getPenaltyPoints()+ ","
								    +player.getGuildId()+ ","
								    +player.getGuildLvl()+ ");");
						
		} catch (SQLException e1) {
			e1.printStackTrace();
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
			case 14: {status = "str"; break; }
			case 15: {status = "wis"; break; }
			case 16: {status = "dex"; break; }
			case 17: {status = "con"; break; }
			case 18: {status = "lea"; break; }
			case 19: {status = "penaltyPoints"; break; }
			default: return;
		}
					
		Statement stmt;
		try {
			stmt  = database.conn.createStatement();
			
			stmt.execute("UPDATE characters SET "+status+" = '"+value+"' WHERE id='"+player.getEntityId()+"';");

		} 
		catch (SQLException e) 
		{
			
			e.printStackTrace();
			
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
			e1.printStackTrace();
			return false;
		}
	}
	
	public void createChar(Client client, int slot, String charName,
			int race, int sex, int hairStyle, int str, int wis, int dex, int con,
			int lead) {
		if (!checkDatabase())
			return;
		Statement stmt;
		try {
						
			stmt = database.conn.createStatement();
			Entity entity  = new Entity();
			EntityManager.getEntityManager().createEntity(entity);
			int characterId = entity.getEntityId();

			EntityManager.getEntityManager().removeEntity(entity);
						
			Player player = Player.createPlayer(race);			
			
			player.setLevel(1);
			player.setName(charName);
			player.setSex(sex);
			player.setHairStyle(hairStyle);
			player.setStrength(str);
			player.setWisdom(wis);
			player.setDexterity(dex);
			player.setConstitution(con);
			player.setLeadership(lead);
			
			player.setMaxHp(((str*1)+(con*2)));
			player.setCurrHp(player.getMaxHp());
			player.setMaxMana(((wis*2)+(dex*1)));
			player.setCurrMana(player.getMaxMana());
			player.setMaxElect(((wis*1)+(dex*2)));
			player.setCurrElect(player.getMaxElect());
			
			player.setMaxStm(((str*2)+(con*1)));
			player.setCurrStm(player.getMaxStm());
			
			player.setLime(Integer.parseInt(Reference.getInstance().getServerReference().getItem("Server").getMemberValue("StartLime")));
			
			EntityManager.getEntityManager().loadEntity(player,characterId);
			client.setPlayer(player);
		
			saveCharStatus(player);
			
			stmt.execute("INSERT INTO slots (charid, slot, accountid) VALUES ("
					+ characterId + ","
					+ slot + ","
					+ client.getAccountId() + "); ");
			
			Potion hpPot1 = (Potion)ItemFactory.createItem(145);
			Potion hpPot2 = (Potion)ItemFactory.createItem(145);
			Potion hpPot3 = (Potion)ItemFactory.createItem(145);
			Potion hpPot4 = (Potion)ItemFactory.createItem(145);
			Potion hpPot5 = (Potion)ItemFactory.createItem(145);
			Potion hpPot6 = (Potion)ItemFactory.createItem(145);
			Weapon weapon = null;
			
			switch(race){
				case 0: {weapon = (Axe)ItemFactory.createItem(48); break;}
				case 1: {weapon = (StaffWeapon)ItemFactory.createItem(171); break;}
				case 2: {weapon = (RingWeapon)ItemFactory.createItem(431); break;}
				case 3: {weapon = (GunWeapon)ItemFactory.createItem(204); break;}
				case 4: {weapon = (Sword)ItemFactory.createItem(168); break;}
				default: break;
			}
			Equipment equipment = player.getEquipment();
			Armor chest = (Armor)ItemFactory.createItem(326);
			Armor pants = (Armor)ItemFactory.createItem(343);
			
			equipment.setItem(G_EquipmentSlot.CHEST, chest);
			equipment.setItem(G_EquipmentSlot.PANTS, pants);
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
			e1.printStackTrace();
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
			e1.printStackTrace();
			return;
		}
	}
	
	public Player loadChar(int slotNumber, int accountId,Client client) {
		
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
					+ slotNumber + ");");
			if (rs.next()) {
				characterId = rs.getInt("charid");
			}
			
			player = loadCharStatus(characterId);
			
			System.out.println("Loaded: " + player.getName());
			
			client.setPlayer(player);
			client.characterId=characterId;
			
		} catch (SQLException e1) {
			
			e1.printStackTrace();
			
		}
		
		return player;
	}
	
	public Player loadInventory(Player player){
		if (!checkDatabase())
			return null;
		Statement invStmt;
		try {
			invStmt = database.conn.createStatement();
			
			ResultSet invTable = invStmt.executeQuery("SELECT * FROM inventory WHERE charid="+player.getEntityId()+";");
			
			while (invTable.next()) 
			{
				Item item = ItemFactory.loadItem(invTable.getInt("itemid"));
				if (item!=null)
				player.getInventory().addItem(invTable.getInt("x"),invTable.getInt("y"),item,invTable.getInt("tab"));
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
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
			e.printStackTrace();
		}
		*/
	
		
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM inventory WHERE charid="+player.getEntityId()+";");
		
			String query = "INSERT INTO inventory (charid, itemid, tab, x, y) VALUES ";
			String data = "";
			
			Iterator<InventoryItem> iter = player.getInventory().getInventoryIterator();
			
			while(iter.hasNext())
			{
				InventoryItem invItem = iter.next();
				Item item = invItem.getItem();
				updateItemInfo(item);
				/*
				
				PreparedStatement statement = saveInventory.getInsertStatement();
				statement.setInt(1,player.getEntityId());
				statement.setInt(2,item.getEntityId());
				statement.setInt(3,invItem.getTab());
				statement.setInt(4,invItem.getPosX());
				statement.setInt(5,invItem.getPosY());
				statement.addBatch();				
				*/
				
				data+="("+player.getEntityId()+ ",'"+item.getEntityId()+"',"+invItem.getTab()+
					","+invItem.getPosX()+ ","+invItem.getPosY()+ ")";			
				if(iter.hasNext())
					data+= ", ";
			
			}
			if(!data.isEmpty())
				stmt.execute(query+data);
			
			//queue.add(saveInventory);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
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
			
			e.printStackTrace();
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
			
			e.printStackTrace();
			
		}
		return -1;
	}
	
	public void loadItemInfo(Item item )
	{
		if (item==null)return;
		if (!checkDatabase())return ;
		
		Statement stmt;
		try {
			stmt  = database.conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM items WHERE id='"+item.getEntityId()+"';");
			
			if (rs.next())
			{
				item.setGemNumber(rs.getInt("gemnumber"));
				item.setExtraStats(rs.getInt("extrastats"));
			}
			
		} 
		catch (SQLException e) 
		{
			
			e.printStackTrace();
			
		}
	}
	
	public void addItem(Item item)
	{
		if (!checkDatabase())
			return ;
			
		Statement stmt;
		try {
			stmt  = database.conn.createStatement();
			
			stmt.execute("INSERT INTO items (id, type, gemnumber, extrastats)" +
					" VALUES ("+item.getEntityId()+ ","+item.getType()+","
					+item.getGemNumber()+","+item.getExtraStats()+");");
				
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteItem(Item item)
	{
		if (item==null)return;
		if (!checkDatabase())return ;
		
		EntityManager.getEntityManager().removeEntity(item);
		Statement stmt;
		
		try {
			stmt  = database.conn.createStatement();
			
			stmt.execute("DELETE FROM items WHERE id='"+item.getEntityId()+"';");
				
		} 
		catch (SQLException e) 
		{
			
			e.printStackTrace();
			
		}
	}
	
	public void updateItemInfo(Item item)
	{
		if (item==null)return;
		if (!checkDatabase())return ;
				
		Statement stmt;
		try {
			stmt  = database.conn.createStatement();
			
			stmt.execute("UPDATE items SET gemnumber = '"+item.getGemNumber()+"',extrastats = '"+item.getExtraStats()+"' WHERE id='"+item.getEntityId()+"';");

		} 
		catch (SQLException e) 
		{
			
			e.printStackTrace();
			
		}
	}
	
	public  void saveSkills(Player player) {
		
		if (!checkDatabase())
			return;
		
		Statement stmt;
		
		try {
			stmt = database.conn.createStatement();
			int playerId = player.getEntityId();
			stmt.execute("DELETE FROM skills WHERE charid="+playerId+";");

			String query = "INSERT INTO skills (charid,id,level) VALUES ";
			String data = "";
			Iterator<Skill> skillsIter = player.getCharSkill().getSkillListIterator();
			
			while(skillsIter.hasNext()){
				Skill skill = (Skill)skillsIter.next();
				
				if(skill.getCurrLevel()>0){
					data+="("+playerId+","+skill.getId()+","+skill.getCurrLevel()+")";			
					if(skillsIter.hasNext())
						data+= ", ";
				}
			}
			if(!data.isEmpty())
				stmt.execute(query+data);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	public  void loadSkills(Player player) {
		if (!checkDatabase())
			return;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id,level FROM skills WHERE charid="+player.getEntityId()+";");			
			while (rs.next()) {
				int id = rs.getInt("id");
				int level = rs.getInt("level");
				player.getCharSkill().getSkill(id).setCurrLevel(level);
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
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
			stmt.execute("DELETE FROM equipment WHERE charid="+player.getEntityId()+";");
			Equipment eq = player.getEquipment();
			
			String query = "INSERT INTO equipment (charid, slot, itemid) VALUES ";
			String data = "";
			int playerId = player.getEntityId();
			for(G_EquipmentSlot slot:G_EquipmentSlot.values())
			{
				Item item = eq.getItem(slot);
				if(item!=null){
					if(!data.isEmpty())
						data+= ", ";
					data+="("+playerId+","+slot.value()+","+item.getEntityId()+")";		
				}
			}							
			if(!data.isEmpty())
				stmt.execute(query+data);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
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
			Item item;
						
			while (stashTable.next()) 
			{
				if(stashTable.getInt("pos") == 12){
					item = new Item(0);
					com.googlecode.reunion.jreunion.game.EntityManager.getEntityManager().loadEntity(item,stashTable.getInt("uniqueitemid"));
					DatabaseUtils.getInstance().loadItemInfo(item);
				}
				else{ 
					item = ItemFactory.loadItem(stashTable.getInt("itemid"));
				}
				StashItem stashItem =	new StashItem(stashTable.getInt("pos"), item);
				client.getPlayer().getStash().addItem(stashItem);
			}
						
		} catch (SQLException e1) {
			e1.printStackTrace();
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
						+stashItem.getItem().getEntityId()+ ");");
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		  }
	}
	
	public void loadExchange(Player player){
		
		if (!checkDatabase())
			return;

		try {
			Statement invStmt = database.conn.createStatement();
			
			ResultSet exchangeTable = invStmt.executeQuery("SELECT * FROM exchange WHERE charid="+player.getEntityId()+";");
						
			while (exchangeTable.next()) 
			{
				Item item = ItemFactory.loadItem(exchangeTable.getInt("itemid"));
				ExchangeItem exchangeItem = new ExchangeItem(item,
						exchangeTable.getInt("x"), exchangeTable.getInt("y"));
				
				player.getExchange().addItem(exchangeItem);
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	public void saveExchange(Player player){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM exchange WHERE charid="+player.getEntityId()+";");
			
			Iterator<ExchangeItem> exchangeIter = player.getExchange().itemListIterator();
			
			while(exchangeIter.hasNext())
			{
				ExchangeItem exchangeItem = exchangeIter.next();
				
				stmt.execute("INSERT INTO exchange (charid, itemid, x, y)" +
						" VALUES ("+player.getEntityId()+ ","
								   +exchangeItem.getItem().getEntityId()+","
								   +exchangeItem.getPosX()+","
								   +exchangeItem.getPosY()+");");
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
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
			
			e.printStackTrace();
			
		}
	}
	
	public void loadQuickSlot(Player player){
		
		if (!checkDatabase())
			return;

		try {
			Statement invStmt = database.conn.createStatement();
			
			ResultSet quickSlotTable = invStmt.executeQuery("SELECT * FROM quickslot WHERE charid="+player.getEntityId()+";");
						
			while (quickSlotTable.next()) 
			{
				Item item = ItemFactory.loadItem(quickSlotTable.getInt("itemid"));
				QuickSlotItem quickSlotItem = new QuickSlotItem(item,quickSlotTable.getInt("slot"));
				
				player.getQuickSlot().addItem(quickSlotItem);
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	public void saveQuickSlot(Player player){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM quickslot WHERE charid="+player.getEntityId()+";");
			
			Iterator<QuickSlotItem> qsIter = player.getQuickSlot().getQuickSlotIterator();
			
			while(qsIter.hasNext())
			{
				QuickSlotItem qsItem = qsIter.next();
				
				stmt.execute("INSERT INTO quickslot (charid, itemid, slot)" +
						" VALUES ("+player.getEntityId()+ ","+qsItem.getItem().getEntityId()+","
						+qsItem.getSlot()+");");
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		  }
	}
	
	
	public void work(){
		
		DatabaseAction action = null;
		
		while((action=queue.poll())!=null) {
			try{
				action.perform();
			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
		
	}
	
}
