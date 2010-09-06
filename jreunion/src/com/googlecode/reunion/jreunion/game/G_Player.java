package com.googlecode.reunion.jreunion.game;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.server.S_CharSkill;
import com.googlecode.reunion.jreunion.server.S_Client;
import com.googlecode.reunion.jreunion.server.S_DatabaseUtils;
import com.googlecode.reunion.jreunion.server.S_ParsedItem;
import com.googlecode.reunion.jreunion.server.S_Parser;
import com.googlecode.reunion.jreunion.server.S_Reference;
import com.googlecode.reunion.jreunion.server.S_Server;
import com.googlecode.reunion.jreunion.server.S_Session;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class G_Player extends G_LivingObject implements G_SkillTarget {

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

	private int race; // 0 - Bulkan; 1 - Kailipton; 2 - Aidia; 3 - Human;
						// 4 - Hybrider

	private int sex; // 0 - Male; 1 - Female

	private int speed;

	private int statusPoints;

	private G_Inventory inventory;

	private String name;

	private int penaltyPoints;

	private boolean runMode; // 0 - Off; 1 - On

	private int adminState; // 0 - normal user; 255 - SuperGM

	private S_Session playerSession;

	private int hairStyle;

	private G_Equipment equipment;

	private List<Integer> attackQueue = new Vector<Integer>();

	private S_CharSkill charSkill;

	private G_QuickSlot quickSlot;

	private G_Quest quest;

	private G_Stash stash;

	private G_Exchange exchange;

	private int guildId;

	private int guildLvl;

	public G_Player() {
		super();
		inventory = new G_Inventory();
		equipment = new G_Equipment();
		charSkill = new S_CharSkill();
		quickSlot = new G_QuickSlot();
		stash = new G_Stash();
		exchange = new G_Exchange();
		// setPlayerMinDmg(325);
		// setPlayerMaxDmg(370);
	}

	public void addAttack(int attack) {
		if (attackQueue.size() >= 5) {
			attackQueue.remove(0);
		}
		attackQueue.add(attack);
	}

	// public abstract int getBaseDmg(G_LivingObject livingObject);

	public void charCombat(int combat) {

		if (combat == 1) {
			setCombatMode(true);
		} else {
			setCombatMode(false);
		}

		if (getSession().getPlayerListSize() > 0) {
			Iterator<G_Player> playerIter = getSession()
					.getPlayerListIterator();

			while (playerIter.hasNext()) {
				G_Player player = playerIter.next();
				S_Client client = S_Server.getInstance().getNetworkModule()
						.getClient(player);

				if (client == null) {
					continue;
				}

				String packetData = "combat " + getEntityId() + " " + combat
						+ "\n";
						client.SendData( packetData);
			}
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
			G_Item item = getInventory().getItemSelected().getItem();
	
			if (item == null) {
				return;
			}
		
			S_Client client = S_Server.getInstance().getNetworkModule()
					.getClient(this);
			if (client == null) {
				return;
			}
			getInventory().setItemSelected(null);
			if (item != null) {
				String packetData = "drop " + item.getEntityId() + " " + item.getType()
						+ " " + getPosX() + " " + getPosY() + " " + getPosZ() + " "
						+ getRotation() + " " + item.getGemNumber() + " "
						+ item.getExtraStats() + "\n";
				System.out.println(packetData);
				client.SendData(packetData);
	
				if (getSession().getPlayerListSize() > 0) {
					Iterator<G_Player> playerIter = getSession()
							.getPlayerListIterator();
	
					while (playerIter.hasNext()) {
						G_Player player = playerIter.next();
						client = S_Server.getInstance().getNetworkModule()
								.getClient(player);
	
						if (client == null) {
							continue;
						}
	
						client.SendData( packetData);
					}
				}
			}
			
			// S> drop [ItemID] [ItemType] [PosX] [PosY] [Height] [Rotation]
			// [GemNumber] [Special]
		} catch (Exception e) {
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

	public S_CharSkill getCharSkill() {
		return charSkill;
	}

	public boolean getCombatMode() {
		return combatMode;
	}

	public int getCons() {
		return cons;
	}

	public int getDef() {
		return def;
	}

	public int getDex() {
		return dex;
	}

	/*** Return the distance between the player and the living object ***/
	public int getDistance(G_LivingObject livingObject) {
		double xcomp = Math.pow(livingObject.getPosX() - getPosX(), 2);
		double ycomp = Math.pow(livingObject.getPosY() - getPosY(), 2);
		double distance = Math.sqrt(xcomp + ycomp);

		return (int) distance;
	}

	public G_Equipment getEquipment() {
		return equipment;
	}

	public G_Exchange getExchange() {
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

	public G_Inventory getInventory() {
		return inventory;
	}

	public int getLead() {
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

	public G_Quest getQuest() {
		return quest;
	}

	public G_QuickSlot getQuickSlot() {
		return quickSlot;
	}

	public int getRace() {
		return race;
	}

	public void getRespawnCoords(int mapId) {
		S_Parser playerSpawns = getMap().getPlayerSpawnReference();
		Iterator<S_ParsedItem> iter = playerSpawns.getItemListIterator();

		while (iter.hasNext()) {
			S_ParsedItem i = iter.next();

			if (i.getName().equals("Default")) {
				S_Server.getInstance()
						.getWorldModule()
						.getWorldCommand()
						.GoToPos(
								this,
								Integer.parseInt(i.getMemberValue("respawnx"))
										+ (int) (Integer.parseInt(i
												.getMemberValue("respawnwidth")) * Math
												.random()),
								Integer.parseInt(i.getMemberValue("respawny"))
										+ (int) (Integer.parseInt(i
												.getMemberValue("respawnheight")) * Math
												.random()));
				return;
			} else {
				Rectangle rectangle = new Rectangle(Integer.parseInt(i
						.getMemberValue("targetx")), Integer.parseInt(i
						.getMemberValue("targety")), Integer.parseInt(i
						.getMemberValue("targetwidth")), Integer.parseInt(i
						.getMemberValue("targetheight")));

				if (rectangle.contains(getPosX(), getPosY())) {
					S_Server.getInstance()
							.getWorldModule()
							.getWorldCommand()
							.GoToPos(
									this,
									Integer.parseInt(i
											.getMemberValue("respawnx"))
											+ (int) (Integer.parseInt(i
													.getMemberValue("respawnwidth")) * Math
													.random()),
									Integer.parseInt(i
											.getMemberValue("respawny"))
											+ (int) (Integer.parseInt(i
													.getMemberValue("respawnheight")) * Math
													.random()));
					return;
				}
			}
		}
	}

	public boolean getRunMode() {
		return runMode;
	}

	/**
	 * @return Returns the playerSession.
	 * @uml.property name="playerSession"
	 */
	public S_Session getSession() {
		return playerSession;
	}

	public int getSex() {
		return sex;
	}

	public int getSpeed() {
		return speed;
	}

	public G_Stash getStash() {
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

	/*** Manages the Items add/Remove from Trade Box ***/
	public void itemExchange(int posX, int posY) {

		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		if (getInventory().getItemSelected() == null) {
			G_ExchangeItem item = getExchange().getItem(posX, posY);

			if (item == null) {
				return;
			}

			G_InventoryItem invItem = new G_InventoryItem(item.getItem(), 0, 0,
					0);

			getInventory().setItemSelected(invItem);
			getExchange().removeItem(item);
		} else {
			G_Item item = getInventory().getItemSelected().getItem();
			G_ExchangeItem newExchangeItem = new G_ExchangeItem(item, posX,
					posY);
			G_ExchangeItem oldExchangeItem = null;
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
				G_InventoryItem invItem = new G_InventoryItem(
						oldExchangeItem.getItem(), 0, 0, 0);
				getInventory().setItemSelected(invItem);
				getExchange().removeItem(oldExchangeItem);
			}
			getExchange().addItem(newExchangeItem);
		}
	}

	/****** Load Items at Trade Box ******/
	public void loadExchange() {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		Iterator<G_ExchangeItem> exchangeIter = getExchange()
				.itemListIterator();
		while (exchangeIter.hasNext()) {
			G_ExchangeItem exchangeItem = exchangeIter.next();

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
		S_ParsedItem exp = S_Reference.getInstance().getExpReference()
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
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		Iterator<G_InventoryItem> invIter = getInventory()
				.getInventoryIterator();
		while (invIter.hasNext()) {
			G_InventoryItem invItem = invIter.next();

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
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		Iterator<G_QuickSlotItem> quickSlot = getQuickSlot()
				.getQuickSlotIterator();
		while (quickSlot.hasNext()) {
			G_QuickSlotItem qsItem = quickSlot.next();
			G_Item item = qsItem.getItem();
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

		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		System.out.print("Player " + getName() + " logging out...\n");

		S_DatabaseUtils.getInstance().saveInventory(this);
		S_DatabaseUtils.getInstance().saveCharStatus(this);
		S_DatabaseUtils.getInstance().saveEquipment(this);
		S_DatabaseUtils.getInstance().saveStash(client);
		S_DatabaseUtils.getInstance().saveExchange(this);
		S_DatabaseUtils.getInstance().saveQuickSlot(this);

		Iterator<S_Session> iter = S_Server.getInstance().getWorldModule()
				.getSessionManager().getSessionListIterator();

		while (iter.hasNext()) {
			S_Session session = iter.next();

			if (session.contains(this)) {
				session.exitPlayer(this);
			}
		}

		if (S_Server.getInstance().getWorldModule().getPlayerManager()
				.containsPlayer(this)) {
			S_Server.getInstance().getWorldModule().getPlayerManager()
					.removePlayer(this);
		}

		S_Server.getInstance().getWorldModule().getSessionManager()
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

	public abstract void meleeAttack(G_LivingObject livingObject);

	/****** Manages the Pick command ******/
	public void pickItem(int uniqueid) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		G_Item item = (G_Item) G_EntityManager.getEntityManager().getEnt(
				uniqueid);
		getInventory().addItem(item);
		G_InventoryItem invItem = getInventory().getItem(item);
		// S_DatabaseUtils.getInstance().saveInventory(client.playerObject);

		if (invItem == null) {
			getInventory().setItemSelected(new G_InventoryItem(item, 0, 0, 0));
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
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(this);

		if (client == null) {
			return;
		}

		String packetData = "pickup " + getEntityId() + "\n";
		
				client.SendData(packetData);// send the message
		// S> pickup [CharID]

		S_Server.getInstance().getWorldModule().getWorldCommand()
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

		setPosX(posX);
		setPosY(posY);
		setPosZ(posZ);
		setRotation(rotation);

		setTargetPosX(posX);
		setTargetPosY(posY);
		setTargetPosZ(posZ);

		if (getSession().getPlayerListSize() > 0) {
			Iterator<G_Player> playerIter = getSession()
					.getPlayerListIterator();

			while (playerIter.hasNext()) {
				G_Player player = playerIter.next();
				S_Client client = S_Server.getInstance().getNetworkModule()
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
	}

	/****** revive player when he dies ******/
	public void revive() {

		updateStatus(0, getMaxHp(), getMaxHp());
		switch (getMap().getMapId()) {
		case 4: {
			getRespawnCoords(4);
			break;
		}
		default: {
			getRespawnCoords(-1);
			break;
		}
		}

		Iterator<S_Session> sessionIter = S_Server.getInstance()
				.getWorldModule().getSessionManager().getSessionListIterator();

		while (sessionIter.hasNext()) {
			S_Session session = sessionIter.next();
			G_Player player = session.getSessionOwner();

			if (session.contains(this)) {
				session.exitPlayer(this);
				getSession().exitPlayer(player);
			}

			if (player.getMap() != getMap() || player == this) {
				continue;
			}

			S_Client client = S_Server.getInstance().getNetworkModule()
					.getClient(player);

			if (client == null) {
				continue;
			}

			int distance = player.getDistance(this);

			if (distance <= S_DatabaseUtils.getInstance().getSessionRadius()) {
				session.enterPlayer(this, 1);
				getSession().enterPlayer(player, 0);
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
		Iterator<G_Player> iter = S_Server.getInstance().getWorldModule()
				.getPlayerManager().getPlayerListIterator();
		while (iter.hasNext()) {
			G_Player pl = iter.next();
			// if (player.getPlayerSession().contains(pl)||player==pl)
			if (true) {
				S_Client client = S_Server.getInstance().getNetworkModule()
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

	public void setCharSkill(S_CharSkill charSkill) {
		this.charSkill = charSkill;
	}

	public void setCombatMode(boolean combatMode) {
		this.combatMode = combatMode;
	}

	public void setCons(int cons) {
		this.cons = cons;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public void setDex(int dex) {
		this.dex = dex;
	}

	public void setEquipment(G_Equipment equipment) {
		this.equipment = equipment;
	}

	public void setExchange(G_Exchange exchange) {
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

	public void setInventory(G_Inventory inventory) {
		this.inventory = inventory;
	}

	public void setLead(int lead) {
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

	public void setQuest(G_Quest quest) {
		this.quest = quest;
	}

	public void setQuickSlot(G_QuickSlot quickSlot) {
		this.quickSlot = quickSlot;
	}

	public void setRace(int race) {
		this.race = race;
	}

	public void setRunMode(boolean runMode) {
		this.runMode = runMode;
	}

	/**
	 * @param playerSession
	 *            The playerSession to set.
	 * @uml.property name="playerSession"
	 */
	public void setSession(S_Session session) {
		playerSession = session;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setStash(G_Stash stash) {
		this.stash = stash;
	}

	public void setStatusPoints(int statusPoints) {
		this.statusPoints = statusPoints;
	}

	public void setStr(int str) {
		this.str = str;
	}

	public void setTotalExp(int totalExp) {
		this.totalExp = totalExp;
	}

	public void setWis(int wis) {
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

		if (getSession().getPlayerListSize() > 0) {
			Iterator<G_Player> iter = getSession().getPlayerListIterator();
			while (iter.hasNext()) {
				G_Player player = iter.next();
				S_Client client = S_Server.getInstance().getNetworkModule()
						.getClient(player);

				if (client == null) {
					continue;
				}

				String packetData = "social char " + getEntityId() + " "
						+ emotionId + "\n";
				
						client.SendData(packetData);
			}
		}
	}

	public void stop(int posX, int posY, int posZ, double rotation) {

		setPosX(posX);
		setPosY(posY);
		setPosZ(posZ);
		setRotation(rotation);

		setTargetPosX(posX);
		setTargetPosY(posY);
		setTargetPosZ(posZ);

		if (getSession().getPlayerListSize() > 0) {
			Iterator<G_Player> playerIter = getSession()
					.getPlayerListIterator();

			while (playerIter.hasNext()) {
				G_Player player = playerIter.next();
				S_Client client = S_Server.getInstance().getNetworkModule()
						.getClient(player);

				if (client == null) {
					continue;
				}

				String packetData = "s char " + getEntityId() + " " + posX
						+ " " + posY + " " + posZ + " " + rotation + "\n";
				
						client.SendData(packetData);
			}
		}
	}

	public void tell(G_Player targetPlayer, String text) {

		S_Client client = S_Server.getInstance().getNetworkModule()
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
		S_Client client = S_Server.getInstance().getNetworkModule()
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
			S_DatabaseUtils.getInstance()
					.updateCharStatus(this, id, getLevel());

			if (getSession().getPlayerListSize() > 0) {
				Iterator<G_Player> playerIter = getSession()
						.getPlayerListIterator();

				while (playerIter.hasNext()) {
					G_Player pl = playerIter.next();
					client = S_Server.getInstance().getNetworkModule()
							.getClient(pl);

					if (client == null) {
						continue;
					}

					
							client.SendData(packetData);
				}
			}
			break;
		}
		case 10: { // Player Lime Status
			setLime(getLime() + curr);
			packetData = "status " + id + " " + getLime() + " " + max + "\n";
			
					client.SendData(packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this, id, getLime());
			break;
		}
		case 11: { // Player Total Exp Status
			setTotalExp(getTotalExp() + curr);
			packetData = "status " + id + " " + getTotalExp() + " " + max
					+ "\n";
			
					client.SendData(packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this, id,
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
				S_DatabaseUtils.getInstance().updateCharStatus(this, id,
						getLvlUpExp());
			} else {
				setLvlUpExp(curr);
				packetData = "status " + id + " " + getLvlUpExp() + " " + max
						+ "\n";
				
						client.SendData(packetData);
				S_DatabaseUtils.getInstance().updateCharStatus(this, id,
						getLvlUpExp());
			}
			break;
		}
		case 13: { // Player Distribution Status Points
			setStatusPoints(getStatusPoints() + curr);
			packetData = "status " + id + " " + getStatusPoints() + " " + max
					+ "\n";
			
					client.SendData(packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this, id,
					getStatusPoints());
			break;
		}
		case 14: { // Player Strenght Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setStr(getStr() + curr);
			packetData = "status " + id + " " + getStr() + " " + max + "\n";
			
					client.SendData(packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this, id, getStr());

			updateStatus(0, getCurrHp(), getMaxHp() + (getStr() / 50) + 1);
			updateStatus(2, getCurrStm(), getMaxStm() + (getStr() / 60) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 15: { // Player Wisdom Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setWis(getWis() + curr);
			packetData = "status " + id + " " + getWis() + " " + max + "\n";
			
					client.SendData(packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this, id, getWis());

			updateStatus(1, getCurrMana(), getMaxMana() + (getWis() / 50) + 2);
			updateStatus(3, getCurrElect(), getMaxElect() + (getWis() / 50) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 16: { // Player Dex Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setDex(getDex() + curr);
			packetData = "status " + id + " " + getDex() + " " + max + "\n";
			
					client.SendData(packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this, id, getDex());

			updateStatus(1, getCurrMana(), getMaxMana() + (getDex() / 50) + 1);
			updateStatus(3, getCurrElect(), getMaxElect() + (getDex() / 50) + 2);
			updateStatus(13, -1, 0);
			break;
		}
		case 17: { // Player Strain Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setCons(getCons() + curr);
			packetData = "status " + id + " " + getCons() + " " + max + "\n";
			
					client.SendData(packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this, id, getCons());

			updateStatus(0, getCurrHp(), getMaxHp() + (getCons() / 50) + 2);
			updateStatus(2, getCurrStm(), getMaxStm() + (getCons() / 50) + 1);
			updateStatus(13, -1, 0);
			break;
		}
		case 18: { // Player Charisma Status
			if (getStatusPoints() <= 0) {
				return;
			}
			setLead(getLead() + curr);
			packetData = "status " + id + " " + getLead() + " " + max + "\n";
			
					client.SendData(packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this, id, getLead());

			if (getLead() % 2 == 0) {
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
			S_DatabaseUtils.getInstance().updateCharStatus(this, id,
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

		setPosX(posX);
		setPosY(posY);
		setPosZ(posZ);

		setTargetPosX(posX);
		setTargetPosY(posY);
		setTargetPosZ(posZ);

		if (getSession().getPlayerListSize() > 0) {
			Iterator<G_Player> playerIter = getSession()
					.getPlayerListIterator();

			while (playerIter.hasNext()) {
				G_Player player = playerIter.next();
				S_Client client = S_Server.getInstance().getNetworkModule()
						.getClient(player);

				if (client == null) {
					continue;
				}

				String packetData = "walk char " + getEntityId() + " " + posX
						+ " " + posY + " " + posZ + " " + run + "\n";
				
						client.SendData(packetData);
			}
		}
	}

	public void wearSlot(int slotid) {
	try {
		G_InventoryItem invItem = getInventory().getItemSelected();
		String packetData = new String();
		String extraPacketData = null;

		if (invItem == null) {
			if (getEquipment().getItem(slotid) instanceof G_Weapon) {
				setMinDmg(1);
				setMaxDmg(2);
			}

			getInventory()
					.setItemSelected(
							new G_InventoryItem(getEquipment().getItem(slotid),
									0, 0, 0));
			getEquipment().setItem(slotid, null);

			packetData = "char_remove " + getEntityId() + " " + slotid + "\n";
		} else {
			if (getEquipment().getItem(slotid) == null) {
				G_Item item = invItem.getItem();
				packetData = "char_wear " + getEntityId() + " " + slotid + " "
						+ item.getType() + " " + item.getGemNumber() + "\n";
				getEquipment().setItem(slotid, item);
				getInventory().setItemSelected(null);
				if (getEquipment().getItem(slotid) instanceof G_Weapon) {
					G_Weapon weapon = (G_Weapon) getEquipment().getItem(slotid);
					setMinDmg(weapon.getMinDamage());
					setMaxDmg(weapon.getMaxDamage());
				}
			} else {
				G_Item currentItem = getEquipment().getItem(slotid);
				extraPacketData = "char_remove " + getEntityId() + " " + slotid
						+ "\n";
				getEquipment().setItem(slotid, invItem.getItem());
				getInventory().setItemSelected(
						new G_InventoryItem(currentItem, 0, 0, 0));
				if (getEquipment().getItem(slotid) instanceof G_Weapon) {
					G_Weapon weapon = (G_Weapon) getEquipment().getItem(slotid);
					setMinDmg(weapon.getMinDamage());
					setMaxDmg(weapon.getMaxDamage());
				}
				G_Item item = getEquipment().getItem(slotid);
				packetData = "char_wear " + getEntityId() + " " + slotid + " "
						+ item.getType() + " " + item.getGemNumber() + "\n";
				
			}

		}
		// S_DatabaseUtils.getInstance().saveEquipment(this);

		if (getSession().getPlayerListSize() > 0) {
			Iterator<G_Player> playerIter = getSession()
					.getPlayerListIterator();

			while (playerIter.hasNext()) {
				G_Player player = playerIter.next();
				S_Client client = S_Server.getInstance().getNetworkModule()
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
	
	} catch (Exception e) {
		System.out.println("wearslot bug");
	}
	}

}
