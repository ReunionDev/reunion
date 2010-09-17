package com.googlecode.reunion.jreunion.game;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.googlecode.reunion.jcommon.S_ParsedItem;
import com.googlecode.reunion.jcommon.S_Parser;
import com.googlecode.reunion.jreunion.game.Enums.G_EquipmentSlot;
import com.googlecode.reunion.jreunion.server.CharSkill;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Session;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class Player extends LivingObject implements SkillTarget {

	private int def = 0;

	private int minDmg;

	private int maxDmg;

	private int totalExp;

	private int lvlUpExp;

	private int lime; // Gold

	private boolean combatMode; // 0 - Peace Mode; 1 - Attack Mode

	private int str;

	private int wis;

	private int dex;

	private int cons;

	private int lead;

	private int sex; // 0 - Male; 1 - Female

	private int speed;

	private int statusPoints;

	private Inventory inventory;

	private String name;

	private int penaltyPoints;

	private boolean runMode; // 0 - Off; 1 - On

	private int adminState; // 0 - normal user; 255 - SuperGM

	private Session playerSession;

	private int hairStyle;

	private Equipment equipment;

	private List<Integer> attackQueue = new Vector<Integer>();

	private CharSkill charSkill;

	private QuickSlot quickSlot;

	private Quest quest;

	private Stash stash;

	private Exchange exchange;

	private int guildId;

	private int guildLvl;
	
	private static Integer sessionRadius;

	public Player() {
		super();
		inventory = new Inventory();
		equipment = new Equipment();
		charSkill = new CharSkill();
		quickSlot = new QuickSlot();
		stash = new Stash();
		exchange = new Exchange();
		// setPlayerMinDmg(325);
		// setPlayerMaxDmg(370);
	}

	public void addAttack(int attack) {
		if (attackQueue.size() >= 5) {
			attackQueue.remove(0);
		}
		attackQueue.add(attack);
	}


	public void charCombat(int combat) {

		if (combat == 1) {
			setCombatMode(true);
		} else {
			setCombatMode(false);
		}

		Iterator<WorldObject> playerIter = getSession()
				.getPlayerListIterator();

		while (playerIter.hasNext()) {
			Player player = (Player) playerIter.next();
			Client client = Server.getInstance().getNetworkModule()
					.getClient(player);

			if (client == null) {
				continue;
			}

			String packetData = "combat " + getEntityId() + " " + combat
					+ "\n";
					client.SendData( packetData);
		}
		
	}

	public void clearAttackQueue() {
		for (int i = 0; i < attackQueue.size(); i++) {
			attackQueue.remove(i);
		}
	}

	/****** Manages the Item Drop ******/
	public void dropItem(int uniqueId) {
	
		try {
			Item item = getInventory().getItemSelected().getItem();
	
			if (item == null) {
				return;
			}
		
			Client client = Server.getInstance().getNetworkModule()
					.getClient(this);
			if (client == null) {
				return;
			}
			getInventory().setItemSelected(null);
			if (item != null) {
				String packetData = "drop " + item.getEntityId() + " " + item.getType()
						+ " " + getPosition().getX() + " " + getPosition().getY() + " " + getPosition().getZ() + " "
						+ getPosition().getRotation() + " " + item.getGemNumber() + " "
						+ item.getExtraStats() + "\n";
				System.out.println(packetData);
				client.SendData(packetData);
	
				Iterator<WorldObject> playerIter = getSession()
						.getPlayerListIterator();

				while (playerIter.hasNext()) {
					Player player = (Player) playerIter.next();
					client = Server.getInstance().getNetworkModule()
							.getClient(player);

					if (client == null) {
						continue;
					}

					client.SendData( packetData);
				}
				
			}
			
			// S> drop [ItemID] [ItemType] [PosX] [PosY] [Height] [Rotation]
			// [GemNumber] [Special]
		} catch (Exception e) {
			//TODO: look at this
			System.out.println("Itembug not fixxed but server crash");
		}
		
		
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

	public CharSkill getCharSkill() {
		return charSkill;
	}

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
		Client client = Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		String packetData = new String();

		getCharSkill().incSkill(this, skill);
		packetData = "skilllevel " + skill.getId() + " " + skill.getCurrLevel()
				+ "\n";
				client.SendData( packetData);
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

	public int getRace() {
		if(this instanceof BulkanPlayer)
			return Enums.RACE_BULKAN;
		if(this instanceof AidiaPlayer)
			return Enums.RACE_AIDIA;
		if(this instanceof KailiptonPlayer)
			return Enums.RACE_KAILIPTON;
		if(this instanceof HumanPlayer)
			return Enums.RACE_HUMAN;
		if(this instanceof HybriderPlayer)
			return Enums.RACE_HYBRIDER;		
		throw new RuntimeException("Unknown race: "+this);
	}

	public void spawn() {
		
		int defaultSpawnId = Integer.parseInt(Reference.getInstance().getMapReference().getItemById(getPosition().getMap().getId()).getMemberValue("DefaultSpawnId"));
		S_ParsedItem defaultSpawn =getPosition().getMap().getPlayerSpawnReference().getItemById(defaultSpawnId);
				
		S_Parser playerSpawns = getPosition().getMap().getPlayerSpawnReference();
		Iterator<S_ParsedItem> iter = playerSpawns.getItemListIterator();

		while (iter.hasNext()) {
			S_ParsedItem item = iter.next();

			if (Integer.parseInt(item.getMemberValue("Id"))!=defaultSpawnId) {
				Rectangle rectangle = new Rectangle(Integer.parseInt(item
						.getMemberValue("TargetX")), Integer.parseInt(item
						.getMemberValue("TargetY")), Integer.parseInt(item
						.getMemberValue("TargetWidth")), Integer.parseInt(item
						.getMemberValue("TargetHeight")));

				if (rectangle.contains(getPosition().getX(), getPosition().getY())) {
					Server.getInstance()
							.getWorldModule()
							.getWorldCommand()
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
		.getWorldModule()
		.getWorldCommand()
		.GoToPos(this,spawnX,spawnY);
	}

	public boolean getRunMode() {
		return runMode;
	}

	/**
	 * @return Returns the playerSession.
	 * @uml.property name="playerSession"
	 */
	public Session getSession() {
		return playerSession;
	}

	public int getSex() {
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

	public static Player createPlayer(int race){
		
		if (race == Enums.RACE_BULKAN) return new BulkanPlayer();
		else if (race == Enums.RACE_KAILIPTON) return new KailiptonPlayer();
		else if (race == Enums.RACE_AIDIA) return  new AidiaPlayer();
		else if (race == Enums.RACE_HUMAN) return new HumanPlayer();
		else if (race == Enums.RACE_HYBRIDER)	return new HybriderPlayer();
		
		throw new RuntimeException("Invalid race: "+race);
		
	}
	/*** Manages the Items add/Remove from Trade Box ***/
	public void itemExchange(int posX, int posY) {

		Client client = Server.getInstance().getNetworkModule()
				.getClient(this);

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
		Client client = Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		Iterator<ExchangeItem> exchangeIter = getExchange()
				.itemListIterator();
		while (exchangeIter.hasNext()) {
			ExchangeItem exchangeItem = exchangeIter.next();

			String packetData = "inven 3 "
					+ exchangeItem.getItem().getEntityId() + " "
					+ exchangeItem.getItem().getType() + " "
					+ exchangeItem.getPosX() + " " + exchangeItem.getPosY()
					+ " " + exchangeItem.getItem().getGemNumber() + " "
					+ exchangeItem.getItem().getExtraStats() + "\n";
			// inven [Tab] [UniqueId] [Type] [PosX] [PosY] [Gems] [Special]
			
					client.SendData( packetData);
		}
	}

	@Override
	public void loadFromReference(int id) {
		S_ParsedItem exp = Reference.getInstance().getExpReference()
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
		Client client = Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		Iterator<InventoryItem> invIter = getInventory()
				.getInventoryIterator();
		while (invIter.hasNext()) {
			InventoryItem invItem = invIter.next();

			String packetData = "inven " + invItem.getTab() + " "
					+ invItem.getItem().getEntityId() + " "
					+ invItem.getItem().getType() + " " + invItem.getPosX()
					+ " " + invItem.getPosY() + " "
					+ invItem.getItem().getGemNumber() + " "
					+ invItem.getItem().getExtraStats() + "\n";
			// inven [Tab] [UniqueId] [Type] [PosX] [PosY] [Gems] [Special]
			
					client.SendData(packetData);

		}
	}

	/****** load Quick Slot Items ******/
	public void loadQuickSlot() {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(this);

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
						+ item.getEntityId() + " "
						+ item.getType() + " "
						+ item.getGemNumber() + " "
						+ item.getExtraStats() + "\n";
				
						client.SendData( packetData);
			}
		}
	}

	/****** Manages the char Logout ******/
	public void logout() {

		Client client = Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		System.out.print("Player " + getName() + " logging out...\n");

		DatabaseUtils.getInstance().saveSkills(this);
		DatabaseUtils.getInstance().saveInventory(this);
		DatabaseUtils.getInstance().saveCharStatus(this);
		DatabaseUtils.getInstance().saveEquipment(this);
		DatabaseUtils.getInstance().saveStash(client);
		DatabaseUtils.getInstance().saveExchange(this);
		DatabaseUtils.getInstance().saveQuickSlot(this);

		Iterator<Session> iter = Server.getInstance().getWorldModule()
				.getSessionManager().getSessionListIterator();

		while (iter.hasNext()) {
			Session session = iter.next();

			if (session.contains(this)) {
				session.exit(this);
			}
		}

		if (Server.getInstance().getWorldModule().getPlayerManager()
				.containsPlayer(this)) {
			Server.getInstance().getWorldModule().getPlayerManager()
					.removePlayer(this);
		}

		Server.getInstance().getWorldModule().getSessionManager()
				.removeSession(getSession());
		setSession(null);
	}

	public void loseStamina(int ammount) {
		int newStamina = getCurrStm() - ammount;
		if (newStamina < 2) {
			newStamina = 0;
		}
		updateStatus(2, newStamina, getMaxStm());
		// setCurrStm(newStamina);
		// Client client =
		// S_Server.getInstance().getNetworkModule().getClient(player);
		// S_Server.getInstance().getNetworkModule().SendPacket(client.networkId,"status
		// 2 "+player.getPlayerCurrStm()+" "+player.getPlayerMaxStm());
	}

	public abstract void meleeAttack(LivingObject livingObject);

	/****** Manages the Pick command ******/
	public void pickItem(int uniqueid) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		Item item = (Item) EntityManager.getEntityManager().getEnt(
				uniqueid);
		getInventory().addItem(item);
		InventoryItem invItem = getInventory().getItem(item);
		// S_DatabaseUtils.getInstance().saveInventory(client.getPlayer()Object);

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
		// S> pick [UniqueID] [Type] [Tab] [PosX] [PosY] [GemNumber] [Special]
	}

	/****** Manages the Pickup command ******/
	public void pickupItem(int uniqueid) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		String packetData = "pickup " + getEntityId() + "\n";
		
				client.SendData(packetData);// send the message
		// S> pickup [CharID]

		Server.getInstance().getWorldModule().getWorldCommand()
				.itemOut(this, uniqueid);
		pickItem(uniqueid);
	}

	public void place(int posX, int posY, int posZ, double rotation,
			int unknown, int run) {

		if (run == 1) {
			setRunMode(true);
		} else {
			setRunMode(false);
		}

		getPosition().setX(posX);
		getPosition().setY(posY);
		getPosition().setZ(posZ);
		getPosition().setRotation(rotation);

		setTargetPosX(posX);
		setTargetPosY(posY);
		setTargetPosZ(posZ);

		Iterator<WorldObject> playerIter = getSession()
				.getPlayerListIterator();

		while (playerIter.hasNext()) {
			Player player = (Player) playerIter.next();
			Client client = Server.getInstance().getNetworkModule()
					.getClient(player);

			if (client == null) {
				continue;
			}

			String packetData = "place char " + getEntityId() + " " + posX
					+ " " + posY + " " + posZ + " " + rotation + " "
					+ unknown + " " + run + "\n";
			
					client.SendData(packetData);
		}
		
	}

	/****** revive player when he dies ******/
	public void revive() {

		updateStatus(0, getMaxHp(), getMaxHp());
		
		spawn();
		

		Iterator<Session> sessionIter = Server.getInstance()
				.getWorldModule().getSessionManager().getSessionListIterator();

		while (sessionIter.hasNext()) {
			Session session = sessionIter.next();
			Player player = session.getOwner();

			if (session.contains(this)) {
				session.exit(this);
				getSession().exit(player);
			}

			if (player.getPosition().getMap() != getPosition().getMap() || player == this) {
				continue;
			}

			Client client = Server.getInstance().getNetworkModule()
					.getClient(player);

			if (client == null) {
				continue;
			}

			int distance = player.getDistance(this);

			if (distance <= this.getSessionRadius()) {
				session.enter(this);//TODO: This looks weird
				getSession().enter(player);
			}
		}
	}

	public void say(String text) {
		int admin;
		if (getAdminState() > 0) {
			admin = 1;
		} else {
			admin = 0;
		}
		Iterator<Player> iter = Server.getInstance().getWorldModule()
				.getPlayerManager().getPlayerListIterator();
		while (iter.hasNext()) {
			Player pl = iter.next();
			// if (player.getPlayerSession().contains(pl)||player==pl)
			if (true) {
				Client client = Server.getInstance().getNetworkModule()
						.getClient(pl);
				if (client == null) {
					continue;
				}
				String packetData = "say " + getEntityId() + " " + getName()
						+ " " + text + " " + admin + "\n";
				
						client.SendData(packetData);
				// serverSay(player.getPlayerName()+" says "+text);
			}
		}
	}

	public void setAdminState(int adminState) {
		this.adminState = adminState;
	}

	public void setCharSkill(CharSkill charSkill) {
		this.charSkill = charSkill;
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


	public void setRunMode(boolean runMode) {
		this.runMode = runMode;
	}

	/**
	 * @param playerSession
	 *            The playerSession to set.
	 * @uml.property name="playerSession"
	 */
	public void setSession(Session session) {
		playerSession = session;
	}

	public void setSex(int sex) {
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
	 * public void useSkill(int skillId) { S_Client client =
	 * S_Server.getInstance().getNetworkModule().getClient(this);
	 * 
	 * if(client==null) return;
	 * 
	 * G_Skill skill = getCharSkill().getSkill(skillId);
	 * 
	 * String packetData =
	 * "skill "+skill.getCurrLevel()+" char "+getEntityId()+" "+skillId+"\n";
	 * S_Server.getInstance().getNetworkModule() .SendPacket(client.networkId,
	 * packetData); // S> skill [SkillLevel] char [CharID] [SkillID] }
	 */
	/****** increase skill level ******/
	public void skillUp(int skillId) {

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

		updateStatus(13, -1, 0);
		// S> skilllevel [SkillNumber] [SkillLevel]
	}

	public void social(int emotionId) {

		Iterator<WorldObject> iter = getSession().getPlayerListIterator();
		while (iter.hasNext()) {
			Player player = (Player) iter.next();
			Client client = Server.getInstance().getNetworkModule()
					.getClient(player);

			if (client == null) {
				continue;
			}

			String packetData = "social char " + getEntityId() + " "
					+ emotionId + "\n";
			
					client.SendData(packetData);
		}
		
	}

	public void stop(int posX, int posY, int posZ, double rotation) {

		getPosition().setX(posX);
		getPosition().setY(posY);
		getPosition().setZ(posZ);
		getPosition().setRotation(rotation);

		setTargetPosX(posX);
		setTargetPosY(posY);
		setTargetPosZ(posZ);

		Iterator<WorldObject> playerIter = getSession()
				.getPlayerListIterator();

		while (playerIter.hasNext()) {
			Player player = (Player) playerIter.next();
			Client client = Server.getInstance().getNetworkModule()
					.getClient(player);

			if (client == null) {
				continue;
			}

			String packetData = "s char " + getEntityId() + " " + posX
					+ " " + posY + " " + posZ + " " + rotation + "\n";
			
					client.SendData(packetData);
		}
	
	}

	public void tell(Player targetPlayer, String text) {

		Client client = Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		if (targetPlayer == null) {
			String packetData = "Player not online";
			
					client.SendData(packetData);
			return;
		}

		String packetData = "say 1 " + getName() + " (PM) " + text + " 0\n";
		
				client.SendData(packetData);
	}

	/****** Handles all the Status Updates ******/
	public void updateStatus(int id, int curr, int max) {
		String packetData = new String();
		Client client = Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		switch (id) {
		case 0: { // Hp Status
			if (curr > max) {
				curr = max;
			}
			setCurrHp(curr);
			setMaxHp(max);
			packetData = "status " + id + " " + getCurrHp() + " " + getMaxHp()
					+ "\n";
			
					client.SendData(packetData);
			break;
		}
		case 1: { // Mana Status
			if (curr > max) {
				curr = max;
			}
			setCurrMana(curr);
			setMaxMana(max);
			packetData = "status " + id + " " + getCurrMana() + " "
					+ getMaxMana() + "\n";
			
					client.SendData(packetData);
			break;
		}
		case 2: { // Stamina Status
			if (curr > max) {
				curr = max;
			}
			setCurrStm(curr);
			setMaxStm(max);
			packetData = "status " + id + " " + getCurrStm() + " "
					+ getMaxStm() + "\n";
			
					client.SendData(packetData);
			break;
		}
		case 3: { // Electric Energy Status
			if (curr > max) {
				curr = max;
			}
			setCurrElect(curr);
			setMaxElect(max);
			packetData = "status " + id + " " + getCurrElect() + " "
					+ getMaxElect() + "\n";
			
					client.SendData(packetData);
			break;
		}
		case 4: { // Player Level Status
			setLevel(getLevel() + curr);
			packetData = "status " + id + " " + getLevel() + " " + max + "\n";
			
					client.SendData(packetData);

			packetData = "levelup " + getEntityId() + "\n";
			
					client.SendData(packetData);
			DatabaseUtils.getInstance()
					.updateCharStatus(this, id, getLevel());

			Iterator<WorldObject> playerIter = getSession()
					.getPlayerListIterator();

			while (playerIter.hasNext()) {
				Player pl = (Player) playerIter.next();
				client = Server.getInstance().getNetworkModule()
						.getClient(pl);

				if (client == null) {
					continue;
				}

				
						client.SendData(packetData);
			}
			break;
		}
		case 10: { // Player Lime Status
			setLime(getLime() + curr);
			packetData = "status " + id + " " + getLime() + " " + max + "\n";
			
					client.SendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getLime());
			break;
		}
		case 11: { // Player Total Exp Status
			setTotalExp(getTotalExp() + curr);
			packetData = "status " + id + " " + getTotalExp() + " " + max
					+ "\n";
			
					client.SendData(packetData);
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
				
						client.SendData(packetData);
				DatabaseUtils.getInstance().updateCharStatus(this, id,
						getLvlUpExp());
			} else {
				setLvlUpExp(curr);
				packetData = "status " + id + " " + getLvlUpExp() + " " + max
						+ "\n";
				
						client.SendData(packetData);
				DatabaseUtils.getInstance().updateCharStatus(this, id,
						getLvlUpExp());
			}
			break;
		}
		case 13: { // Player Distribution Status Points
			setStatusPoints(getStatusPoints() + curr);
			packetData = "status " + id + " " + getStatusPoints() + " " + max
					+ "\n";
			
					client.SendData(packetData);
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
			
					client.SendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getStr());

			updateStatus(0, getCurrHp(), getMaxHp() + (getStr() / 50) + 1);
			updateStatus(2, getCurrStm(), getMaxStm() + (getStr() / 60) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 15: { // Player Wisdom Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setWisdom(getWis() + curr);
			packetData = "status " + id + " " + getWis() + " " + max + "\n";
			
					client.SendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getWis());

			updateStatus(1, getCurrMana(), getMaxMana() + (getWis() / 50) + 2);
			updateStatus(3, getCurrElect(), getMaxElect() + (getWis() / 50) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 16: { // Player Dex Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setDexterity(getDexterity() + curr);
			packetData = "status " + id + " " + getDexterity() + " " + max + "\n";
			
					client.SendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getDexterity());

			updateStatus(1, getCurrMana(), getMaxMana() + (getDexterity() / 50) + 1);
			updateStatus(3, getCurrElect(), getMaxElect() + (getDexterity() / 50) + 2);
			updateStatus(13, -1, 0);
			break;
		}
		case 17: { // Player Strain Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setConstitution(getConstitution() + curr);
			packetData = "status " + id + " " + getConstitution() + " " + max + "\n";
			
					client.SendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getConstitution());

			updateStatus(0, getCurrHp(), getMaxHp() + (getConstitution() / 50) + 2);
			updateStatus(2, getCurrStm(), getMaxStm() + (getConstitution() / 50) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 18: { // Player Charisma Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setLeadership(getLeadership() + curr);
			packetData = "status " + id + " " + getLeadership() + " " + max + "\n";
			
					client.SendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id, getLeadership());

			if (getLeadership() % 2 == 0) {
				updateStatus(0, getCurrHp(), getMaxHp() + 1);
				updateStatus(1, getCurrMana(), getMaxMana() + 1);
				updateStatus(2, getCurrStm(), getMaxStm() + 1);
				updateStatus(3, getCurrElect(), getMaxElect() + 1);
			}
			updateStatus(13, -1, 0);
			break;
		}
		case 19: { // Player Penalty Points Status ([inGame=packet] -> 100=10;
					// 1000=100; 10000=1000)
			packetData = "status " + id + " " + curr + " " + max + "\n";
			
					client.SendData(packetData);
			DatabaseUtils.getInstance().updateCharStatus(this, id,
					getPenaltyPoints());
			break;
		}
		}
		// S> status [StatusConstant] [Current] [Max]

		// S_DatabaseUtils.getInstance().saveCharStatus(this);
	}

	public void walk(int posX, int posY, int posZ, int run) {

		if (run == 1) {
			setRunMode(true);
		} else {
			setRunMode(false);
		}

		getPosition().setX(posX);
		getPosition().setY(posY);
		getPosition().setZ(posZ);

		setTargetPosX(posX);
		setTargetPosY(posY);
		setTargetPosZ(posZ);

		Iterator<WorldObject> playerIter = getSession()
				.getPlayerListIterator();

		while (playerIter.hasNext()) {
			Player player = (Player) playerIter.next();
			Client client = Server.getInstance().getNetworkModule()
					.getClient(player);

			if (client == null) {
				continue;
			}

			String packetData = "walk char " + getEntityId() + " " + posX
					+ " " + posY + " " + posZ + " " + run + "\n";
			
					client.SendData(packetData);
		}
		
	}

	public void wearSlot(G_EquipmentSlot slot) {
	
		InventoryItem invItem = getInventory().getItemSelected();
		String packetData = new String();
		String extraPacketData = null;

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

			packetData = "char_remove " + getEntityId() + " " + slot + "\n";
		} else {
			if (getEquipment().getItem(slot) == null) {
				Item item = invItem.getItem();
				packetData = "char_wear " + getEntityId() + " " + slot + " "
						+ item.getType() + " " + item.getGemNumber() + "\n";
				getEquipment().setItem(slot, item);
				getInventory().setItemSelected(null);
				if (getEquipment().getItem(slot) instanceof Weapon) {
					Weapon weapon = (Weapon) getEquipment().getItem(slot);
					setMinDmg(weapon.getMinDamage());
					setMaxDmg(weapon.getMaxDamage());
				}
			} else {
				Item currentItem = getEquipment().getItem(slot);
				extraPacketData = "char_remove " + getEntityId() + " " + slot
						+ "\n";
				getEquipment().setItem(slot, invItem.getItem());
				getInventory().setItemSelected(
						new InventoryItem(currentItem, 0, 0, 0));
				if (getEquipment().getItem(slot) instanceof Weapon) {
					Weapon weapon = (Weapon) getEquipment().getItem(slot);
					setMinDmg(weapon.getMinDamage());
					setMaxDmg(weapon.getMaxDamage());
				}
				Item item = getEquipment().getItem(slot);
				packetData = "char_wear " + getEntityId() + " " + slot + " "
						+ item.getType() + " " + item.getGemNumber() + "\n";
				
			}

		}
		// S_DatabaseUtils.getInstance().saveEquipment(this);

		Iterator<WorldObject> playerIter = getSession()
				.getPlayerListIterator();

		while (playerIter.hasNext()) {
			Player player = (Player) playerIter.next();
			Client client = Server.getInstance().getNetworkModule()
					.getClient(player);

			if (client == null) {
				continue;
			}
			if (extraPacketData != null) {
				
						client.SendData(extraPacketData);
			}
			
					client.SendData(packetData);
		}
		
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
		Server.getInstance().getWorldModule().getWorldCommand()
		.charIn(session.getOwner(), this);
	}
		

	@Override
	public void exit(Session session){
		Server.getInstance().getWorldModule().getWorldCommand()
		.charOut(session.getOwner(), this);
	}
	
	

}
