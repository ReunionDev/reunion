package com.googlecode.reunion.jreunion.game;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.client.ClientDisconnectEvent;
import com.googlecode.reunion.jreunion.events.client.ClientEvent.ClientFilter;
import com.googlecode.reunion.jreunion.events.map.ItemPickupEvent;
import com.googlecode.reunion.jreunion.events.map.PlayerLogoutEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkAcceptEvent;
import com.googlecode.reunion.jreunion.events.session.SessionEvent;
import com.googlecode.reunion.jreunion.game.Equipment.Slot;
import com.googlecode.reunion.jreunion.game.Player.Status;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Client.State;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Session;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Tools;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class Player extends LivingObject implements SkillTarget, EventListener {

	private int defense = 0;

	private int minDmg;
	
	java.util.Map<Skill,Integer> skills = new HashMap<Skill,Integer> ();

	private int maxDmg;

	private int totalExp;

	private int lvlUpExp;

	private int lime; // Gold
	
	private int slot;

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}
	
	public static enum Sex{
		MALE, //0
		FEMALE	//1
	}
	
	public static enum Race{
		BULKAN, //0

		KAILIPTON, //1

		AIDIA, //2

		HUMAN, //3

		HYBRIDER, //4
		
		RACE_PET, //5
		
		UNDEFINED; //6
	}

	private boolean isInCombat; // 0 - Peace Mode; 1 - Attack Mode

	private int strength;

	private int wisdom;

	private int dexterity;

	private int constitution;

	private int leadership;

	private Sex sex; // 0 - Male; 1 - Female

	private int speed;

	private int statusPoints;

	private Inventory inventory;

	private String name;

	private int penaltyPoints;

	private int adminState; // 0 - normal user; 255 - SuperGM

	private Session playerSession;

	private int hairStyle;

	private Equipment equipment;

	private List<Integer> attackQueue = new Vector<Integer>();

	private QuickSlot quickSlot;

	private Quest quest;

	private Stash stash;

	private Exchange exchange;

	private int guildId;

	private int guildLevel;
	
	private Client client;
	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	
	private static Integer sessionRadius;

	public Player(Client client) {
		super();
		this.setClient(client);
		client.setPlayer(this);
		inventory = new Inventory(this);
		equipment = new Equipment(this);
		quickSlot = new QuickSlot(this);
		stash = new Stash(this);
		exchange = new Exchange(this);
		
		client.addEventListener(ClientDisconnectEvent.class, this, new ClientFilter(client));

	}
	
	public void addAttack(int attack) {
		if (attackQueue.size() >= 5) {
			attackQueue.remove(0);
		}
		attackQueue.add(attack);
	}
	
	public void clearAttackQueue() {
		for (int i = 0; i < attackQueue.size(); i++) {
			attackQueue.remove(i);
		}
	}

	/****** Manages the Item Drop ******/
	public void dropItem(int uniqueId) {
	
		Item item = getInventory().getItemSelected().getItem();

		if (item == null) {
			return;
		}
		
		LocalMap map = getPosition().getMap();
		map.getWorld().getCommand().dropItem(getPosition(), item);
		
		getInventory().setItemSelected(null);
	
	}
	

	public int getAdminState() {
		return adminState;
	}
	
	@Override
	public synchronized void setHp(int hp){
		int before = this.getHp();
		super.setHp(hp);	
		if(before!=this.getHp())
			this.sendStatus(Status.HP);
	}
	
	public void sendStatus(Status status) {
		if(client.getState()==Client.State.INGAME){
			int min=0,max=0;
			switch (status) {
				case HP: //0
					min = getHp();
					max = getMaxHp();				
					break;
				case MANA: //1
					min = getMana();
					max = getMaxMana();
					break;
				case STAMINA: //2
					min = getStamina();
					max = getMaxStamina();
					break;
				case ELECTRICITY: //3
					min = getElectricity();
					max = getMaxElectricity();
					break;
				case LEVEL: //4
					min = getLevel();
					break;
				case LIME: //10
					min = getLime();
					break;
				case TOTALEXP:
					min = getTotalExp();
					break;
				case LEVELUPEXP: //12
					min = getLevelUpExp();
					break;
				case STATUSPOINTS: //13
					min = getStatusPoints();
					break;
				case STRENGTH: //14
					min = getStrength();
					break;
				case WISDOM: //15
					min = getWisdom();
					break;
				case DEXTERITY: //16
					min = getDexterity();
					break;
				case CONSTITUTION: //17
					min = getConstitution();
					break;
				case LEADERSHIP: //18
					min = getLeadership();
					break;
				case PENALTYPOINTS: //19
					min = getPenaltyPoints();
					break;
				default:
					throw new RuntimeException(status+" not implemented yet");
			
			}			
			client.sendPacket(Type.STATUS, status.value(), min, max);
		}
	}

	public abstract int getMaxElectricity();
	

	public abstract int getMaxMana();
	

	public abstract int getMaxStamina();
	

	public synchronized void setStamina(int stamina){
		int before = this.stamina;
		this.stamina = Tools.between(stamina, 0, getMaxStamina());
		if(before!=this.stamina)
			this.sendStatus(Status.STAMINA);
	}
	
	abstract int getBaseDamage();
	

	public Iterator<Integer> getAttackQueueIterator() {
		return attackQueue.iterator();
	}

	public int getBestAttack() {
		int bestAttack = 0;
		Iterator<Integer> iter = getAttackQueueIterator();

		while (iter.hasNext()) {
			int count = 1;
			int attack = iter.next();

			if (bestAttack < attack) {
				bestAttack = attack;
			}
			count++;
		}
		return bestAttack;
	}
	
	public boolean isInCombat() {
		return isInCombat;
	}

	public int getConstitution() {
		return constitution;
	}

	public int getDef() {
		return defense;
	}

	public int getDexterity() {
		return dexterity;
	}

	/*** Return the distance between the player and the living object ***/
	public int getDistance(LivingObject livingObject) {
		double xcomp = Math.pow(livingObject.getPosition().getX() - getPosition().getX(), 2);
		double ycomp = Math.pow(livingObject.getPosition().getY() - getPosition().getY(), 2);
		double distance = Math.sqrt(xcomp + ycomp);
		return (int) distance;
	}

	public Equipment getEquipment() {
		return equipment;
	}
	
	public Exchange getExchange() {
		return exchange;
	}

	public int getGuildId() {
		return guildId;
	}

	public int getGuildLvl() {
		return guildLevel;
	}

	public int getHairStyle() {
		return hairStyle;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public int getLeadership() {
		return leadership;
	}

	public int getLime() {
		return lime;
	}

	public int getLevelUpExp() {
		return lvlUpExp;
	}

	public int getMaxDmg() {
		return maxDmg;
	}

	public int getMinDmg() {
		return minDmg;
	}

	public String getName() {
		return name;
	}

	public int getPenaltyPoints() {
		return penaltyPoints;
	}

	public Quest getQuest() {
		return quest;
	}

	public QuickSlot getQuickSlot() {
		return quickSlot;
	}
	
	private int mana;

	private int electricity;

	private int stamina;

	

	public int getElectricity() {
		return electricity;
	}	
	
	public int getStamina() {
		return stamina;
	}
	
	
	public int getMana() {
		return mana;
	}

	
	public synchronized void setMana(int mana) {
		int before = this.mana;
		this.mana = Tools.between(mana, 0, getMaxMana());
		if(before!=this.mana)
			sendStatus(Status.MANA);
	}


	public synchronized void setElectricity(int electricity) {
		int before = this.electricity;
		this.electricity = Tools.between(electricity,0,getMaxElectricity());
		if(this.electricity!=before)
			sendStatus(Status.ELECTRICITY);
	}	

	public Race getRace() {
		if(this instanceof BulkanPlayer)
			return Race.BULKAN;
		if(this instanceof AidiaPlayer)
			return Race.AIDIA;
		if(this instanceof KailiptonPlayer)
			return Race.KAILIPTON;
		if(this instanceof HumanPlayer)
			return Race.HUMAN;
		if(this instanceof HybriderPlayer)
			return Race.HYBRIDER;		
		throw new RuntimeException("Unknown race: "+this);
	}

	public void spawn() {
		//TODO: fix respawn
		/*
		int defaultSpawnId = Integer.parseInt(Reference.getInstance().getMapReference().getItemById(getPosition().getMap().getId()).getMemberValue("DefaultSpawnId"));
		ParsedItem defaultSpawn = getPosition().getMap().getPlayerSpawnReference().getItemById(defaultSpawnId);
				
		Parser playerSpawns = getPosition().getMap().getPlayerSpawnReference();
		Iterator<ParsedItem> iter = playerSpawns.getItemListIterator();

		while (iter.hasNext()) {
			ParsedItem item = iter.next();

			if (Integer.parseInt(item.getMemberValue("Id"))!=defaultSpawnId) {
				Rectangle rectangle = new Rectangle(Integer.parseInt(item
						.getMemberValue("TargetX")), Integer.parseInt(item
						.getMemberValue("TargetY")), Integer.parseInt(item
						.getMemberValue("TargetWidth")), Integer.parseInt(item
						.getMemberValue("TargetHeight")));

				if (rectangle.contains(getPosition().getX(), getPosition().getY())) {
					Server.getInstance()
							.getWorld()
							.getCommand()
							.GoToPos(
									this,
									Integer.parseInt(item
											.getMemberValue("X"))
											+ (int) (Integer.parseInt(item
													.getMemberValue("Width")) * Math
													.random()),
									Integer.parseInt(item
											.getMemberValue("Y"))
											+ (int) (Integer.parseInt(item
													.getMemberValue("Height")) * Math
													.random()));
					return;
				}
			}
		}
		
		int x = Integer.parseInt(defaultSpawn.getMemberValue("X"));
		int y = Integer.parseInt(defaultSpawn.getMemberValue("Y"));
		int width = Integer.parseInt(defaultSpawn.getMemberValue("Width"));
		int height = Integer.parseInt(defaultSpawn.getMemberValue("Height"));
		
		Random rand = new Random(System.currentTimeMillis());
		int spawnX = x+(width>0?rand.nextInt(width):0);
		int spawnY = y+(height>0?rand.nextInt(height):0);
		
		Server.getInstance()
		.getWorld()
		.getCommand()
		.GoToPos(this,spawnX,spawnY);
		*/
	}

	/**
	 * @return Returns the playerSession.
	 * @uml.property name="playerSession"
	 */
	public Session getSession() {
		return playerSession;
	}

	public Sex getSex() {
		return sex;
	}

	public int getSpeed() {
		return speed;
	}

	public Stash getStash() {
		return stash;
	}

	public int getStatusPoints() {
		return statusPoints;
	}

	public int getStrength() {
		return strength;
	}

	public int getTotalExp() {
		return totalExp;
	}

	public int getWisdom() {
		return wisdom;
	}

	public static Player createPlayer(Client client, Race race){
		
		if (race == Race.BULKAN) return new BulkanPlayer(client);
		else if (race == Race.KAILIPTON) return new KailiptonPlayer(client);
		else if (race == Race.AIDIA) return  new AidiaPlayer(client);
		else if (race == Race.HUMAN) return new HumanPlayer(client);
		else if (race == Race.HYBRIDER)	return new HybriderPlayer(client);
		
		throw new RuntimeException("Invalid race: "+race);
		
	}
	/*** Manages the Items add/Remove from Trade Box ***/
	public void itemExchange(int posX, int posY) {

		Client client = getClient();

		if (client == null) {
			return;
		}

		if (getInventory().getItemSelected() == null) {
			ExchangeItem item = getExchange().getItem(posX, posY);

			if (item == null) {
				return;
			}

			InventoryItem invItem = new InventoryItem(item.getItem(), 0, 0,
					0);

			getInventory().setItemSelected(invItem);
			getExchange().removeItem(item);
		} else {
			Item item = getInventory().getItemSelected().getItem();
			ExchangeItem newExchangeItem = new ExchangeItem(item, posX,
					posY);
			ExchangeItem oldExchangeItem = null;
			int x = 0, y = 0;

			while (oldExchangeItem == null && x < item.getSizeX()) {
				while (oldExchangeItem == null && y < item.getSizeY()) {
					oldExchangeItem = getExchange().getItem(posX + x, posY + y);
					y++;
				}
				y = 0;
				x++;
			}

			if (oldExchangeItem == null) {
				getInventory().setItemSelected(null);
			} else {
				InventoryItem invItem = new InventoryItem(
						oldExchangeItem.getItem(), 0, 0, 0);
				getInventory().setItemSelected(invItem);
				getExchange().removeItem(oldExchangeItem);
			}
			getExchange().addItem(newExchangeItem);
		}
	}

	/****** Load Items at Trade Box ******/
	public void loadExchange() {
		Client client = getClient();

		if (client == null) {
			return;
		}

		Iterator<ExchangeItem> exchangeIter = getExchange()
				.itemListIterator();
		while (exchangeIter.hasNext()) {
			ExchangeItem exchangeItem = exchangeIter.next();
			
			client.sendPacket(Type.INVEN, exchangeItem);

			// inven [Tab] [UniqueId] [Type] [PosX] [PosY] [Gems] [Special]
		}
	}

	@Override
	public void loadFromReference(int id) {
		ParsedItem exp = Reference.getInstance().getExpReference()
				.getItemById(id);

		if (exp == null) {
			// cant find Item in the reference continue to load defaults:
			setLevelUpExp(1000);
		} else {

			if (exp.checkMembers(new String[] { "Exp" })) {
				// use member from file
				setLevelUpExp(Integer.parseInt(exp.getMemberValue("Exp")));
			} else {
				// use default
				setLevelUpExp(1000);
			}
		}
	}

	/****** load Inventory Items ******/
	public void loadInventory() {
		Client client = getClient();
	
		Iterator<InventoryItem> invIter = getInventory()
				.getInventoryIterator();
		while (invIter.hasNext()) {
			InventoryItem invItem = invIter.next();
			
			client.sendPacket(Type.INVEN, invItem);
			// inven [Tab] [UniqueId] [Type] [PosX] [PosY] [Gems] [Special]

		}
	}

	/****** load Quick Slot Items ******/
	public void loadQuickSlot() {
		Client client = getClient();

		if (client == null) {
			return;
		}

		Iterator<QuickSlotItem> quickSlot = getQuickSlot()
				.getQuickSlotIterator();
		while (quickSlot.hasNext()) {
			QuickSlotItem qsItem = quickSlot.next();
			Item item = qsItem.getItem();
			if(item!=null){
				String packetData = "quick " + qsItem.getSlot() + " "
						+ item.getId() + " "
						+ item.getType() + " "
						+ item.getGemNumber() + " "
						+ item.getExtraStats() + "\n";
				
						client.sendData( packetData);
			}
		}
	}

	/****** Manages the char Logout ******/
	public synchronized void logout() {


		Logger.getLogger(Player.class).info("Player " + getName() + " logging out...\n");

		DatabaseUtils.getInstance().saveSkills(this);
		DatabaseUtils.getInstance().saveInventory(this);
		DatabaseUtils.getInstance().saveCharacter(this);
		DatabaseUtils.getInstance().saveEquipment(this);
		DatabaseUtils.getInstance().saveStash(client);
		DatabaseUtils.getInstance().saveExchange(this);
		DatabaseUtils.getInstance().saveQuickSlot(this);
		
		getPosition().getMap().fireEvent(PlayerLogoutEvent.class, this);
		
		
	}

	public void loseStamina(int ammount) {	
		setStamina(getStamina() - ammount);		
	}

	public abstract void meleeAttack(LivingObject livingObject);

	/****** Manages the Pick command ******/
	// When you pick up an item, or buy something from merchant
	public void pickItem(Item item) {
		Client client = getClient();

		//TODO: Fix item pickup
		getInventory().addItem(item);
		
		getInventory().PrintInventoryMap(0);
		InventoryItem invItem = getInventory().getItem(item);
		// DatabaseUtils.getInstance().saveInventory(client.getPlayer()Object);

		if (invItem == null) {
			invItem = new InventoryItem(item, 0, 0, 0);
			getInventory().setItemSelected(invItem);
				
		}
		client.sendPacket(Type.PICK, invItem);
		
		// S> pick [UniqueID] [Type] [Tab] [PosX] [PosY] [GemNumber] [Special]
	}

	/****** Manages the Pickup command ******/
	public void pickupItem(RoamingItem roamingItem) {
		Client client = getClient();
		
		Item item = roamingItem.getItem();
		
		Player owner = roamingItem.getOwner();
		if(roamingItem.getOwner()!=null&&roamingItem.getOwner()!=this){
			
			getClient().sendPacket(Type.SAY, "This item belongs to "+owner.getName());
			return;
			
		}		
		
		client.sendPacket(Type.PICKUP, this);
		this.getInterested().sendPacket(Type.PICKUP, this);
				
		getPosition().getMap().fireEvent(ItemPickupEvent.class, this, roamingItem);
		// S> pickup [CharID]

		//pickItem(roamingItem.getItem());
	}

	public void place(Position position, int unknown, boolean running) {
		
		setIsRunning(running);
		
		synchronized(this){
			setPosition(position);
			setTargetPosition(position.clone());			
		}
		
		this.getInterested().sendPacket(Type.PLACE, this, unknown);
		
	}

	/****** revive player when he dies ******/
	public void revive() {

		setHp(getMaxHp());
		spawn();

	}
	
	public int getSkillLevel(Skill skill){
		
		return skills.get(skill);
		
	}

	public java.util.Map<Skill, Integer> getSkills() {
		return skills;
	}

	public void say(String text) {
	
		getPosition().getMap().getWorld().getCommand().playerSay(this, text);
	}

	public void setAdminState(int adminState) {
		this.adminState = adminState;
	}

	public void setIsInCombat(boolean isInCombat) {
		this.isInCombat = isInCombat;
		getInterested().sendPacket(Type.COMBAT, this);
	}

	public void setConstitution(int cons) {
		this.constitution = cons;
		sendStatus(Status.CONSTITUTION);
		sendStatus(Status.HP);
	}

	public void setDefense(int def) {
		this.defense = def;
	}

	public void setDexterity(int dex) {
		this.dexterity = dex;
		sendStatus(Status.DEXTERITY);
		sendStatus(Status.ELECTRICITY);
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public void setGuildId(int guildId) {
		this.guildId = guildId;
	}
	
	@Override
	public void setLevel(int level) {
		super.setLevel(level);
		loadFromReference(level);
		if(client.getState()==State.INGAME){

			
			sendStatus(Status.LEVEL);
			client.sendPacket(Type.LEVELUP, this);		
			getInterested().sendPacket(Type.LEVELUP, this);
			
			setHp(this.getMaxHp());
			setMana(this.getMaxHp());
			setElectricity(this.getMaxElectricity());
			setStamina(this.getMaxStamina());
			
			
		}
	}

	public void setGuildLevel(int guildLevel) {
		this.guildLevel = guildLevel;
	}

	public void setHairStyle(int hairStyle) {
		this.hairStyle = hairStyle;
	}

	public void setLeadership(int lead) {
		this.leadership = lead;
		sendStatus(Status.LEADERSHIP);
		sendStatus(Status.HP);
		sendStatus(Status.MANA);
		sendStatus(Status.STAMINA);
		sendStatus(Status.ELECTRICITY);
	}

	public void setLime(int lime) {
		this.lime = lime;
		this.sendStatus(Status.LIME);
	}

	public void setLevelUpExp(int lvlUpExp) {
		synchronized(this) {
			
			if(lvlUpExp<0){
				this.setLevel(getLevel()+1);
				this.setStatusPoints(this.getStatusPoints()+3);
				this.setLevelUpExp(this.getLevelUpExp()-Math.abs(lvlUpExp));
			}
			else{
				this.lvlUpExp = lvlUpExp;
				sendStatus(Status.LEVELUPEXP);
			}
		}
	}

	public void setMaxDmg(int maxDmg) {
		this.maxDmg = maxDmg;
	}

	public void setMinDmg(int minDmg) {
		this.minDmg = minDmg;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPenaltyPoints(int penaltyPoints) {
		this.penaltyPoints = penaltyPoints;
		sendStatus(Status.PENALTYPOINTS);
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
	}

	/**
	 * @param playerSession
	 *            The playerSession to set.
	 * @uml.property name="playerSession"
	 */
	public void setSession(Session session) {
		playerSession = session;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setStatusPoints(int statusPoints) {
		this.statusPoints = statusPoints;
		sendStatus(Status.STATUSPOINTS);
	}

	public void setStrength(int str) {
		this.strength = str;
		sendStatus(Status.STRENGTH);
		sendStatus(Status.HP);
		sendStatus(Status.STAMINA);
	}

	public void setTotalExp(int totalExp) {
		this.totalExp = totalExp;
		sendStatus(Status.TOTALEXP);
	}

	public void setWisdom(int wisdom) {
		this.wisdom = wisdom;
		sendStatus(Status.WISDOM);
		sendStatus(Status.MANA);
	}

	public void social(int emotionId) {

		getInterested().sendPacket(Type.SOCIAL, this, emotionId);
		
	}

	public void stop(Position position) {

		synchronized(this) {
			setPosition(position);	
			setTargetPosition(position.clone());		
		}
		
		this.getInterested().sendPacket(Type.S_CHAR, this);
	
	}

	public void tell(Player targetPlayer, String text) {

		Client client = this.getClient();

		if (targetPlayer == null) {
			client.sendPacket(Type.MSG,"Player not online");
			return;
		}

		String packetData = "say 1 " + getName() + " (PM) " + text + " 0\n";
		
		client.sendData(packetData);
	}
	
	
	public static enum Status{
		
		HP(0),
		MANA(1),
		STAMINA(2),
		ELECTRICITY(3),
		LEVEL(4),
		LIME(10),
		TOTALEXP(11),
		LEVELUPEXP(12),
		STATUSPOINTS(13), 
		STRENGTH(14),
		WISDOM(15),
		DEXTERITY(16),
		CONSTITUTION(17),
		LEADERSHIP(18),
		PENALTYPOINTS(19),

		
		//TODO: Finish this
		;
		
		int value;
		Status(int value){
			this.value = value;
		}
		
		public int value(){
			return value;
		}
		
		public static Status byValue(int statusId){			
			for(Status status:Status.values())
			{
				if(status.value()==statusId){					
					return status;
				}
			}
			return null;
		}
		
	}
	
	/****** Handles all the Status Updates ******/
	public void updateStatus(int id, int curr, int max) {
		Client client = this.getClient();

		if (client == null) {
			return;
		}

		switch (id) {


		case 4: { // Player Level Status
			setLevel(getLevel() + curr);
			client.sendPacket(Type.STATUS, id, getLevel(), max);
			
			DatabaseUtils.getInstance()
					.updateCharStatus(this, id, getLevel());
			
			client.sendPacket(Type.LEVELUP, this);
			
			getInterested().sendPacket(Type.LEVELUP, this);

			break;
		}
		case 10: { // Player Lime Status
			setLime(getLime() + curr);
			client.sendPacket(Type.STATUS, id, getLime(), max);
			
			DatabaseUtils.getInstance().updateCharStatus(this, id, getLime());
			break;
		}
		case 11: { // Player Total Exp Status
			setTotalExp(getTotalExp() + curr);
			client.sendPacket(Type.STATUS, id, getTotalExp(), max);
	
			DatabaseUtils.getInstance().updateCharStatus(this, id,
					getTotalExp());
			break;
		}
		case 12: { // Player Next Level Up Exp Status
			if (curr <= 0) {
				updateStatus(4, 1, 0);
				updateStatus(13, 3, 0);
				updateStatus(0, getMaxHp(), getMaxHp());
				updateStatus(1, getMaxMana(), getMaxMana());
				updateStatus(2, getMaxStamina(), getMaxStamina());
				updateStatus(3, getMaxElectricity(), getMaxElectricity());

				loadFromReference(getLevel());
				client.sendPacket(Type.STATUS, id, getLevelUpExp(), max);
						
				DatabaseUtils.getInstance().updateCharStatus(this, id,
						getLevelUpExp());
			} else {
				setLevelUpExp(curr);
				
				client.sendPacket(Type.STATUS, id, getLevelUpExp(), max);
						
				DatabaseUtils.getInstance().updateCharStatus(this, id,
						getLevelUpExp());
			}
			break;
		}
		case 13: { // Player Distribution Status Points
			setStatusPoints(getStatusPoints() + curr);
			
			
			
			client.sendPacket(Type.STATUS, id, getStatusPoints(), max);
			DatabaseUtils.getInstance().updateCharStatus(this, id,
					getStatusPoints());
			break;
		}
		case 14: { // Player Strenght Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setStrength(getStrength() + curr);			
			client.sendPacket(Type.STATUS, id, getStrength(), max);
					
			DatabaseUtils.getInstance().updateCharStatus(this, id, getStrength());

			updateStatus(0, getHp(), getMaxHp() + (getStrength() / 50) + 1);
			updateStatus(2, getStamina(), getMaxStamina() + (getStrength() / 60) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 15: { // Player Wisdom Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setWisdom(getWisdom() + curr);
			
			client.sendPacket(Type.STATUS, id, getWisdom(), max);
					
			DatabaseUtils.getInstance().updateCharStatus(this, id, getWisdom());

			updateStatus(1, getMana(), getMaxMana() + (getWisdom() / 50) + 2);
			updateStatus(3, getElectricity(), getMaxElectricity() + (getWisdom() / 50) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 16: { // Player Dex Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setDexterity(getDexterity() + curr);

			client.sendPacket(Type.STATUS, id, getDexterity(), max);
					
			DatabaseUtils.getInstance().updateCharStatus(this, id, getDexterity());

			updateStatus(1, getMana(), getMaxMana() + (getDexterity() / 50) + 1);
			updateStatus(3, getElectricity(), getMaxElectricity() + (getDexterity() / 50) + 2);
			updateStatus(13, -1, 0);
			break;
		}
		case 17: { // Player Strain Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setConstitution(getConstitution() + curr);
			
			client.sendPacket(Type.STATUS, id, getConstitution(), max);
					
			DatabaseUtils.getInstance().updateCharStatus(this, id, getConstitution());

			updateStatus(0, getHp(), getMaxHp() + (getConstitution() / 50) + 2);
			updateStatus(2, getStamina(), getMaxStamina() + (getConstitution() / 50) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 18: { // Player Charisma Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setLeadership(getLeadership() + curr);
						
			client.sendPacket(Type.STATUS, id, getLeadership(), max);
					
			DatabaseUtils.getInstance().updateCharStatus(this, id, getLeadership());

			if (getLeadership() % 2 == 0) {
				updateStatus(0, getHp(), getMaxHp() + 1);
				updateStatus(1, getMana(), getMaxMana() + 1);
				updateStatus(2, getStamina(), getMaxStamina() + 1);
				updateStatus(3, getElectricity(), getMaxElectricity() + 1);
			}
			updateStatus(13, -1, 0);
			break;
		}
		case 19: { // Player Penalty Points Status ([inGame=packet] -> 100=10;
					// 1000=100; 10000=1000)
	
			client.sendPacket(Type.STATUS, id, curr, max);
					
			DatabaseUtils.getInstance().updateCharStatus(this, id,
					getPenaltyPoints());
			break;
		}
		}
		// S> status [StatusConstant] [Current] [Max]

		// DatabaseUtils.getInstance().saveCharStatus(this);
	}

	public void wearSlot(Slot slot) {
	
		InventoryItem invItem = getInventory().getItemSelected();

		if (invItem == null) {
			if (getEquipment().getItem(slot) instanceof Weapon) {
				setMinDmg(1);
				setMaxDmg(2);
			}

			getInventory()
					.setItemSelected(
							new InventoryItem(getEquipment().getItem(slot),
									0, 0, 0));
			getEquipment().setItem(slot, null);
			getInterested().sendPacket(Type.CHAR_REMOVE, this, slot);
			
		} else {
			if (getEquipment().getItem(slot) == null) {
				Item item = invItem.getItem();
				
				getInterested().sendPacket(Type.CHAR_WEAR, this, slot, item);
				
				getEquipment().setItem(slot, item);
				getInventory().setItemSelected(null);
				if (getEquipment().getItem(slot) instanceof Weapon) {
					Weapon weapon = (Weapon) getEquipment().getItem(slot);
					setMinDmg(weapon.getMinDamage());
					setMaxDmg(weapon.getMaxDamage());
				}
			} else {
				Item currentItem = getEquipment().getItem(slot);
				getInterested().sendPacket(Type.CHAR_REMOVE, this, slot);
				getEquipment().setItem(slot, invItem.getItem());
				getInventory().setItemSelected(
						new InventoryItem(currentItem, 0, 0, 0));
				if (getEquipment().getItem(slot) instanceof Weapon) {
					Weapon weapon = (Weapon) getEquipment().getItem(slot);
					setMinDmg(weapon.getMinDamage());
					setMaxDmg(weapon.getMaxDamage());
				}
				Item item = getEquipment().getItem(slot);

				getInterested().sendPacket(Type.CHAR_WEAR, this, slot, item);
				
			}

		}
		// DatabaseUtils.getInstance().saveEquipment(this);

	}
	/**
	 * @param sessionRadius the sessionRadius to set
	 */
	public void setSessionRadius(int sessionRadius) {
		Player.sessionRadius = sessionRadius;
	}

	/**
	 * @return the sessionRadius
	 */
	public int getSessionRadius() {
		if(Player.sessionRadius==null){
			setSessionRadius(Integer.parseInt(Reference.getInstance().getServerReference().getItem("Server").getMemberValue("SessionRadius")));
		}
		return Player.sessionRadius;
	}
	
	@Override
	public void enter(Session session){
		session.getOwner().getClient().sendPacket(Type.IN_CHAR, this, false);
	}

	@Override
	public void exit(Session session){
		session.getOwner().getClient().sendPacket(Type.OUT, this);
	}
	
	public void handleEvent(Event event){
		Server.getInstance().getNetwork().addEventListener(NetworkAcceptEvent.class, this);
		if(event instanceof ClientDisconnectEvent){
			ClientDisconnectEvent clientDisconnectEvent = (ClientDisconnectEvent) event;
			Logger.getLogger(Player.class).debug(clientDisconnectEvent.getSource());
			logout();
		}
		if(event instanceof SessionEvent){
			SessionEvent sessionEvent = (SessionEvent) event;
		}
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");

		buffer.append("id: ");
		buffer.append(getId());
		buffer.append(", ");
		
		buffer.append("name: ");
		buffer.append(getName());
		buffer.append(", ");
		
		buffer.append("race: ");
		buffer.append(getRace());
		buffer.append(", ");
		
		buffer.append("level: ");
		buffer.append(getLevel());		
				
		buffer.append("}");
		return buffer.toString();
	}
	
	public Skill getSkill(int id){
		Skill skill = getPosition().getMap().getWorld().getSkillManager().getSkill(id);		
		return skills.containsKey(skill)?skill:null;
		
	}

	public void setSkillLevel(Skill skill, int level) {
			
		skills.put(skill, level);
		
	}

	public void addStatus(Status status) {
		synchronized(this){
			int statusPoints = getStatusPoints();
			if(getStatusPoints()>0){
							
				switch(status){
					case STRENGTH: //14
						setStrength(getStrength()+1);
						break;
					case WISDOM: //15
						setWisdom(getWisdom()+1);
						break;
					case DEXTERITY: //16
						setDexterity(getDexterity()+1);
						break;
					case CONSTITUTION: //17
						setConstitution(getConstitution()+1);
						break;
					case LEADERSHIP: //18
						setLeadership(getLeadership()+1);
						break;
					default:
						throw new RuntimeException("Invalid Status: "+status);
						
				}
				setStatusPoints(statusPoints-1);
			}
		}
	}
}
