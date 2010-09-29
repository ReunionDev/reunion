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
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Session;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class Player extends LivingObject implements SkillTarget, EventListener {

	private int def = 0;

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
		
		RACE_PET; //5
		
	}


	private boolean combatMode; // 0 - Peace Mode; 1 - Attack Mode

	private int str;

	private int wis;

	private int dex;

	private int cons;

	private int lead;

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

	private int guildLvl;
	
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
		inventory = new Inventory();
		equipment = new Equipment();
		quickSlot = new QuickSlot();
		stash = new Stash();
		exchange = new Exchange();
		
		client.addEventListener(ClientDisconnectEvent.class, this, new ClientFilter(client));
		// setPlayerMinDmg(325);
		// setPlayerMaxDmg(370);
	}

	public void addAttack(int attack) {
		if (attackQueue.size() >= 5) {
			attackQueue.remove(0);
		}
		attackQueue.add(attack);
	}


	public void charCombat(boolean combat) {

		setCombatMode(combat);
		getInterested().sendPacket(Type.COMBAT, this);
		
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
	/*
	public CharSkill getCharSkill() {
		return charSkill;
	}
	*/
	public boolean getCombatMode() {
		return combatMode;
	}

	public int getConstitution() {
		return cons;
	}

	public int getDef() {
		return def;
	}

	public int getDexterity() {
		return dex;
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
	
	public void levelUpSkill(Skill skill) {
		
		if(!skills.containsKey(skill))
			return; //cheater?
		synchronized(this){
		
			int currentSkillLevel = skills.get(skill);
			
			if(currentSkillLevel<skill.getMaxLevel()){
				
				skills.put(skill, ++currentSkillLevel);
				
				 getClient().sendPacket(Type.SKILLLEVEL, skill,currentSkillLevel);
			}
		}
	}

	public Exchange getExchange() {
		return exchange;
	}

	public int getGuildId() {
		return guildId;
	}

	public int getGuildLvl() {
		return guildLvl;
	}

	public int getHairStyle() {
		return hairStyle;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public int getLeadership() {
		return lead;
	}

	public int getLime() {
		return lime;
	}

	public int getLvlUpExp() {
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

	public int getStr() {
		return str;
	}

	public int getTotalExp() {
		return totalExp;
	}

	public int getWis() {
		return wis;
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

			String packetData = "inven 3 "
					+ exchangeItem.getItem().getId() + " "
					+ exchangeItem.getItem().getType() + " "
					+ exchangeItem.getPosX() + " " + exchangeItem.getPosY()
					+ " " + exchangeItem.getItem().getGemNumber() + " "
					+ exchangeItem.getItem().getExtraStats() + "\n";
			// inven [Tab] [UniqueId] [Type] [PosX] [PosY] [Gems] [Special]
			
					client.sendData( packetData);
		}
	}

	@Override
	public void loadFromReference(int id) {
		ParsedItem exp = Reference.getInstance().getExpReference()
				.getItemById(id);

		if (exp == null) {
			// cant find Item in the reference continue to load defaults:
			setLvlUpExp(1000);
		} else {

			if (exp.checkMembers(new String[] { "Exp" })) {
				// use member from file
				setLvlUpExp(Integer.parseInt(exp.getMemberValue("Exp")));
			} else {
				// use default
				setLvlUpExp(1000);
			}
		}
	}

	/****** load Quick Slot Items ******/
	public void loadInventory() {
		Client client = getClient();

		if (client == null) {
			return;
		}

		Iterator<InventoryItem> invIter = getInventory()
				.getInventoryIterator();
		while (invIter.hasNext()) {
			InventoryItem invItem = invIter.next();

			String packetData = "inven " + invItem.getTab() + " "
					+ invItem.getItem().getId() + " "
					+ invItem.getItem().getType() + " " + invItem.getPosX()
					+ " " + invItem.getPosY() + " "
					+ invItem.getItem().getGemNumber() + " "
					+ invItem.getItem().getExtraStats() + "\n";
			// inven [Tab] [UniqueId] [Type] [PosX] [PosY] [Gems] [Special]
			
					client.sendData(packetData);

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
	public void logout() {


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
		int newStamina = getStm() - ammount;
		if (newStamina < 2) {
			newStamina = 0;
		}
		updateStatus(2, newStamina, getMaxStm());
		// setCurrStm(newStamina);
		// Client client =
		// Server.getInstance().getNetworkModule().getClient(player);
		// Server.getInstance().getNetworkModule().SendPacket(client.networkId,"status
		// 2 "+player.getPlayerCurrStm()+" "+player.getPlayerMaxStm());
	}

	public abstract void meleeAttack(LivingObject livingObject);

	/****** Manages the Pick command ******/
	// When you pick up an item, or buy something from merchant
	public void pickItem(Item item) {
		Client client = getClient();

		if (client == null) {
			return;
		}
		//TODO: Fix item pickup
		/*Item item = (Item) EntityManager.getEntityManager().getEnt(uniqueid);
		getInventory().addItem(item);
		InventoryItem invItem = getInventory().getItem(item);
		// DatabaseUtils.getInstance().saveInventory(client.getPlayer()Object);

		if (invItem == null) {
			getInventory().setItemSelected(new InventoryItem(item, 0, 0, 0));
			String packetData = "pick " + uniqueid + " " + item.getType()
					+ " 0 0 0 " + item.getGemNumber() + " "
					+ item.getExtraStats() + "\n";
			
					client.SendData(packetData);
			return;
		}

		String packetData = "pick " + uniqueid + " " + item.getType() + " "
				+ invItem.getPosX() + " " + invItem.getPosY() + " "
				+ invItem.getTab() + " " + item.getGemNumber() + " "
				+ item.getExtraStats() + "\n";
		
				client.SendData(packetData);
		*/
		// S> pick [UniqueID] [Type] [Tab] [PosX] [PosY] [GemNumber] [Special]
	}

	/****** Manages the Pickup command ******/
	public void pickupItem(RoamingItem roamingItem) {
		Client client = getClient();
		
		Item item = roamingItem.getItem();
		
		Player owner = roamingItem.getOwner();
		if(roamingItem.getOwner()!=null){
			
			getClient().sendPacket(Type.SAY, "This item belongs to "+owner.getName(),-1);
			return;
			
		}
		
		String packetData = "pickup " + getId() + "\n";
		
		
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

		updateStatus(0, getMaxHp(), getMaxHp());
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

	public void setCombatMode(boolean combatMode) {
		this.combatMode = combatMode;
	}

	public void setConstitution(int cons) {
		this.cons = cons;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public void setDexterity(int dex) {
		this.dex = dex;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}

	public void setGuildId(int guildId) {
		this.guildId = guildId;
	}

	public void setGuildLvl(int guildLvl) {
		this.guildLvl = guildLvl;
	}

	public void setHairStyle(int hairStyle) {
		this.hairStyle = hairStyle;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public void setLeadership(int lead) {
		this.lead = lead;
	}

	public void setLime(int lime) {
		this.lime = lime;
	}

	public void setLvlUpExp(int lvlUpExp) {
		this.lvlUpExp = lvlUpExp;
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
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
	}

	public void setQuickSlot(QuickSlot quickSlot) {
		this.quickSlot = quickSlot;
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

	public void setStash(Stash stash) {
		this.stash = stash;
	}

	public void setStatusPoints(int statusPoints) {
		this.statusPoints = statusPoints;
	}

	public void setStrength(int str) {
		this.str = str;
	}

	public void setTotalExp(int totalExp) {
		this.totalExp = totalExp;
	}

	public void setWisdom(int wis) {
		this.wis = wis;
	}

	/****** player activates skill ******/
	/*
	 * public void useSkill(int skillId) { Client client =
	 * Server.getInstance().getNetworkModule().getClient(this);
	 * 
	 * if(client==null) return;
	 * 
	 * G_Skill skill = getCharSkill().getSkill(skillId);
	 * 
	 * String packetData =
	 * "skill "+skill.getCurrLevel()+" char "+getEntityId()+" "+skillId+"\n";
	 * Server.getInstance().getNetworkModule() .SendPacket(client.networkId,
	 * packetData); // S> skill [SkillLevel] char [CharID] [SkillID] }
	 */
	/****** increase skill level ******/
	public void skillUp(int skillId) {

		Skill skill = getPosition().getMap().getWorld().getSkillManager().getSkill(skillId);
		//TODO: Check why this was linked
		/*
		if (skillId == 3 || skillId == 4 || skillId == 12) {
			levelUpSkill(getCharSkill().getSkill(3));
			levelUpSkill(getCharSkill().getSkill(4));
			levelUpSkill(getCharSkill().getSkill(12));
		} else if (skillId == 5 || skillId == 10 || skillId == 13) {
			levelUpSkill(getCharSkill().getSkill(5));
			levelUpSkill(getCharSkill().getSkill(10));
			levelUpSkill(getCharSkill().getSkill(13));
		}

		else if (skillId == 8 || skillId == 11 || skillId == 14) {
			levelUpSkill(getCharSkill().getSkill(8));
			levelUpSkill(getCharSkill().getSkill(11));
			levelUpSkill(getCharSkill().getSkill(14));
		} else if (skillId == 26 || skillId == 27 || skillId == 28) {
			levelUpSkill(getCharSkill().getSkill(26));
			levelUpSkill(getCharSkill().getSkill(27));
			levelUpSkill(getCharSkill().getSkill(28));
		} else {
			levelUpSkill(getCharSkill().getSkill(skillId));
		}
		*/
		levelUpSkill(skill);

		updateStatus(13, -1, 0);
		// S> skilllevel [SkillNumber] [SkillLevel]
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
			String packetData = "Player not online";
			
			client.sendData(packetData);
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
		TOTALXP(11),
		NEXTLEVELXP(12),
		STATUSPOINTS(13),
		
		//TODO: Finish this
		;
		
		int value;
		Status(int value){
			this.value = value;
			
		}
		public int value(){
			return value;			
		
		}
		
		public static Status byValue(int slotId){
			
			for(Status slot:Status.values())
			{
				if(slot.value()==slotId){					
					return slot;
				}
			}
			return null;
		}
		
	}
	
	/****** Handles all the Status Updates ******/
	public void updateStatus(int id, int curr, int max) {
		String packetData = new String();
		Client client = this.getClient();

		if (client == null) {
			return;
		}

		switch (id) {
		case 0: { // Hp Status
			if (curr > max) {
				curr = max;
			}
			setHp(curr);
			setMaxHp(max);
			packetData = "status " + id + " " + getHp() + " " + getMaxHp()
					+ "\n";
			
					client.sendData(packetData);
			break;
		}
		case 1: { // Mana Status
			if (curr > max) {
				curr = max;
			}
			setCurrMana(curr);
			setMaxMana(max);
			packetData = "status " + id + " " + getMana() + " "
					+ getMaxMana() + "\n";
			
					client.sendData(packetData);
			break;
		}
		case 2: { // Stamina Status
			if (curr > max) {
				curr = max;
			}
			setCurrStm(curr);
			setMaxStm(max);
			packetData = "status " + id + " " + getStm() + " "
					+ getMaxStm() + "\n";
			
					client.sendData(packetData);
			break;
		}
		case 3: { // Electric Energy Status
			if (curr > max) {
				curr = max;
			}
			setCurrElect(curr);
			setMaxElect(max);
			packetData = "status " + id + " " + getElect() + " "
					+ getMaxElect() + "\n";
			
					client.sendData(packetData);
			break;
		}
		case 4: { // Player Level Status
			setLevel(getLevel() + curr);
			packetData = "status " + id + " " + getLevel() + " " + max + "\n";
			
			client.sendData(packetData);

			DatabaseUtils.getInstance()
					.updateCharStatus(this, id, getLevel());
			
			client.sendPacket(Type.LEVELUP, this);
			
			getInterested().sendPacket(Type.LEVELUP, this);

			break;
		}
		case 10: { // Player Lime Status
			setLime(getLime() + curr);
			packetData = "status " + id + " " + getLime() + " " + max + "\n";
			
			client.sendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getLime());
			break;
		}
		case 11: { // Player Total Exp Status
			setTotalExp(getTotalExp() + curr);
			packetData = "status " + id + " " + getTotalExp() + " " + max
					+ "\n";
			
					client.sendData(packetData);
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
				updateStatus(2, getMaxStm(), getMaxStm());
				updateStatus(3, getMaxElect(), getMaxElect());

				loadFromReference(getLevel());
				packetData = "status " + id + " " + getLvlUpExp() + " " + max
						+ "\n";
				
						client.sendData(packetData);
				DatabaseUtils.getInstance().updateCharStatus(this, id,
						getLvlUpExp());
			} else {
				setLvlUpExp(curr);
				packetData = "status " + id + " " + getLvlUpExp() + " " + max
						+ "\n";
				
						client.sendData(packetData);
				DatabaseUtils.getInstance().updateCharStatus(this, id,
						getLvlUpExp());
			}
			break;
		}
		case 13: { // Player Distribution Status Points
			setStatusPoints(getStatusPoints() + curr);
			packetData = "status " + id + " " + getStatusPoints() + " " + max
					+ "\n";
			
					client.sendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id,
					getStatusPoints());
			break;
		}
		case 14: { // Player Strenght Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setStrength(getStr() + curr);
			packetData = "status " + id + " " + getStr() + " " + max + "\n";
			
					client.sendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getStr());

			updateStatus(0, getHp(), getMaxHp() + (getStr() / 50) + 1);
			updateStatus(2, getStm(), getMaxStm() + (getStr() / 60) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 15: { // Player Wisdom Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setWisdom(getWis() + curr);
			packetData = "status " + id + " " + getWis() + " " + max + "\n";
			
					client.sendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getWis());

			updateStatus(1, getMana(), getMaxMana() + (getWis() / 50) + 2);
			updateStatus(3, getElect(), getMaxElect() + (getWis() / 50) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 16: { // Player Dex Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setDexterity(getDexterity() + curr);
			packetData = "status " + id + " " + getDexterity() + " " + max + "\n";
			
					client.sendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getDexterity());

			updateStatus(1, getMana(), getMaxMana() + (getDexterity() / 50) + 1);
			updateStatus(3, getElect(), getMaxElect() + (getDexterity() / 50) + 2);
			updateStatus(13, -1, 0);
			break;
		}
		case 17: { // Player Strain Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setConstitution(getConstitution() + curr);
			packetData = "status " + id + " " + getConstitution() + " " + max + "\n";
			
					client.sendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getConstitution());

			updateStatus(0, getHp(), getMaxHp() + (getConstitution() / 50) + 2);
			updateStatus(2, getStm(), getMaxStm() + (getConstitution() / 50) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 18: { // Player Charisma Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setLeadership(getLeadership() + curr);
			packetData = "status " + id + " " + getLeadership() + " " + max + "\n";
			
					client.sendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getLeadership());

			if (getLeadership() % 2 == 0) {
				updateStatus(0, getHp(), getMaxHp() + 1);
				updateStatus(1, getMana(), getMaxMana() + 1);
				updateStatus(2, getStm(), getMaxStm() + 1);
				updateStatus(3, getElect(), getMaxElect() + 1);
			}
			updateStatus(13, -1, 0);
			break;
		}
		case 19: { // Player Penalty Points Status ([inGame=packet] -> 100=10;
					// 1000=100; 10000=1000)
			packetData = "status " + id + " " + curr + " " + max + "\n";
			
					client.sendData(packetData);
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
}
