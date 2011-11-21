package com.googlecode.reunion.jreunion.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.client.ClientDisconnectEvent;
import com.googlecode.reunion.jreunion.events.client.ClientEvent.ClientFilter;
import com.googlecode.reunion.jreunion.events.map.ItemPickupEvent;
import com.googlecode.reunion.jreunion.events.map.PlayerLogoutEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkAcceptEvent;
import com.googlecode.reunion.jreunion.events.session.SessionEvent;
import com.googlecode.reunion.jreunion.game.Equipment.Slot;
import com.googlecode.reunion.jreunion.game.items.equipment.Armor;
import com.googlecode.reunion.jreunion.game.quests.QuestState;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Client.State;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.PacketParser;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Session;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Tools;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class Player extends LivingObject implements EventListener {

	private long defense = 0;
	
	java.util.Map<Skill,Integer> skills = new HashMap<Skill,Integer> ();

	private long totalExp;

	private long lvlUpExp;

	private long lime; // Gold
	
	private int slot;
	
	
	public int playerId = -1 ; //id used for database storage

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

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

	private long strength;

	private long wisdom;

	private long dexterity;

	private long constitution;

	private long leadership;

	private Sex sex; // 0 - Male; 1 - Female

	private long speed;

	private long statusPoints;

	private Inventory inventory;

	private long penaltyPoints;

	private long adminState; // 0 - normal user; 255 - SuperGM

	private Session playerSession;

	private long hairStyle;

	private Equipment equipment;

	private List<Long> attackQueue = new Vector<Long>();

	private QuickSlotBar quickSlotBar;
	
	private QuestState questState;

	private Stash stash;

	private Exchange exchange;

	private long guildId;

	private long guildLevel;
	
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
		equipment = new Equipment(this);
		quickSlotBar = new QuickSlotBar(this);
		stash = new Stash(this);
		exchange = new Exchange(this);
		
		client.addEventListener(ClientDisconnectEvent.class, this, new ClientFilter(client));

	}
	
	public void addAttack(long attack) {
		if (attackQueue.size() >= 5) {
			attackQueue.remove(0);
		}
		attackQueue.add(attack);
	}
	
	public void clearAttackQueue() {
		attackQueue.clear();
	}

	/****** Manages the Item Drop ******/
	public void dropItem(int playerId) {
		
		HandPosition handPosition = getInventory().getHoldingItem();
		
		if (handPosition == null) {
			Logger.getLogger(Player.class).error("Failed to get Player "+this+" holding item (HandPosition=NULL)");
			return;
		}
		
		Item<?> item = handPosition.getItem();
		
		if (item == null) {
			Logger.getLogger(Player.class).error("Failed to get Player "+this+" holding item (Item=NULL)");
			return;
		}
		
		LocalMap map = getPosition().getLocalMap();
		RoamingItem roamingItem = map.getWorld().getCommand().dropItem(getPosition(), item);
		Logger.getLogger(Player.class).info("Player "+this+" droped roaming item "+roamingItem);
		getInventory().setHoldingItem(null);
	}
	

	public long getAdminState() {
		return adminState;
	}
	
	@Override
	public synchronized void setHp(long hp){
		long before = this.getHp();
		super.setHp(hp);	
		if(before!=this.getHp())
			this.sendStatus(Status.HP);
	}
	
	public void sendStatus(Status status) {
		if(client.getState()==State.INGAME||client.getState()==State.LOADED){
			long min=0,max=0;
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

	public abstract long getMaxElectricity();
	

	public abstract long getMaxMana();
	

	public abstract long getMaxStamina();
	

	public synchronized void setStamina(long stamina){
		long before = this.stamina;
		this.stamina = Tools.between(stamina, 0l, getMaxStamina());
		if(before!=this.stamina)
			this.sendStatus(Status.STAMINA);
	}
	
	public abstract long getBaseDamage();
	

	public List<Long> getAttackQueue() {
		return attackQueue;
	}

	public long getBestAttack() {
		
		long bestAttack = 0;
		
		for(long queuedAttack : getAttackQueue()){
			if(queuedAttack > bestAttack)
				bestAttack = queuedAttack;
		}
	
		return bestAttack;
	}
	
	public boolean isInCombat() {
		return isInCombat;
	}

	public long getConstitution() {
		return constitution;
	}

	public long getDef() {
		return defense;
	}

	public long getDexterity() {
		return dexterity;
	}

	/*** Return the distance between the player and the living object ***/
	public long getDistance(LivingObject livingObject) {
		double xcomp = Math.pow(livingObject.getPosition().getX() - getPosition().getX(), 2);
		double ycomp = Math.pow(livingObject.getPosition().getY() - getPosition().getY(), 2);
		double distance = Math.sqrt(xcomp + ycomp);
		return (long)distance;
	}

	public Equipment getEquipment() {
		return equipment;
	}
	
	public Exchange getExchange() {
		return exchange;
	}

	public long getGuildId() {
		return guildId;
	}

	public long getGuildLvl() {
		return guildLevel;
	}

	public long getHairStyle() {
		return hairStyle;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public long getLeadership() {
		return leadership;
	}

	public long getLime() {
		return lime;
	}

	public long getLevelUpExp() {
		return lvlUpExp;
	}

	public long getPenaltyPoints() {
		return penaltyPoints;
	}

	public Quest getQuest() {
		if(getQuestState()!=null){
			return getQuestState().getQuest();
		}
		return null;
	}
	
	public QuestState getQuestState() {
		return questState;
	}

	public QuickSlotBar getQuickSlotBar() {
		return quickSlotBar;
	}
	
	private long mana;

	private long electricity;

	private long stamina;

	public long getElectricity() {
		return electricity;
	}	
	
	public long getStamina() {
		return stamina;
	}
	
	
	public long getMana() {
		return mana;
	}

	public synchronized void setMana(long mana) {
		long before = this.mana;
		this.mana = Tools.between(mana, 0l, getMaxMana());
		if(before!=this.mana)
			sendStatus(Status.MANA);
	}

	public synchronized void setElectricity(long electricity) {
		long before = this.electricity;
		this.electricity = Tools.between(electricity, 0l, getMaxElectricity());
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
		
		//this.setPosition(new Position(7026, 5220, 106, this.getPosition().getLocalMap(), 0.0f));
		//getClient().getWorld().getCommand().GoToPos(this, this.getPosition());
		
		PlayerSpawn defaultSpawn = this.getPosition().getLocalMap().getDefaultSpawn();
		//TODO: Gracefully handle respawn
		defaultSpawn.spawn(this);
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

	public long getSpeed() {
		return speed;
	}

	public Stash getStash() {
		return stash;
	}

	public long getStatusPoints() {
		return statusPoints;
	}
	
	public long getMaxStatusPoints() {
		return (getLevel() <= 250) ? getLevel()*3 : 250*3+(getLevel()-250)*10;
	}

	public long getStrength() {
		return strength;
	}

	public long getTotalExp() {
		return totalExp;
	}

	public long getWisdom() {
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

		if (getInventory().getHoldingItem() == null) {
			ExchangeItem item = getExchange().getItem(posX, posY);

			if (item == null) {
				return;
			}

			InventoryItem invItem = new InventoryItem(item.getItem(), new InventoryPosition( 0, 0, 0));

			getInventory().setHoldingItem(new HandPosition(invItem.getItem()));
			getExchange().removeItem(item);
		} else {
			Item<?> item = getInventory().getHoldingItem().getItem();
			ExchangeItem newExchangeItem = new ExchangeItem(item, posX,
					posY);
			ExchangeItem oldExchangeItem = null;
			int x = 0, y = 0;

			while (oldExchangeItem == null && x < item.getType().getSizeX()) {
				while (oldExchangeItem == null && y < item.getType().getSizeY()) {
					oldExchangeItem = getExchange().getItem(posX + x, posY + y);
					y++;
				}
				y = 0;
				x++;
			}

			if (oldExchangeItem == null) {
				getInventory().setHoldingItem(null);
			} else {
				InventoryItem invItem = new InventoryItem(
						oldExchangeItem.getItem(), new InventoryPosition( 0, 0, 0));
				getInventory().setHoldingItem(new HandPosition(invItem.getItem()));
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
			Item<?> item = exchangeItem.getItem();
			
			if(item.getEntityId()==-1){
				client.getPlayer().getPosition().getLocalMap().createEntityId(item);
			}
			
			client.sendPacket(Type.INVEN, exchangeItem);

			// inven [Tab] [UniqueId] [Type] [PosX] [PosY] [Gems] [Special]
		}
	}

	@Override
	public void loadFromReference(int id) {
		ParsedItem parsedItem = Reference.getInstance().getExpReference()
				.getItemById(id);

		if (parsedItem == null) {
			// cant find Item in the reference continue to load defaults:
			setLevelUpExp(1000);
		} else {
			if (parsedItem.checkMembers(new String[] { "Exp" })) {
				// use member from file
				try{
					setLevelUpExp(Long.parseLong(parsedItem.getMemberValue("Exp")));
				} 
				catch(Exception NumerFormatException)
				{
					setLevelUpExp(1000);
					Logger.getLogger(Player.class).info(getName()+
							" level up experience value, not supported by LONG");
				}
			} else {
				// use default
				setLevelUpExp(1000);
			}
		}
	}

	/****** load Inventory Items ******/
	public void loadInventory() {
		Client client = getClient();
		
		if (client == null) {
			return;
		}
	
		Iterator<InventoryItem> invIter = getInventory()
				.getInventoryIterator();
		while (invIter.hasNext()) {
			InventoryItem invItem = invIter.next();
			Item<?> item = invItem.getItem();
			if(item.getEntityId()==-1){
				client.getPlayer().getPosition().getLocalMap().createEntityId(item);
			}
			client.sendPacket(Type.INVEN, invItem);

		}
	}

	/****** load Quick Slot Items ******/
	public void loadQuickSlot() {
		Client client = getClient();

		if (client == null) {
			return;
		}

		Iterator<QuickSlotItem> quickSlot = getQuickSlotBar()
				.getQuickSlotIterator();
		while (quickSlot.hasNext()) {
			QuickSlotItem qsItem = quickSlot.next();
			Item<?> item = qsItem.getItem();
			
			if(item.getEntityId()==-1){
				client.getPlayer().getPosition().getLocalMap().createEntityId(item);
			}
			client.sendPacket(Type.QUICK, qsItem);
		}
	}
	
	/****** load Equipment Items ******/
	public void loadEquipment(LocalMap localMap) {
		
		if(localMap == null)
			return;
		for(Equipment.Slot slot: Equipment.Slot.values()){
			Item<?> item = getEquipment().getItem(slot);
			
			if(item == null)
				continue;
			
			if(item.getEntityId()==-1){
				localMap.createEntityId(item);
			}
		}
	}

	/****** load Stash Items ******/
	public void loadStash(LocalMap localMap) {
		
		if(localMap == null)
			return;
		
		Iterator<StashItem> stashIter = getStash().itemListIterator();
		
		while(stashIter.hasNext()){
			StashItem stashItem = (StashItem) stashIter.next();
			Item<?> item = stashItem.getItem();
			
			if(item == null)
				continue;
			
			if(item.getEntityId()==-1){
				localMap.createEntityId(item);
			}
		}
	}

	/****** Manages the char Logout ******/
	public synchronized void save() {

		if(getEntityId() != -1){
			Logger.getLogger(Player.class).info("Player " + getName() + " saving...\n");
			DatabaseUtils.getDinamicInstance().saveSkills(this);
			DatabaseUtils.getDinamicInstance().saveInventory(this);
			DatabaseUtils.getDinamicInstance().saveCharacter(this);
			DatabaseUtils.getDinamicInstance().saveEquipment(this);
			DatabaseUtils.getDinamicInstance().saveStash(getClient());
			DatabaseUtils.getDinamicInstance().saveExchange(this);
			DatabaseUtils.getDinamicInstance().saveQuickSlot(this);
			DatabaseUtils.getDinamicInstance().saveQuest(this);
		}
		
	}

	public void loseStamina(long ammount) {	
		setStamina(getStamina() - ammount);		
	}


	/****** Manages the Pick command ******/
	// When you pick up an item, or buy something from merchant
	public void pickItem(Item<?> item, int roamingItemEntityId, int neededTab) {
		
		Client client = getClient();
		
		InventoryItem inventoryItem = getInventory().storeItem(item, neededTab);
		
		if (inventoryItem == null) {
			inventoryItem = new InventoryItem(item, new InventoryPosition(0, 0, 0));
			getInventory().setHoldingItem(new HandPosition(inventoryItem.getItem()));
		}
		
		client.sendPacket(Type.PICK, inventoryItem, roamingItemEntityId==-1 ? item.getEntityId() : roamingItemEntityId);
		
	}

	/****** Manages the Pickup command ******/
	public void pickupItem(RoamingItem roamingItem) {
		
		Client client = getClient();
		Player owner = roamingItem.getOwner();
		
		if(owner!=null && owner!=this) {
			client.sendPacket(Type.SAY, "This item belongs to " + owner.getName());
			return;
		}		
		
		roamingItem.stopDeleteTimer();
		getPosition().getLocalMap().fireEvent(ItemPickupEvent.class, this, roamingItem);
		Logger.getLogger(PacketParser.class).info("Player "+this+ " picked up roaming item " + roamingItem);
		// S> pickup [CharID]
	}

	public void place(Position position, int unknown, boolean running) {
		
		setIsRunning(running);
		
		//synchronized(this){
			setPosition(position);
			setTargetPosition(position.clone());			
		//}
		
		this.getInterested().sendPacket(Type.PLACE, this, unknown);
		
	}

	/****** revive player when he dies ******/
	public void revive() {
		long hp = getLevel() <= 30 ? getMaxHp() : (long)(getMaxHp()*.1);
		setHp(hp);
		spawn();
	}
	
	public int getSkillLevel(Skill skill){
		return skills.get(skill);
	}

	public java.util.Map<Skill, Integer> getSkills() {
		return skills;
	}

	public void say(String text) {
		if(text.charAt(0) != '@')
		{
			getInterested().sendPacket(Type.SAY, text, this);
			getClient().sendPacket(Type.SAY, text, this);
		}
	}
	
	public void resetSkills()
	{
		java.util.Map<Skill,Integer> playerSkills = new HashMap<Skill,Integer> ();
		playerSkills = getSkills(); 
		
		// reset player skills to its minimum level
		for(Skill skill: playerSkills.keySet()){
			skill.reset(this);
		}
	}
	
	public void setAdminState(long adminState) {
		this.adminState = adminState;
	}

	public void setIsInCombat(boolean isInCombat) {
		this.isInCombat = isInCombat;
		getInterested().sendPacket(Type.COMBAT, this);
	}

	public void setConstitution(long cons) {
		this.constitution = cons;
		sendStatus(Status.CONSTITUTION);
		sendStatus(Status.HP);
		//sendStatus(Status.STAMINA);
	}

	public void setDefense() {
		
		this.defense = 0;
		
		for(Equipment.Slot slot: Equipment.Slot.values()){
			Item<?> item = getEquipment().getItem(slot);
			
			if(item==null)
				continue;
			
			if(item.is(Armor.class)){
				this.defense += ((Armor)item.getType()).getDef(item);
			}
		}
	}

	public void setDexterity(long dex) {
		this.dexterity = dex;
		sendStatus(Status.DEXTERITY);
		sendStatus(Status.ELECTRICITY);
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public void setGuildId(long guildId) {
		this.guildId = guildId;
	}
	
	@Override
	public void setLevel(int level) {
		super.setLevel(level);
		loadFromReference(level);
		if(client.getState()==State.INGAME) {
			
			sendStatus(Status.LEVEL);
			client.sendPacket(Type.LEVELUP, this);		
			getInterested().sendPacket(Type.LEVELUP, this);
			
			setHp(this.getMaxHp());
			setMana(this.getMaxHp());
			setElectricity(this.getMaxElectricity());
			setStamina(this.getMaxStamina());
		}
	}

	public void setGuildLevel(long guildLevel) {
		this.guildLevel = guildLevel;
	}

	public void setHairStyle(long hairStyle) {
		this.hairStyle = hairStyle;
	}

	public void setLeadership(long lead) {
		this.leadership = lead;
		sendStatus(Status.LEADERSHIP);
		sendStatus(Status.HP);
		sendStatus(Status.MANA);
		sendStatus(Status.STAMINA);
		sendStatus(Status.ELECTRICITY);
	}

	public void setLime(long lime) {
		this.lime = lime;
		this.sendStatus(Status.LIME);
	}

	public void setLevelUpExp(long lvlUpExp) {
		//synchronized(this) {
			
			if(lvlUpExp<=0){
				this.setLevel(getLevel()+1);
				if(this.getLevel() > 250){
					this.setStatusPoints(this.getStatusPoints()+10);
				} else {
					this.setStatusPoints(this.getStatusPoints()+3);
				}
				this.setLevelUpExp(this.getLevelUpExp()-Math.abs(lvlUpExp));
			}
			else{
				this.lvlUpExp = lvlUpExp;
				sendStatus(Status.LEVELUPEXP);
			}
		//}
	}

	public void setPenaltyPoints(long penaltyPoints) {
		this.penaltyPoints = penaltyPoints;
		sendStatus(Status.PENALTYPOINTS);
	}

	public void setQuest(Quest quest) {
		if(quest == null){
			if (getQuestState()!=null){
				if(getQuestState().isComplete()){
					client.sendPacket(Type.QT, "end " + getQuest().getId());
				}else{
					client.sendPacket(Type.SAY, "Quest cancelled.");
					client.sendPacket(Type.QT, "get -1");
				}
				setQuestState(null);
			}
		} else{
			setQuestState(new QuestState(quest));
			client.sendPacket(Type.QT, "get " + quest.getId());
		}		
	}
	
	public void setQuestState(QuestState questState){
		this.questState = questState;
	}

	public void setSession(Session session) {
		playerSession = session;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public void setStatusPoints(long statusPoints) {
		this.statusPoints = statusPoints;
		sendStatus(Status.STATUSPOINTS);
	}

	public void setStrength(long str) {
		this.strength = str;
		sendStatus(Status.STRENGTH);
		sendStatus(Status.HP);
		sendStatus(Status.STAMINA);
	}

	public void setTotalExp(long totalExp) {
		this.totalExp = totalExp;
		sendStatus(Status.TOTALEXP);
	}

	public void setWisdom(long wisdom) {
		this.wisdom = wisdom;
		sendStatus(Status.WISDOM);
		sendStatus(Status.MANA);
	}

	public void social(long emotionId) {

		getInterested().sendPacket(Type.SOCIAL, this, emotionId);
		
	}

	public void stop(Position position) {

		//synchronized(this) {
			setPosition(position);	
			setTargetPosition(position.clone());		
		//}
		
		this.getInterested().sendPacket(Type.S_CHAR, this);
	
	}

	public void tell(String targetName, String text) {

		if(targetName.equals(getName()))
			return;
		
		Client client = getClient();
		
		Player targetPlayer = client.getWorld().getPlayerManager().getPlayer(targetName);
		
		if (targetPlayer == null) {
			client.sendPacket(Type.SAY, targetName+" is not online!");
			return;
		}

		client.sendPacket(Type.WISPER, text,""+getEntityId(), targetPlayer.getName(), "->Whisper*");
		targetPlayer.getClient().sendPacket(Type.WISPER, text,""+getEntityId(),getName(), "<-Whisper*");
	}
	
	public static enum Status {
		
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
	public void updateStatus(int id, long curr, long max) {
		Client client = this.getClient();

		if (client == null) {
			return;
		}

		switch (id) {


		case 4: { // Player Level Status
			setLevel(getLevel() + (int)curr);
			client.sendPacket(Type.STATUS, id, getLevel(), max);
			
			DatabaseUtils.getDinamicInstance()
					.updateCharStatus(this, id, getLevel());
			
			client.sendPacket(Type.LEVELUP, this);
			
			getInterested().sendPacket(Type.LEVELUP, this);

			break;
		}
		case 10: { // Player Lime Status
			setLime(getLime() + curr);
			client.sendPacket(Type.STATUS, id, getLime(), max);
			
			DatabaseUtils.getDinamicInstance().updateCharStatus(this, id, getLime());
			break;
		}
		case 11: { // Player Total Exp Status
			setTotalExp(getTotalExp() + curr);
			client.sendPacket(Type.STATUS, id, getTotalExp(), max);
	
			DatabaseUtils.getDinamicInstance().updateCharStatus(this, id,
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
						
				DatabaseUtils.getDinamicInstance().updateCharStatus(this, id,
						getLevelUpExp());
			} else {
				setLevelUpExp(curr);
				
				client.sendPacket(Type.STATUS, id, getLevelUpExp(), max);
						
				DatabaseUtils.getDinamicInstance().updateCharStatus(this, id,
						getLevelUpExp());
			}
			break;
		}
		case 13: { // Player Distribution Status Points
			setStatusPoints(getStatusPoints() + curr);
			
			
			
			client.sendPacket(Type.STATUS, id, getStatusPoints(), max);
			DatabaseUtils.getDinamicInstance().updateCharStatus(this, id,
					getStatusPoints());
			break;
		}
		case 14: { // Player Strenght Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setStrength(getStrength() + curr);			
			client.sendPacket(Type.STATUS, id, getStrength(), max);
					
			DatabaseUtils.getDinamicInstance().updateCharStatus(this, id, getStrength());

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
					
			DatabaseUtils.getDinamicInstance().updateCharStatus(this, id, getWisdom());

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
					
			DatabaseUtils.getDinamicInstance().updateCharStatus(this, id, getDexterity());

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
					
			DatabaseUtils.getDinamicInstance().updateCharStatus(this, id, getConstitution());

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
					
			DatabaseUtils.getDinamicInstance().updateCharStatus(this, id, getLeadership());

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
					
			DatabaseUtils.getDinamicInstance().updateCharStatus(this, id,
					getPenaltyPoints());
			break;
		}
		}
		// S> status [StatusConstant] [Current] [Max]

		// DatabaseUtils.getInstance().saveCharStatus(this);
	}

	public void wearSlot(Slot slot) {
	
		HandPosition handPosition = getInventory().getHoldingItem();
		//InventoryItem invItem = new InventoryItem(getInventory().getHoldingItem().getItem(),
		//		new InventoryPosition(0,0,0));

		if (handPosition == null) {
			
			getInventory().setHoldingItem(new HandPosition(getEquipment().getItem(slot)));
			getEquipment().setItem(slot, null);
			getInterested().sendPacket(Type.CHAR_REMOVE, this, slot);
			Logger.getLogger(Player.class).info("Player "+this+" removed equipment "
					+getInventory().getHoldingItem().getItem());
			
		} else {
			InventoryItem invItem = new InventoryItem(getInventory().getHoldingItem().getItem(),
							new InventoryPosition(0,0,0));
			Item<?> wearingItem = getEquipment().getItem(slot);
			
			if( (wearingItem != null) ){
				getInventory().setHoldingItem(new HandPosition(wearingItem));
				getInterested().sendPacket(Type.CHAR_REMOVE, this, slot);
				Logger.getLogger(Player.class).info("Player "+this+" removed equipment "+wearingItem);
			} else {
				getInventory().setHoldingItem(null);
			}
			
			getEquipment().setItem(slot, invItem.getItem());
			getInterested().sendPacket(Type.CHAR_WEAR, this, slot, invItem.getItem());
			Logger.getLogger(Player.class).info("Player "+this+" equiped item "+invItem.getItem());
			
			/*
			if (getEquipment().getItem(slot) == null) {
				Item<?> item = invItem.getItem();
				
				getInterested().sendPacket(Type.CHAR_WEAR, this, slot, item);
				
				getEquipment().setItem(slot, item);
				getInventory().setHoldingItem(null);
				Logger.getLogger(Player.class).info("Player "+this+" equiped item "+item);
				/*
				if (getEquipment().getItem(slot) instanceof Weapon) {
					Weapon weapon = (Weapon) getEquipment().getItem(slot);
					setMinDmg(weapon.getMinDamage());
					setMaxDmg(weapon.getMaxDamage());
				}
				
			} else {
				Item<?> currentItem = getEquipment().getItem(slot);
				getInterested().sendPacket(Type.CHAR_REMOVE, this, slot);
				getEquipment().setItem(slot, invItem.getItem());
				getInventory().setHoldingItem(new HandPosition(currentItem));
				
				if (getEquipment().getItem(slot) instanceof Weapon) {
					Weapon weapon = (Weapon) getEquipment().getItem(slot);
					setMinDmg(weapon.getMinDamage());
					setMaxDmg(weapon.getMaxDamage());
				}
				
				Item<?> item = getEquipment().getItem(slot);

				getInterested().sendPacket(Type.CHAR_WEAR, this, slot, item);
				
				
			}
		*/

		}
		// DatabaseUtils.getInstance().saveEquipment(this);
		setDefense();

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
			setSessionRadius((int)getClient().getWorld().getServerSetings().getSessionRadius());
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
			Position position = getPosition();
			if(position!=null&&position.getMap()!=null){
				LocalMap map = getPosition().getLocalMap();
				map.fireEvent(PlayerLogoutEvent.class, this);
			}
		}
		if(event instanceof SessionEvent){
			SessionEvent sessionEvent = (SessionEvent) event;
		}
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");

		buffer.append("id:");
		buffer.append(getEntityId());
		buffer.append("("+getPlayerId()+")");
		buffer.append(", ");
		
		buffer.append("name:");
		buffer.append(getName());
		buffer.append(", ");
		
		buffer.append("race:");
		buffer.append(getRace());
		buffer.append(", ");
		
		buffer.append("level:");
		buffer.append(getLevel());		
				
		buffer.append("}");
		return buffer.toString();
	}
	
	public Skill getSkill(int id){
		Skill skill = getPosition().getLocalMap().getWorld().getSkillManager().getSkill(id);		
		return skills.containsKey(skill)?skill:null;
		
	}

	public void setSkillLevel(Skill skill, int level) {
			
		skills.put(skill, level);
		
	}

	public void addStatus(Status status) {
		//synchronized(this){
			long statusPoints = getStatusPoints();
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
		//}
	}
}
