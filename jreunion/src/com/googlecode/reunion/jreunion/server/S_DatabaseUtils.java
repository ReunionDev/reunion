package com.googlecode.reunion.jreunion.server;

import java.sql.*;
import java.util.*;

import com.googlecode.reunion.jreunion.game.*;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_DatabaseUtils {
	
	private S_DatabaseUtils() {
		super();
		database = null;
		
	}
	
	private S_Database database;
	
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
	public void setDatabase(S_Database db) {
		database = db;
	}
	
	private static S_DatabaseUtils _instance = null;
	
	// Private constructor vervangt de standaard public constructor
	
	// gesynchroniseerde creator om muti-threading problemen te voorkomen
	// nog een controle om te voorkomen dat er meer dan 1 object wordt
	// geinstantieerd
	private synchronized static void createInstance() {
		if (_instance == null) {
			_instance = new S_DatabaseUtils();
		}
	}
	
	public static S_DatabaseUtils getInstance() {
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
	
	public int getVersion() {
		if (!checkDatabase())
			return -1;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			ResultSet rs = stmt
			.executeQuery("SELECT data FROM settings WHERE id='clientversion'");
			if (rs.next()) {
				String s = rs.getString("data");
				
				return Integer.parseInt(s);
			} else
				return -2;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return -3;
		}
		
	}
	
	public float getSessionRadius() {
		if (!checkDatabase())
			return -1;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			ResultSet rs = stmt
			.executeQuery("SELECT data FROM settings WHERE id='sessionradius'");
			if (rs.next())
				return rs.getFloat("data");
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
			.executeQuery("SELECT slotnumber,characterid FROM slots WHERE accountid="
					+ accountId + " ;");
			int chars=0;
			while (rs.next()) {
				
				slotlist[rs.getInt("slotnumber")]=rs.getInt("characterid");
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
					
					
					
					G_Equipment eq = getEquipment(slotlist[slotnr]);
					
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
					if (eq.getSecondHand()!=null) eqShield = eq.getSecondHand().getType();
					
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
	
	public G_Equipment getEquipment(int charId) {
		G_Equipment eq = new G_Equipment();
		if (!checkDatabase())
			return null;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM equipment WHERE charid="+ charId + ";");
			if (rs.next()) 
			{
				
				eq.setHelmet((G_Armor)S_ItemFactory.loadItem(rs.getInt("head")));
				eq.setArmor((G_Armor)S_ItemFactory.loadItem(rs.getInt("body")));
				eq.setPants((G_Armor)S_ItemFactory.loadItem(rs.getInt("legs")));
				eq.setBoots((G_Armor)S_ItemFactory.loadItem(rs.getInt("feet")));
				eq.setFirstHand((G_Weapon)S_ItemFactory.loadItem(rs.getInt("weapon")));
				eq.setShoulderMount(S_ItemFactory.loadItem(rs.getInt("shouldermount")));
				eq.setBracelet((G_Bracelet)S_ItemFactory.loadItem(rs.getInt("bracelet")));
				eq.setRing((G_Ring)S_ItemFactory.loadItem(rs.getInt("ring")));
				eq.setNecklace((G_Necklace)S_ItemFactory.loadItem(rs.getInt("necklace")));
				eq.setSecondHand(S_ItemFactory.loadItem(rs.getInt("shield")));
			}
			
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			
		}
		return eq;
	}
	
	public void setCharWear(S_Client client, int slot, int itemType) {
		if (!checkDatabase())
			return;
		String field="";
		
		switch(slot){
		case 0: {field="head";break;}
		case 1: {field="body";break;}
		case 2: {field="legs";break;}
		case 3: {field="shouldermount";break;}
		case 4: {field="feet";break;}
		case 5: {field="shield";break;}
		case 6: {field="necklace";break;}
		case 7: {field="ring";break;}
		case 8: {field="bracelet";break;}
		case 9: {field="weapon";break;}
		default: break;
		}
		
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			
			//ResultSet rs = stmt.executeQuery("SELECT * FROM equipment WHERE charid="+client.accountId+";");
			
			stmt.execute("INSERT INTO equipment ("+field+")VALUES ("+itemType+")WHERE charid="+client.getPlayer().getEntityId()+";");
			
		}catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	public G_Player loadCharStatus(int characterId){
		G_Player player = null;
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
				player = G_Player.createPlayer(race);
				
				G_EntityManager.getEntityManager().loadEntity(player,characterId);
				
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
	
	public void saveCharStatus(G_Player player){
		
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
		
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
								    +player.getDex()+ ","
								    +player.getCons()+ ","
								    +player.getLead()+ ","
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
	
	public void updateCharStatus(G_Player player, int id, int value)
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
	
	public void CreateChar(S_Client client, int slotNumber, String charName,
			int race, int sex, int hairStyle, int str, int wis, int dex, int con,
			int lead) {
		if (!checkDatabase())
			return;
		Statement stmt;
		try {
						
			stmt = database.conn.createStatement();
			G_Entity entity  = new G_Entity();
			G_EntityManager.getEntityManager().createEntity(entity);
			int characterId = entity.getEntityId();

			G_EntityManager.getEntityManager().removeEntity(entity);
						
			G_Player player = G_Player.createPlayer(race);			
			
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
			
			player.setLime(Integer.parseInt(S_Reference.getInstance().getServerReference().getItem("Server").getMemberValue("StartLime")));
			
			G_EntityManager.getEntityManager().loadEntity(player,characterId);
			client.setPlayer(player);
		
			saveCharStatus(player);
			
			stmt.execute("INSERT INTO slots (characterid, slotnumber, accountid) VALUES ("
					+ characterId + ","
					+ slotNumber + ","
					+ client.getAccountId() + "); ");
			
			G_Armor top = (G_Armor)S_ItemFactory.createItem(326);
			G_Armor legs = (G_Armor)S_ItemFactory.createItem(343);
			G_Potion hpPot1 = (G_Potion)S_ItemFactory.createItem(145);
			G_Potion hpPot2 = (G_Potion)S_ItemFactory.createItem(145);
			G_Potion hpPot3 = (G_Potion)S_ItemFactory.createItem(145);
			G_Potion hpPot4 = (G_Potion)S_ItemFactory.createItem(145);
			G_Potion hpPot5 = (G_Potion)S_ItemFactory.createItem(145);
			G_Potion hpPot6 = (G_Potion)S_ItemFactory.createItem(145);
			G_Weapon weapon = null;
			
			switch(race){
				case 0: {weapon = (G_Axe)S_ItemFactory.createItem(48); break;}
				case 1: {weapon = (G_StaffWeapon)S_ItemFactory.createItem(171); break;}
				case 2: {weapon = (G_RingWeapon)S_ItemFactory.createItem(431); break;}
				case 3: {weapon = (G_GunWeapon)S_ItemFactory.createItem(204); break;}
				case 4: {weapon = (G_Sword)S_ItemFactory.createItem(168); break;}
				default: break;
			}
						
			stmt.execute("INSERT INTO equipment (charid, head, body, legs, feet, weapon, shield, shouldermount, bracelet, ring, necklace) VALUES ("+characterId+",-1,"+top.getEntityId()+","+legs.getEntityId()+",-1,-1,-1,-1,-1,-1,-1);");
			
			player.getQuickSlot().addItem(new G_QuickSlotItem(hpPot1,0));
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
			.execute("DELETE FROM slots WHERE slotnumber = "
					+ slotNumber
					+ " and accountid = "
					+ accountId
					+ ";");
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	public G_Player loadChar(int slotNumber, int accountId,S_Client client) {
		
		G_Player player=null;
		if (!checkDatabase())
			return null;
		int characterId = -1;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			ResultSet rs = stmt
			.executeQuery("(SELECT characterid FROM slots WHERE accountid ="
					+ accountId
					+ " and slotnumber = "
					+ slotNumber + ");");
			if (rs.next()) {
				characterId = rs.getInt("characterid");
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
	
	public G_Player loadInventory(G_Player player){
		if (!checkDatabase())
			return null;
		Statement invStmt;
		try {
			invStmt = database.conn.createStatement();
			
			ResultSet invTable = invStmt.executeQuery("SELECT * FROM inventory WHERE characterid="+player.getEntityId()+";");
			
			while (invTable.next()) 
			{
				G_Item item = S_ItemFactory.loadItem(invTable.getInt("uniqueitemid"));
				if (item!=null)
				player.getInventory().addItem(invTable.getInt("x"),invTable.getInt("y"),item,invTable.getInt("tab"));
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}
		return player;
	}
	public void saveInventory(G_Player player){
		if (!checkDatabase())
			return;
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM inventory WHERE characterid="+player.getEntityId()+";");
			Iterator<G_InventoryItem> iter = player.getInventory().getInventoryIterator();
			
			while(iter.hasNext())
			{
				G_InventoryItem invItem = iter.next();				
				G_Item item = invItem.getItem();
				updateItemInfo(item);				
				
				stmt.execute("INSERT INTO inventory (characterid, uniqueitemid, tab, x, y)" +
						" VALUES ("+player.getEntityId()+ ",'"+item.getEntityId()+"',"+invItem.getTab()+
						","+invItem.getPosX()+ ","+invItem.getPosY()+ ");");				
			}
			
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
	
	public void loadItemInfo(G_Item item )
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
	
	public void addItem(G_Item item)
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
	
	public void deleteItem(G_Item item)
	{
		if (item==null)return;
		if (!checkDatabase())return ;
		
		G_EntityManager.getEntityManager().removeEntity(item);
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
	
	public void updateItemInfo(G_Item item)
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
	
	public  void saveSkills(G_Player player) {
		
		if (!checkDatabase())
			return;
		
		Statement stmt;
		
		try {
			stmt = database.conn.createStatement();
			int playerId = player.getEntityId();
			stmt.execute("DELETE FROM skills WHERE charid="+playerId+";");

			String query = "INSERT INTO skills (charid,id,level) VALUES ";
			String data = "";
			Iterator<G_Skill> skillsIter = player.getCharSkill().getSkillListIterator();
			
			while(skillsIter.hasNext()){
				G_Skill skill = (G_Skill)skillsIter.next();
				
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
	
	public  void loadSkills(G_Player player) {
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
	
	public void saveEquipment(G_Player player){
		if (!checkDatabase())
			return;
		if (player.getEquipment()==null)return;
		
		Statement stmt;
		try {
			stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM equipment WHERE charid="+player.getEntityId()+";");
			G_Equipment eq = player.getEquipment();
			
			int eqHelmet = -1;
			int eqArmor = -1;
			int eqPants = -1;
			int eqShoulderMount = -1;
			int eqBoots = -1;
			int eqFirstHand = -1;
			int eqSecondHand = -1;
			int eqRing = -1;
			int eqNecklace = -1;
			int eqBracelet = -1;
									
			if (eq.getHelmet()!=null) {eqHelmet = eq.getHelmet().getEntityId();updateItemInfo(eq.getHelmet());}
			if (eq.getArmor()!=null) {eqArmor = eq.getArmor().getEntityId();updateItemInfo(eq.getArmor());}
			if (eq.getPants()!=null) {eqPants = eq.getPants().getEntityId();updateItemInfo(eq.getPants());}
			if (eq.getShoulderMount()!=null) {eqShoulderMount = eq.getShoulderMount().getEntityId();updateItemInfo(eq.getShoulderMount());}
			if (eq.getBoots()!=null) {eqBoots = eq.getBoots().getEntityId();updateItemInfo(eq.getBoots());}
			if (eq.getFirstHand()!=null) {eqFirstHand = eq.getFirstHand().getEntityId();updateItemInfo(eq.getFirstHand());}
			if (eq.getSecondHand()!=null) {eqSecondHand = eq.getSecondHand().getEntityId();updateItemInfo(eq.getSecondHand());}
			if (eq.getRing()!=null) {eqRing = eq.getRing().getEntityId();updateItemInfo(eq.getRing());}
			if (eq.getNecklace()!=null) {eqNecklace = eq.getRing().getEntityId();updateItemInfo(eq.getNecklace());}
			if (eq.getBracelet()!=null) {eqBracelet = eq.getBracelet().getEntityId();updateItemInfo(eq.getBracelet());}
						
			stmt.execute("INSERT INTO equipment (charid, head, body, legs, feet, weapon, shield, shouldermount, bracelet, ring, necklace)VALUES ("+player.getEntityId()+","+eqHelmet+","+eqArmor+","+eqPants+","+eqBoots+ ","+eqFirstHand+","+eqSecondHand+","+eqShoulderMount+","+eqBracelet+","+eqRing+","+eqNecklace+"); ");
			
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	public void loadStash(S_Client client){
				
		if (!checkDatabase())
			return;

		try {
			Statement invStmt = database.conn.createStatement();
			
			ResultSet stashTable = invStmt.executeQuery("SELECT * FROM stash WHERE accountid="+client.getAccountId()+";");
			client.getPlayer().getStash().clearStash();
			G_Item item;
						
			while (stashTable.next()) 
			{
				if(stashTable.getInt("pos") == 12){
					item = new G_Item(0);
					com.googlecode.reunion.jreunion.game.G_EntityManager.getEntityManager().loadEntity(item,stashTable.getInt("uniqueitemid"));
					S_DatabaseUtils.getInstance().loadItemInfo(item);
				}
				else{ 
					item = S_ItemFactory.loadItem(stashTable.getInt("uniqueitemid"));
				}
				G_StashItem stashItem =	new G_StashItem(stashTable.getInt("pos"), item);
				client.getPlayer().getStash().addItem(stashItem);
			}
						
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	public void saveStash(S_Client client){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM stash WHERE accountid="+client.getAccountId()+";");
			Iterator<G_StashItem> stashIter = client.getPlayer().getStash().itemListIterator();
			
			while(stashIter.hasNext())
			{
				G_StashItem stashItem = stashIter.next();
				
				stmt.execute("INSERT INTO stash (accountid, pos, uniqueitemid)" +
						" VALUES ("+client.getAccountId()+ ","
						+stashItem.getPos()+ ","
						+stashItem.getItem().getEntityId()+ ");");
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		  }
	}
	
	public void loadExchange(G_Player player){
		
		if (!checkDatabase())
			return;

		try {
			Statement invStmt = database.conn.createStatement();
			
			ResultSet exchangeTable = invStmt.executeQuery("SELECT * FROM exchange WHERE characterid="+player.getEntityId()+";");
						
			while (exchangeTable.next()) 
			{
				G_Item item = S_ItemFactory.loadItem(exchangeTable.getInt("uniqueitemid"));
				G_ExchangeItem exchangeItem = new G_ExchangeItem(item,
						exchangeTable.getInt("x"), exchangeTable.getInt("y"));
				
				player.getExchange().addItem(exchangeItem);
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	public void saveExchange(G_Player player){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM exchange WHERE characterid="+player.getEntityId()+";");
			
			Iterator<G_ExchangeItem> exchangeIter = player.getExchange().itemListIterator();
			
			while(exchangeIter.hasNext())
			{
				G_ExchangeItem exchangeItem = exchangeIter.next();
				
				stmt.execute("INSERT INTO exchange (characterid, uniqueitemid, x, y)" +
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
	
	public void loadQuickSlot(G_Player player){
		
		if (!checkDatabase())
			return;

		try {
			Statement invStmt = database.conn.createStatement();
			
			ResultSet quickSlotTable = invStmt.executeQuery("SELECT * FROM quickslot WHERE characterid="+player.getEntityId()+";");
						
			while (quickSlotTable.next()) 
			{
				G_Item item = S_ItemFactory.loadItem(quickSlotTable.getInt("uniqueitemid"));
				G_QuickSlotItem quickSlotItem = new G_QuickSlotItem(item,quickSlotTable.getInt("slot"));
				
				player.getQuickSlot().addItem(quickSlotItem);
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	public void saveQuickSlot(G_Player player){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = database.conn.createStatement();
			stmt.execute("DELETE FROM quickslot WHERE characterid="+player.getEntityId()+";");
			
			Iterator<G_QuickSlotItem> qsIter = player.getQuickSlot().getQuickSlotIterator();
			
			while(qsIter.hasNext())
			{
				G_QuickSlotItem qsItem = qsIter.next();
				
				stmt.execute("INSERT INTO quickslot (characterid, uniqueitemid, slot)" +
						" VALUES ("+player.getEntityId()+ ","+qsItem.getItem().getEntityId()+","
						+qsItem.getSlot()+");");
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		  }
	}
	
}
