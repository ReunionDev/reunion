package org.reunionemu.jreunion.server;

import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.reunionemu.jreunion.dao.ItemDao;
import org.reunionemu.jreunion.game.Equipment;
import org.reunionemu.jreunion.game.Equipment.Slot;
import org.reunionemu.jreunion.game.ExchangeItem;
import org.reunionemu.jreunion.game.HandPosition;
import org.reunionemu.jreunion.game.InventoryItem;
import org.reunionemu.jreunion.game.InventoryPosition;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.Pet;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Sex;
import org.reunionemu.jreunion.game.Position;
import org.reunionemu.jreunion.game.QuickSlotItem;
import org.reunionemu.jreunion.game.QuickSlotPosition;
import org.reunionemu.jreunion.game.RoamingItem;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.StashItem;
import org.reunionemu.jreunion.game.StashPosition;
import org.reunionemu.jreunion.game.items.pet.PetEquipment;
import org.reunionemu.jreunion.game.items.pet.PetEquipment.PetSlot;
import org.reunionemu.jreunion.model.jpa.RoamingItemImpl;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
@Service
public class Database {
	
	@Autowired
	DataSource datasource;
	
	public Connection connection = null; //reunion database
		
	@Autowired
	ItemDao<Item<?>> itemDao;
	
	private Database() {
		_instance = this;
	}
	
	@PostConstruct
	public void start(){

		try {
			connection = datasource.getConnection();
			LoggerFactory.getLogger(Database.class).info(getClass().getSimpleName() + " connection established");

		} catch (Exception e) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
		}
		
	}
	
	@PreDestroy
	public void stop() {

		if (connection != null) {
			try {
				connection.close();
				LoggerFactory.getLogger(Database.class).info(getClass().getName()
						+ " connection terminated");
			} catch (Exception e) { /* ignore close errors */
				
			}
		}
	}
		
	public boolean checkDatabase() {
		if (connection != null)
			return true;
		else
			return false;
	}
			
	private static Database _instance = null;
	
	private synchronized static void createInstance() {
		if (_instance == null) {
			_instance = new Database();
		}
	}
	
	public static Database getInstance() {
		if (_instance == null)
			createInstance();
		return _instance;
	}
	
	public Position getSavedPosition(Player player){
		if (!checkDatabase())
			return null;
		Position position = null;
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT `x`, `y`, `z`, `mapId` FROM `characters` WHERE `characters`.`id`="+player.getPlayerId()+" AND `mapId` IS NOT NULL AND `x` IS NOT NULL AND `y` IS NOT NULL AND `z` IS NOT NULL");
			
			if(rs.next()){
				Map map = Server.getInstance().getWorld().getMap(rs.getInt("mapId"));
				if(map != null){
					position = new Position(rs.getInt("x"),rs.getInt("y"),rs.getInt("z"), map, Double.NaN);
				}
			}
			rs.close();
		} catch (SQLException e) {
			LoggerFactory.getLogger(this.getClass()).error("Exception", e);
		}
		return position;
	}
	
	public void setSavedPosition(Player player){
		if (!checkDatabase())
			return;
		try {
			Statement stmt = connection.createStatement();
			Position position = player.getPosition();
			if(position!=null&&position.getMap()!=null)
				stmt.execute("UPDATE `characters` SET `x`="+position.getX()+", `y`="+position.getY()+", `z`="+position.getZ()+", `mapId`="+position.getMap().getId()+" WHERE `characters`.`id`="+player.getPlayerId());
			
		} catch (SQLException e) {
			LoggerFactory.getLogger(this.getClass()).error("Exception", e);
		}
	}
	
	public String getCharList(Client client) {
		if (!checkDatabase())
			return null;
		
		long accountId = client.getAccountId();
		String charlist ="";
		int chars = 0;
		
		try {
			Statement stmt = connection.createStatement();
			
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
				
				charlist += "chars_exist " + slot + " "
				+ (client.getVersion() >= 2000 ? rs.getString("id") + " " : "") // nga client have this extra value in the packet
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
				+ "0" + " " // unknown value
				+ eq.getTypeId(Slot.HELMET) + " " 
				+ eq.getTypeId(Slot.CHEST) + " " 
				+ eq.getTypeId(Slot.PANTS) + " " 
				+ eq.getTypeId(Slot.SHOULDER)	+ " "
				+ eq.getTypeId(Slot.BOOTS) + " " 
				+ eq.getTypeId(Slot.OFFHAND) 
				+ " 0\n"; //unknown value
				
				//chars_exist 3 12341234 0 0 0 2 90 12 15 15 90 90 15 15 30 5 5 30 10 309 -1 -1 -1 -1 -1 1
				// chars_exist [SlotNumber] [Name] [Race] [Sex] [HairStyle]
				// [Level] [Vitality] [Stamina] [Magic] [Energy] [Vitality]
				// [Stamina] [Magic] [Energy] [Strength] [Wisdom]
				// [Dexterity] [Constitution] [Leadership] [HeadGear]
				// [Chest] [Pants] [SoulderMount] [Feet] [Shield] 0
				chars++;
			}
			
		} catch (SQLException e) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
			return null;
		}
		
		LoggerFactory.getLogger(Database.class).info("found " + chars
				+ " char(s) for Account(" + accountId + ")");	
		
		charlist += "chars_end 0 "+accountId+"\n";
		return charlist;
	}
	
	public Equipment loadEquipment(Equipment equipment, int charid) {
		
		if (!checkDatabase())
			return null;
		
		Statement stmt;
		
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM equipment WHERE charid="+ charid + ";");
			while(rs.next()) 
			{
				int slotId = rs.getInt("slot");
				
				Item<?> item = itemDao.findOne((long)rs.getInt("itemid"));
				
				Slot slot = Slot.byValue(slotId);
				equipment.setItem(slot, item);
			}
			
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			
		}
		return equipment;
		
	}

	public Equipment loadEquipment(Player player) {		
		return loadEquipment(player.getEquipment(), player.getPlayerId());
	}
	
	public PetEquipment loadPetEquipment(PetEquipment equipment, int petid) {
		
		if (!checkDatabase())
			return null;
		
		Statement stmt;
		
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM petequipment WHERE petid="+ petid + ";");
			while(rs.next()) 
			{
				int slotId = rs.getInt("slot");
				
				Item<?> item = itemDao.findOne((long)rs.getInt("itemid"));
				
				PetSlot petSlot = PetSlot.byValue(slotId);
				equipment.setItem(petSlot, item);
			}
			
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			
		}
		return equipment;
	}
	
	public PetEquipment loadPetEquipment(Player player) {		
		return loadPetEquipment(new PetEquipment(), player.getPet().getId());
	}
	
	public Player loadCharStatus(Client client, int charId){
		Player player = null;
		if (!checkDatabase())
			return null;
		Statement stmt;		
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt
			.executeQuery("SELECT C.*,A.level AS userlevel FROM characters AS C, accounts AS A WHERE C.accountid=A.id AND C.id="
					+ charId + ";");
			if (rs.next()) {
				int raceId = rs.getInt("race");
				Race race = Race.values()[raceId];
				player = Player.createPlayer(client, race);
				player.setPlayerId(charId);				
				player.setStrength(rs.getLong("strength"));
				player.setWisdom(rs.getLong("wisdom"));
				player.setDexterity(rs.getLong("dexterity"));
				player.setConstitution(rs.getLong("constitution"));
				player.setLeadership(rs.getLong("leadership"));
				player.setLevel(rs.getInt("level"));
				player.setTotalExp(rs.getLong("totalExp"));
				player.setLevelUpExp(rs.getLong("levelUpExp"));
				player.setLime(rs.getLong("lime"));
				player.setStatusPoints(rs.getLong("statusPoints"));
				player.setPenaltyPoints(rs.getLong("penaltyPoints"));
				player.setSex(Sex.values()[rs.getInt("sex")]);
				player.setName(rs.getString("name"));
				player.setGuildId(rs.getLong("guildid"));
				player.setGuildLevel(rs.getLong("guildlvl"));
				player.setAdminState(rs.getLong("userlevel"));
				player.setHairStyle(rs.getLong("hair"));
				player.setPetId(rs.getInt("petid"));
				
				if(player.getGuildLvl() != -1)
				{
					ResultSet rsGuildName = stmt
							.executeQuery("SELECT name FROM guilds WHERE id='"+player.getGuildId()+"';");
					if(rsGuildName.next())
						player.setGuildName(rsGuildName.getString("name"));
				}
				
				return player;
			} else
				return null;
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
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
			
			int charId = player.getPlayerId();
			Statement stmt = connection.createStatement();
			
			if(charId!=-1){				
				stmt.execute("DELETE FROM characters WHERE id="+charId+";");				
			}
			
			Pet pet = player.getPet();
						
			String q = "INSERT INTO characters ("+(charId==-1?"":"id,")+"accountid,name,level,strength,wisdom,dexterity," +
												"constitution,leadership,race,sex,hair,totalExp,levelUpExp,lime," +
												"statusPoints,penaltyPoints,guildid,guildlvl,petid)" +
						 " VALUES ("+(charId==-1?"":charId+",")+
								    +client.getAccountId()+ ",'"
								    +player.getName()+ "',"
								    +player.getLevel()+ ","
								    +player.getStrength()+ ","
								    +player.getWisdom()+ ","
								    +player.getDexterity()+ ","
								    +player.getConstitution()+ ","
								    +player.getLeadership()+ ","
								    +player.getRace().value()+ ","
								    +player.getSex().ordinal()+ ","
								    +player.getHairStyle()+ ","								   
								    +player.getTotalExp()+ ","
								    +player.getLevelUpExp()+ ","
								    +player.getLime()+ ","
								    +player.getStatusPoints()+ ","
								    +player.getPenaltyPoints()+ ","
								    +player.getGuildId()+ ","
								    +player.getGuildLvl()+ ","
								    +(pet == null ? -1 : pet.getId())+");";
			
			stmt.execute(q,Statement.RETURN_GENERATED_KEYS);
			
			ResultSet res = stmt.getGeneratedKeys();
			if (res.next())
			    player.setPlayerId(res.getInt(1));			
			
			//used when player creates a new char
			if(player.getPosition().getMap() == null){ 
				//TODO: better way to handle with the player default map, after char creation
				int mapId = (int)player.getClient().getWorld().getServerSettings().getDefaultMapId();
				Map map = player.getClient().getWorld().getMap(mapId);
				Position position = new Position(7025,5225,106,map,Double.NaN);
				player.setPosition(position);
			}
			
			setSavedPosition(player);
						
		} catch (Exception e) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
			return;
		}
	}
	
	public java.util.List<Pet> loadPets() {
		
		if (!checkDatabase())
			return null;
		
		Statement stmt;		
		java.util.List<Pet> petList = new Vector<Pet>();
		
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM pet;");
			
			if (rs.next()) {
				do {
					Pet pet = loadPet(rs.getInt("id"));
					petList.add(pet);
				} while(rs.next());
			}
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return null;
		}
		return petList;
	}
	
	public Pet loadPet(int petId){
		
		if (!checkDatabase())
			return null;
		
		Statement stmt;		
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM pet WHERE id="+ petId + ";");
			
			if (rs.next()) {
					
				//Pet pet = new Pet(rs.getInt("charid"), breedTime == 0 ? true : false);
				Pet pet = new Pet();
				
				pet.setId(rs.getInt("id"));
				pet.setMaxHp(rs.getInt("hp"));
				pet.setHp(rs.getInt("hp"));
				pet.setCloseDefence(rs.getInt("closeDefence"));
				pet.setDistantDefence(rs.getInt("distantDefence"));
				pet.setCloseAttack(rs.getInt("closeAttack"));
				pet.setDistantAttack(rs.getInt("distantAttack"));
				pet.setExp(rs.getLong("exp"));
				pet.setLoyalty(rs.getInt("loyalty"));
				pet.setAmulet(itemDao.findOne((long)rs.getInt("amulet")));
				pet.setName(rs.getString("name"));
				pet.setLevel(rs.getInt("level"));
				pet.setBasket(itemDao.findOne((long)rs.getInt("basket")));
				pet.setState(rs.getInt("state"));
				pet.setBreederTimer(rs.getInt("breedtime"));
				
				return pet;
			} else
				return null;
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return null;
		}
	}
	
	public void savePet(Player player){
		
		if (!checkDatabase())
			return;
			
		try {
			Pet pet = player.getPet();
			if(pet == null)
				return;
			
			Statement stmt = connection.createStatement();
			int petId = pet.getId();
			
			Item<?> amulet = pet.getAmulet();
			Item<?> basket = pet.getBasket();
			
			if(petId!=-1){				
				stmt.execute("DELETE FROM pet WHERE id="+petId+";");				
			}
						
			String q = "INSERT INTO pet ("+(petId==-1?"":"id,")+"hp,closeDefence,distantDefence,closeAttack,distantAttack,"+
										"exp,loyalty,amulet,name,level,basket,state,breedtime)" +
						 " VALUES ("+(petId==-1?"":petId+",")+
								    +pet.getMaxHp()+ ","
								    +pet.getCloseDefence()+ ","
								    +pet.getDistantDefence()+ ","
								    +pet.getCloseAttack()+ ","
								    +pet.getDistantAttack()+ ","
								    +pet.getExp()+ ","
								    +pet.getLoyalty()+ ","
								    +(amulet == null ? -1 : amulet.getItemId())+ ",'"
								    +pet.getName()+ "',"
								    +pet.getLevel()+ ","								   
								    +(basket == null ? -1 : basket.getItemId())+ ","
								    +pet.getState() + ","
								    +pet.getBreederTimer()+");";
			
			stmt.execute(q,Statement.RETURN_GENERATED_KEYS);
			
			ResultSet res = stmt.getGeneratedKeys();
			if (res.next())
			    pet.setId(res.getInt(1));			
			
		} catch (Exception e) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
			return;
		}
	}
	
	public void deletePet(Pet pet) {
		
		if (!checkDatabase() || pet == null)
			return;
		
		Statement deleteStmt;
		Statement selectStmt;
		ResultSet rs;
		
		try {
			deleteStmt = connection.createStatement();
			selectStmt = connection.createStatement();
			
			//delete pet equipment from DB
			rs = selectStmt.executeQuery("SELECT * FROM petequipment WHERE petid = "+pet.getId()+ ";");
			if(rs.next()){
				do {
					itemDao.findOne((long)rs.getInt("itemid")).delete();
				} while(rs.next());
				deleteStmt.execute("DELETE FROM petequipment WHERE petid = "+pet.getId()+ ";");
			}
			
			//delete pet from DB
			rs = selectStmt.executeQuery("SELECT * FROM pet WHERE id = "+pet.getId()+ ";");
			if(rs.next()){
				itemDao.findOne((long)rs.getInt("amulet")).delete();
				itemDao.findOne((long)rs.getInt("basket")).delete();
				deleteStmt.execute("DELETE FROM pet WHERE id = "+pet.getId()+ ";");
			}
			
			selectStmt.execute("UPDATE characters SET petid = -1 WHERE id = "+pet.getOwner().getPlayerId()+";");
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
			
	
	public void updateCharStatus(Player player, int id, long value)
	{
		if (!checkDatabase())
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
			stmt  = connection.createStatement();
			
			stmt.execute("UPDATE characters SET "+status+" = '"+value+"' WHERE id='"+player.getPlayerId()+"';");

		} 
		catch (SQLException e) 
		{
			
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
			
		}
	}
			
	public boolean getCharNameFree(String charName) {
		if (!checkDatabase())
			return false;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt
			.executeQuery("SELECT id FROM characters WHERE name='"
					+ charName + "';");
			if (rs.next()) {
				
				return false;
			} else
				return true;
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
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
						
			stmt = connection.createStatement();			
								
			Player player = Player.createPlayer(client, race);
			ItemManager itemManager = player.getClient().getWorld().getItemManager();
			
			player.setLevel(((race == Race.HYBRIDER) ? 200 : 1));

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
			
			player.setLime((int)client.getWorld().getServerSettings().getStartLime());
			
			client.setPlayer(player);
		
			saveCharacter(player);
			int charId = player.getPlayerId();
			
			LoggerFactory.getLogger(Database.class).info(""+charId);
			
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
			if(race == Race.HYBRIDER)
			{
				Item<?> helmet = itemManager.create(1162);
				Item<?> chest = itemManager.create(1163);
				Item<?> pants = itemManager.create(1164);
				Item<?> boots = itemManager.create(1165);
				
				equipment.setItem(Slot.HELMET, helmet);
				equipment.setItem(Slot.CHEST, chest);
				equipment.setItem(Slot.PANTS, pants);
				equipment.setItem(Slot.BOOTS, boots);
			}
			else
			{
				Item<?> chest = itemManager.create(326);
				Item<?> pants = itemManager.create(343);
				
				equipment.setItem(Slot.CHEST, chest);
				equipment.setItem(Slot.PANTS, pants);
			}
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
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public Player loadChar(int slot, long accountId, Client client) {
		
		Player player=null;
		if (!checkDatabase())
			return null;
		int characterId = -1;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt
			.executeQuery("(SELECT charid FROM slots WHERE accountid ="
					+ accountId
					+ " and slot = "
					+ slot + ");");
			if (rs.next()) {
				characterId = rs.getInt("charid");
			
				player = loadCharStatus(client, characterId);
				player.setSlot(slot);
				
				LoggerFactory.getLogger(Database.class).info("Loaded: " + player.getName());
			}
			
		} catch (SQLException e) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception", e);
		}
		
		return player;
	}
	
	public Player loadInventory(Player player){
		if (!checkDatabase())
			return null;
		Statement invStmt;

		try {
			invStmt = connection.createStatement();
			
			ResultSet invTable = invStmt.executeQuery("SELECT * FROM inventory WHERE charid="+player.getPlayerId()+";");
			
			while (invTable.next()) 
			{
				Item<?> item = itemDao.findOne((long)invTable.getInt("itemid"));	
				
				if (item!=null){
					if(invTable.getInt("tab") == -1 && invTable.getInt("x") == -1 && invTable.getInt("y") == -1){
						HandPosition handPosition = new HandPosition(item);
						player.getInventory().setHoldingItem(handPosition);
					} else {
						InventoryItem inventoryItem = new InventoryItem(item,
								new InventoryPosition(invTable.getInt("x"), invTable.getInt("y"),invTable.getInt("tab")));
						player.getInventory().addInventoryItem(inventoryItem);
					}
				}
			}
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return null;
		}
		return player;
	}
		
	public void saveInventory(Player player){
		if (!checkDatabase())
			return;
	
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.execute("DELETE FROM inventory WHERE charid="+player.getPlayerId()+";");
		
			String query = "INSERT INTO inventory (charid, itemid, tab, x, y) VALUES ";
			String data = "";
			
			Iterator<InventoryItem> iter = player.getInventory().getInventoryIterator();
			HandPosition handPosition = player.getInventory().getHoldingItem();
			
			while(iter.hasNext())
			{
				InventoryItem invItem = iter.next();
				Item<?> item = invItem.getItem();
				item.save();
				
				data += "("+player.getPlayerId()+ ","+item.getItemId()+","+invItem.getPosition().getTab()+
					","+invItem.getPosition().getPosX()+ ","+invItem.getPosition().getPosY()+ ")";			
				if(iter.hasNext())
					data+= ", ";			
			}
			
			if(handPosition != null){
				data += ", (" + player.getPlayerId() + "," + handPosition.getItem().getItemId() + ",-1,-1,-1)"; 
			}
			
			if(!data.isEmpty()){
				stmt.execute(query+data);
				
			}
			
			//queue.add(saveInventory);
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public List<RoamingItem> loadRoamingItems(LocalMap map){
		
		List<RoamingItem> items = new Vector<RoamingItem>();
	
		Statement stmt;
		try {
			stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM `roaming` WHERE `mapid` = "+map.getId()+";");
			
			while (rs.next()) 
			{
				int itemid = rs.getInt("itemid");
				Item<?> item = itemDao.findOne((long)itemid);
				
				if (item==null)
					stmt.execute("DELETE FROM `roaming` WHERE itemid="+itemid);
				else{
					
					Position position = new Position(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"), map, rs.getDouble("rotation"));
					RoamingItem roamingItem = new RoamingItemImpl(item, position);
					
					items.add(roamingItem);
				}
			}
			
		} catch (SQLException e) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
			return null;
		}
	
	
		return items;		
	}
		
	public  void saveSkills(Player player) {
		
		if (!checkDatabase())
			return;
		
		Statement stmt;
		
		try {
			stmt = connection.createStatement();
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
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public  void loadSkills(Player player) {
		if (!checkDatabase())
			return;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id,level FROM skills WHERE charid="+player.getPlayerId()+";");			
			while (rs.next()) {
				int id = rs.getInt("id");
				int level = rs.getInt("level");
				
				Skill skill = Server.getInstance().getWorld().getSkillManager().getSkill(id);
				player.getSkills().put(skill, level);
				//player.getCharSkill().getSkill(id).setCurrLevel(level);
			}
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void saveEquipment(Player player){
		if (!checkDatabase())
			return;
		if (player.getEquipment()==null)return;
		
		Statement stmt;
		try {
			stmt = connection.createStatement();
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
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void savePetEquipment(Pet pet){
		if (!checkDatabase() || pet == null)
			return;

		if (pet.getEquipment() == null)
			return;
		
		Statement stmt;
		try {
			int petId = pet.getId();
			stmt = connection.createStatement();
			stmt.execute("DELETE FROM petequipment WHERE petid="+petId+";");
			
			PetEquipment eq = pet.getEquipment();
			
			String query = "INSERT INTO petequipment (petid, slot, itemid) VALUES ";
			String data = "";
			
			for(PetSlot slot: PetSlot.values())
			{
				Item<?> item = eq.getItem(slot);
				if(item != null){
					if(!data.isEmpty())
						data+= ", ";
					data+="("+petId+","+slot.value()+","+item.getItemId()+")";		
				}
			}							
			if(!data.isEmpty())
				stmt.execute(query+data);
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void loadStash(Client client){
				
		if (!checkDatabase())
			return;
		
		try {
			Statement invStmt = connection.createStatement();
			
			ResultSet rs = invStmt.executeQuery("SELECT * FROM warehouse WHERE accountid="+client.getAccountId()+";");
			client.getPlayer().getStash().clearStash();
						
			while (rs.next()) 
			{
				Item<?> item = itemDao.findOne((long)rs.getInt("itemid"));
				StashItem stashItem = new StashItem(new StashPosition(rs.getInt("pos")), item);
				client.getPlayer().getStash().addItem(stashItem);
			}
						
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception ",e1);
			return;
		}
	}
	
	public void saveStash(Client client){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = connection.createStatement();
			stmt.execute("DELETE FROM warehouse WHERE accountid="+client.getAccountId()+";");
			
			String query = "INSERT INTO warehouse (accountid, pos, itemid) VALUES ";
			String data = "";
			
			Iterator<StashItem> stashIter = client.getPlayer().getStash().itemListIterator();
			
			while(stashIter.hasNext())
			{
				StashItem stashItem = (StashItem) stashIter.next();
				
				data += "("+client.getAccountId()+ ","+stashItem.getStashPosition().getSlot()+","+stashItem.getItem().getItemId()+ ")";			
					if(stashIter.hasNext())
						data+= ", ";
				
			}
						
			if(!data.isEmpty()){
				stmt.execute(query+data);				
			}
			
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception ",e1);
			return;
		  }
	}
	
	public void loadExchange(Player player){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement invStmt = connection.createStatement();
			
			ResultSet exchangeTable = invStmt.executeQuery("SELECT * FROM exchange WHERE charid="+player.getPlayerId()+";");
						
			while (exchangeTable.next()) 
			{
				Item<?> item = itemDao.findOne((long)exchangeTable.getInt("itemid"));
				ExchangeItem exchangeItem = new ExchangeItem(item,
						exchangeTable.getInt("x"), exchangeTable.getInt("y"));
				
				player.getExchange().addItem(exchangeItem);
			}
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void saveExchange(Player player){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = connection.createStatement();
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
			LoggerFactory.getLogger(this.getClass()).warn("Exception", e);
			return;
		}
	}
	
	public void deleteGuild(int id)
	{
		if (!checkDatabase())return ;
			
		Statement stmt;
		try {
			stmt  = connection.createStatement();
			
			stmt.execute("DELETE FROM guilds WHERE id='"+id+"';");
			
			stmt.execute("Update characters SET guildid = '0', guildlvl = '0' WHERE guildid = "+id+";");
		} 
		catch (SQLException e) 
		{
			
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
			
		}
	}
	
	public void loadQuickSlot(Player player){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement invStmt = connection.createStatement();
			
			ResultSet quickSlotTable = invStmt.executeQuery("SELECT * FROM quickslot WHERE charid="+player.getPlayerId()+";");
						
			while (quickSlotTable.next()) 
			{
				Item<?> item = itemDao.findOne((long)quickSlotTable.getInt("itemid"));
				QuickSlotPosition quickSlotPosition = new QuickSlotPosition(player.getQuickSlotBar(),quickSlotTable.getInt("slot"));
				QuickSlotItem quickSlotItem = new QuickSlotItem(item,quickSlotPosition);
				player.getQuickSlotBar().addItem(quickSlotItem);
			}
			
		} catch (SQLException e) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception", e);
			return;
		}
	}
	
	public void saveQuickSlot(Player player){
		
		if (!checkDatabase())
			return;
		
		try {
			Statement stmt = connection.createStatement();
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
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
			return;
		  }
	}
	
	public boolean deleteQuickSlotItem(Item<?> item){
		if (!checkDatabase())
			return false ;
		Statement stmt;
		try {
			stmt  = connection.createStatement();		
			return stmt.execute("DELETE FROM `quickslot` WHERE `itemid`="+item.getItemId()+";");
			
		}catch (Exception e) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
		}
		return false;
	}
	
	public void deleteCharEquipment(int charId) {
		if (!checkDatabase())
			return;
		Statement stmt;
		Statement itemStmt;
		
		try {
			stmt = connection.createStatement();
			itemStmt = connection.createStatement();
			
			ResultSet rs = itemStmt.executeQuery("SELECT * FROM equipment WHERE charid = "+charId+ ";");
			if(rs.next()){
				do {
					itemDao.findOne((long)rs.getInt("itemid")).delete();
				} while(rs.next());
			} else return;
			
			stmt
			.execute("DELETE FROM equipment WHERE charid = "+charId+ ";");
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharExchange(int charId) {
		if (!checkDatabase())
			return;
		Statement stmt;
		Statement itemStmt;
		
		try {
			stmt = connection.createStatement();
			itemStmt = connection.createStatement();
			
			ResultSet rs = itemStmt.executeQuery("SELECT * FROM exchange WHERE charid = "+charId+ ";");
			if(rs.next()){
				do {
					itemDao.findOne((long)rs.getInt("itemid")).delete();
				} while(rs.next());
			} else return;
			
			stmt
			.execute("DELETE FROM exchange WHERE charid = "+charId+ ";");
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharInventory(int charId) {
		if (!checkDatabase())
			return;
		Statement stmt;
		Statement itemStmt;
		
		try {
			stmt = connection.createStatement();
			itemStmt = connection.createStatement();
			
			ResultSet rs = itemStmt.executeQuery("SELECT * FROM inventory WHERE charid = "+charId+ ";");
			if(rs.next()){
				do {
					itemDao.findOne((long)rs.getInt("itemid")).delete();

				} while(rs.next());
			} else return;
			
			stmt
			.execute("DELETE FROM inventory WHERE charid = "+charId+ ";");
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
		
	public void deleteCharQuickSlot(int charId) {
		if (!checkDatabase())
			return;
		Statement stmt;
		Statement itemStmt;
		
		try {
			stmt = connection.createStatement();
			itemStmt = connection.createStatement();
			
			ResultSet rs = itemStmt.executeQuery("SELECT * FROM quickslot WHERE charid = "+charId+ ";");
			if(rs.next()){
				do {
					itemDao.findOne((long)rs.getInt("itemid")).delete();
				} while(rs.next());
			} else return;
			
			stmt
			.execute("DELETE FROM quickslot WHERE charid = "+charId+ ";");
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharSkills(int charId) {
		if (!checkDatabase())
			return;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt
			.execute("DELETE FROM skills WHERE charid = "+charId+ ";");
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharacter(int charId) {
		if (!checkDatabase())
			return;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt
			.execute("DELETE FROM characters WHERE id = "+charId+ ";");
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public void deleteCharSlot(int charId) {
		if (!checkDatabase())
			return;
		Statement stmt;
		
		try {
			stmt = connection.createStatement();
			
			stmt.execute("DELETE FROM slots WHERE charid = "+ charId + ";");
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return;
		}
	}
	
	public int getCharId(int slotNumber, long accountId) {
		if (!checkDatabase())
			return -1;
		
		Statement stmt;
		int charId = -1;
		
		try {
			stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT charid FROM slots WHERE slot = "
					+ slotNumber
					+ " and accountid = "
					+ accountId
					+ ";");
			
			if(rs.next())
				charId = rs.getInt("charid");
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return -1;
		}
		
		return charId;
	}
	
	public String getCharName(int charId) {
		if (!checkDatabase())
			return "";
		
		Statement stmt;
		String charName = "";
		
		try {
			stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM characters WHERE id = "+ charId	+ ";");
			
			if(rs.next())
				charName = rs.getString("name");
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return "";
		}
		
		return charName;
	}
	
	public int addGuild (String name)
	{
		if (!checkDatabase())
			return 0;
		
		Statement stmt;
		int guildId = 0;
		
		try {
			stmt = connection.createStatement();
			
			boolean rs = stmt.execute("INSERT INTO guilds SET Name = '"+ name+ "';");
			
			ResultSet rsId = stmt.executeQuery("SELECT id FROM guilds WHERE name = '"+name+"'");
			
			if(rsId.next())
				guildId = Integer.parseInt(rsId.getString("id"));
			
		} catch (SQLException e1) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e1);
			return 0;
		}
		return guildId;
	}
}
