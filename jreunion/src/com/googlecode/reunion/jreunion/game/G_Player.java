package com.googlecode.reunion.jreunion.game;

import java.awt.Rectangle;
import java.util.*;

import com.googlecode.reunion.jreunion.server.*;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class G_Player extends G_LivingObject implements G_SkillTarget{
	
	private int def=0;

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
		//setPlayerMinDmg(325);
		//setPlayerMaxDmg(370);
	}
	
	public abstract void meleeAttack(G_LivingObject livingObject);
	
	//public abstract int getBaseDmg(G_LivingObject livingObject);
	
	public void setDef(int def) {
		this.def = def;
	}

	public int getDef() {
		return this.def;
	}

	public void setMinDmg(int minDmg) {
		this.minDmg = minDmg;
	}

	public int getMinDmg() {
		return this.minDmg;
	}

	public void setMaxDmg(int maxDmg) {
		this.maxDmg = maxDmg;
	}

	public int getMaxDmg() {
		return this.maxDmg;
	}

	public void setTotalExp(int totalExp) {
		this.totalExp = totalExp;
	}

	public int getTotalExp() {
		return this.totalExp;
	}

	public void setLvlUpExp(int lvlUpExp) {
		this.lvlUpExp = lvlUpExp;
	}

	public int getLvlUpExp() {
		return this.lvlUpExp;
	}

	public void setLime(int lime) {
		this.lime = lime;
	}

	public int getLime() {
		return this.lime;
	}

	public void setCombatMode(boolean combatMode) {
		this.combatMode = combatMode;
	}

	public boolean getCombatMode() {
		return this.combatMode;
	}

	public void setStr(int str) {
		this.str = str;
	}

	public int getStr() {
		return this.str;
	}

	public void setWis(int wis) {
		this.wis = wis;
	}

	public int getWis() {
		return this.wis;
	}

	public void setDex(int dex) {
		this.dex = dex;
	}

	public int getDex() {
		return this.dex;
	}

	public void setCons(int cons) {
		this.cons = cons;
	}

	public int getCons() {
		return this.cons;
	}

	public void setLead(int lead) {
		this.lead = lead;
	}

	public int getLead() {
		return this.lead;
	}

	public void setRace(int race) {
		this.race = race;
	}

	public int getRace() {
		return this.race;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getSex() {
		return this.sex;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getSpeed() {
		return this.speed;
	}

	public void setStatusPoints(int statusPoints) {
		this.statusPoints = statusPoints;
	}

	public int getStatusPoints() {
		return this.statusPoints;
	}

	public void setInventory(G_Inventory inventory) {
		this.inventory = inventory;
	}

	public G_Inventory getInventory() {
		return this.inventory;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setPenaltyPoints(int penaltyPoints) {
		this.penaltyPoints = penaltyPoints;
	}

	public int getPenaltyPoints() {
		return this.penaltyPoints;
	}

	public void setRunMode(boolean runMode) {
		this.runMode = runMode;
	}

	public boolean getRunMode() {
		return this.runMode;
	}

	public void setAdminState(int adminState) {
		this.adminState = adminState;
	}

	public int getAdminState() {
		return this.adminState;
	}

	/**
	 * @param playerSession
	 *            The playerSession to set.
	 * @uml.property name="playerSession"
	 */
	public void setSession(S_Session session) {
		this.playerSession = session;
	}

	/**
	 * @return Returns the playerSession.
	 * @uml.property name="playerSession"
	 */
	public S_Session getSession() {
		return this.playerSession;
	}

	public void setHairStyle(int hairStyle) {
		this.hairStyle = hairStyle;
	}

	public int getHairStyle() {
		return this.hairStyle;
	}
	public void setEquipment(G_Equipment equipment) {
		this.equipment = equipment;
	}

	public G_Equipment getEquipment() {
		return this.equipment;
	}
	public void setCharSkill(S_CharSkill charSkill) {
		this.charSkill = charSkill;
	}

	public S_CharSkill getCharSkill() {
		return this.charSkill;
	}

	public void setQuickSlot(G_QuickSlot quickSlot) {
		this.quickSlot = quickSlot;
	}
	public G_QuickSlot getQuickSlot() {
		return this.quickSlot;
	}
	
	public void setStash(G_Stash stash) {
		this.stash = stash;
	}
	public G_Stash getStash() {
		return this.stash;
	}
	
	public void setExchange(G_Exchange exchange) {
		this.exchange = exchange;
	}
	public G_Exchange getExchange() {
		return this.exchange;
	}
	
	public void addAttack(int attack){
		if(attackQueue.size()>=5)
			attackQueue.remove(0);
		attackQueue.add(attack);
	}
	public int getBestAttack(){
		int bestAttack=0;
		Iterator<Integer> iter = getAttackQueueIterator();
		
		while(iter.hasNext()){
			int count=1;
			int attack = iter.next();

			if(bestAttack < attack)
				bestAttack = attack;
			count++;
		}
		return bestAttack;
	}
	public void clearAttackQueue(){
		for(int i=0; i<attackQueue.size(); i++)
			attackQueue.remove(i);
	}
	public Iterator<Integer> getAttackQueueIterator() {
		return attackQueue.iterator();
	}
	
	public void setQuest(G_Quest quest){
		this.quest = quest; 
	}
	public G_Quest getQuest(){
		return this.quest;
	}
	
	public void setGuildId(int guildId){
		this.guildId = guildId; 
	}
	public int getGuildId(){
		return this.guildId;
	}
	
	public void setGuildLvl(int guildLvl){
		this.guildLvl = guildLvl; 
	}
	public int getGuildLvl(){
		return this.guildLvl;
	}
	
	public void walk(int posX, int posY, int posZ,int run) {

		if (run == 1) this.setRunMode(true);
		else this.setRunMode(false);
				
		setPosX(posX);
		setPosY(posY);
		setPosZ(posZ);

		setTargetPosX(posX);
		setTargetPosY(posY);
		setTargetPosZ(posZ);
		
		if (getSession().getPlayerListSize() > 0){
			Iterator<G_Player> playerIter = this.getSession().getPlayerListIterator();
			
			while(playerIter.hasNext()){
				G_Player player = playerIter.next();
				S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
				
				if (client == null)
					continue;
				
				String packetData = "walk char " + getEntityId()+ " " 
									+ posX + " " + posY + " "+posZ+" " + run + "\n";
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId,packetData);
			}
		}
	}

	public void place(int posX, int posY, int posZ,double rotation, int unknown, int run) {
		
		if (run == 1) this.setRunMode(true);
		else this.setRunMode(false);
		
		setPosX(posX);
		setPosY(posY);
		setPosZ(posZ);
		setRotation(rotation);
		
		setTargetPosX(posX);
		setTargetPosY(posY);
		setTargetPosZ(posZ);
		
		if (getSession().getPlayerListSize() > 0){
			Iterator<G_Player> playerIter = this.getSession().getPlayerListIterator();
			
			while(playerIter.hasNext()){
				G_Player player = playerIter.next();
				S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
				
				if (client == null)
					continue;
				
				String packetData = "place char " + getEntityId() + " " + posX + " "
									+ posY + " "+posZ+" "+rotation+" " + unknown + " " + run + "\n";
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId,packetData);
			}
		}
	}
	
	public void stop(int posX, int posY, int posZ,double rotation) {
		
		setPosX(posX);
		setPosY(posY);
		setPosZ(posZ);
		setRotation(rotation);
		
		setTargetPosX(posX);
		setTargetPosY(posY);
		setTargetPosZ(posZ);
		
		if (getSession().getPlayerListSize() > 0){
			Iterator<G_Player> playerIter = this.getSession().getPlayerListIterator();
			
			while(playerIter.hasNext()){
				G_Player player = playerIter.next();
				S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
				
				if (client == null)
					continue;
				
				String packetData = "s char " +getEntityId()+ " "+posX+" "+posY+" "
									+posZ+" "+rotation+"\n";
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId,packetData);
			}
		}
	}
	
	public void loseStamina(int ammount) {
		int newStamina = getCurrStm() - ammount;
		if (newStamina < 2) {
			newStamina = 0;
		}
		updateStatus(2,newStamina,getMaxStm());
		//setCurrStm(newStamina);
		// Client client = S_Server.getInstance().getNetworkModule().getClient(player);
		// S_Server.getInstance().getNetworkModule().SendPacket(client.networkId,"status
		// 2 "+player.getPlayerCurrStm()+" "+player.getPlayerMaxStm());
	}

	public void say(String text) {
		int admin;
		if (getAdminState() > 0)
			admin = 1;
		else
			admin = 0;
		Iterator<G_Player> iter = S_Server.getInstance().getWorldModule().getPlayerManager().getPlayerListIterator();
		while (iter.hasNext()) {
			G_Player pl = iter.next();
			// if (player.getPlayerSession().contains(pl)||player==pl)
			if (true) {
				S_Client client = S_Server.getInstance().getNetworkModule()
						.getClient(pl);
				if (client == null)
					continue;
				int networkId = client.networkId;
				String packetData = "say "+getEntityId()+" " + getName() + " "
				+ text + " " + admin + "\n";
				S_Server.getInstance().getNetworkModule().SendPacket(networkId,
						packetData);
				// serverSay(player.getPlayerName()+" says "+text);
			}
		}
	}
	public void tell(G_Player targetPlayer, String text) {

		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
		
		if (client == null)
			return;

		if (targetPlayer == null) {
			String packetData = "Player not online";
			S_Server.getInstance().getNetworkModule().SendPacket(client.networkId,packetData);
			return;
		}

		String packetData = "say 1 " + getName() + " (PM) " + text+ " 0\n";
		S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
	}
	
	public void charCombat(int combat) {
						
		if (combat == 1) 
			setCombatMode(true);
		else 
			setCombatMode(false);
				
		if(getSession().getPlayerListSize() > 0){
			Iterator<G_Player> playerIter = getSession().getPlayerListIterator();
			
			while(playerIter.hasNext()){
				G_Player player = playerIter.next();
				S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
				
				if(client == null)
					continue;
				
				String packetData = "combat "+getEntityId()+" "+combat+"\n";
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
			}
		}
	}
	
	public void social(int emotionId) {

		if(this.getSession().getPlayerListSize() > 0){
			Iterator<G_Player> iter = this.getSession().getPlayerListIterator();
			while (iter.hasNext()) {
				G_Player player = iter.next();
				S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
				
				if(client == null)
					continue;
				
				String packetData = "social char "+this.getEntityId()+" " + emotionId + "\n";
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
			}
		}
	}
	/******		Manages the char Logout		******/
	public void logout() {
				
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
		
		if(client == null)
			return;
		
		System.out.print("Player "+this.getName()+" logging out...\n");
		
		S_DatabaseUtils.getInstance().saveInventory(this);
		S_DatabaseUtils.getInstance().saveCharStatus(this);
		S_DatabaseUtils.getInstance().saveEquipment(this);
		S_DatabaseUtils.getInstance().saveStash(client);
		S_DatabaseUtils.getInstance().saveExchange(this);
		S_DatabaseUtils.getInstance().saveQuickSlot(this);
		
		Iterator<S_Session> iter = S_Server.getInstance().getWorldModule().getSessionManager().getSessionListIterator();
		
		while (iter.hasNext()) {
			S_Session session = iter.next();
			
			if(session.contains(this))
				session.exitPlayer(this);
		}
		
		if(S_Server.getInstance().getWorldModule().getPlayerManager().containsPlayer(this))
			S_Server.getInstance().getWorldModule().getPlayerManager().removePlayer(this);
		
		S_Server.getInstance().getWorldModule().getSessionManager().removeSession(this.getSession());
		setSession(null);
	}

	/******		Manages the Item Drop		******/
	public void dropItem(int uniqueId) {

		G_Item item = getInventory().getItemSelected().getItem();
		
		if(item == null)
			return;
		
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
			
		if(client == null)
			return;
				
		getInventory().setItemSelected(null);
		
		String packetData = "drop " +item.getEntityId()+ " "
									+item.getType()+ " "
									+getPosX()+ " "
									+getPosY()+ " "
									+getPosZ()+ " "
									+getRotation()+ " "
									+item.getGemNumber()+ " "
									+item.getExtraStats()+ "\n";
		
		S_Server.getInstance().getNetworkModule().SendPacket(client.networkId,packetData);
			
		if(getSession().getPlayerListSize() > 0){
			Iterator<G_Player> playerIter = getSession().getPlayerListIterator();
			
			while(playerIter.hasNext()){
				G_Player player = playerIter.next();
				client = S_Server.getInstance().getNetworkModule().getClient(player);
				
				if(client == null)
					continue;
				
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
			}
		}
		// S> drop [ItemID] [ItemType] [PosX] [PosY] [Height] [Rotation] [GemNumber] [Special]
	}
	/******		Manages the Pickup command		******/
	public void pickupItem(int uniqueid)
	{
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
		
		if (client==null)
			return;
		
		String packetData = "pickup "+this.getEntityId()+"\n";
		S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);// send the message
		//S> pickup [CharID]
		
		S_Server.getInstance().getWorldModule().getWorldCommand().itemOut(this,uniqueid);
		pickItem(uniqueid);
	}	
	
	/******		Manages the Pick command		******/
	public void pickItem(int uniqueid)
	{
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
		
		if (client==null)
			return;
						
		G_Item item = (G_Item)G_EntityManager.getEntityManager().getEnt(uniqueid);
		getInventory().addItem(item);
		G_InventoryItem invItem = getInventory().getItem(item);
		//S_DatabaseUtils.getInstance().saveInventory(client.playerObject);
		
		if(invItem == null){
			getInventory().setItemSelected(new G_InventoryItem(item,0,0,0));
			String packetData = "pick "+uniqueid+" "+item.getType()+" 0 0 0 "+item.getGemNumber()+" "+item.getExtraStats()+"\n";
			S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
			return;
		}
					
		String packetData = "pick "+uniqueid+" "+item.getType()+" "+invItem.getPosX()+" "+invItem.getPosY()+" "+invItem.getTab()+" "+item.getGemNumber()+" "+item.getExtraStats()+"\n";
		S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
		//S> pick [UniqueID] [Type] [Tab] [PosX] [PosY] [GemNumber] [Special]
	}
	/******		player activates skill		******/
	/*public void useSkill(int skillId) {
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
				
		if(client==null)
			return;
		
		G_Skill skill = getCharSkill().getSkill(skillId);
						
		String packetData = "skill "+skill.getCurrLevel()+" char "+getEntityId()+" "+skillId+"\n";
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, packetData);
		// S> skill [SkillLevel] char [CharID] [SkillID]
	}*/
	/******		increase skill level		******/
	public void skillUp(int skillId) {
						
		if(skillId == 3 || skillId == 4 || skillId == 12){
			levelUpSkill(getCharSkill().getSkill(3));
			levelUpSkill(getCharSkill().getSkill(4));
			levelUpSkill(getCharSkill().getSkill(12));
		}
		else if(skillId == 5 || skillId == 10 || skillId == 13){
			levelUpSkill(getCharSkill().getSkill(5));
			levelUpSkill(getCharSkill().getSkill(10));
			levelUpSkill(getCharSkill().getSkill(13));
		}
										
		else if(skillId == 8 || skillId == 11 || skillId == 14){
			levelUpSkill(getCharSkill().getSkill(8));
			levelUpSkill(getCharSkill().getSkill(11));
			levelUpSkill(getCharSkill().getSkill(14));
		}							
		else if(skillId == 26 || skillId == 27 || skillId == 28){
			levelUpSkill(getCharSkill().getSkill(26));
			levelUpSkill(getCharSkill().getSkill(27));
			levelUpSkill(getCharSkill().getSkill(28));
		}
		else levelUpSkill(getCharSkill().getSkill(skillId));
			
		updateStatus(13,-1,0);
		//S> skilllevel [SkillNumber] [SkillLevel]
	}
	
	/******		revive player when he dies		******/
	public void revive(){
		
		updateStatus(0,getMaxHp(),getMaxHp());
		switch(getMap().getMapId()){
			case 4: { this.getRespawnCoords(4); break; }
			default: {this.getRespawnCoords(-1); break; } 
		}
				
		Iterator<S_Session> sessionIter = S_Server.getInstance().getWorldModule().getSessionManager().getSessionListIterator();
		
		while(sessionIter.hasNext()){
			S_Session session = sessionIter.next();
			G_Player player = session.getSessionOwner();
						
			if(session.contains(this)){
				session.exitPlayer(this);
				this.getSession().exitPlayer(player);
			}
			
			if(player.getMap() != this.getMap() || player == this)
				continue;
				
			S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
				
			if(client == null)
				continue;
									
			int distance = player.getDistance(this);
			
			if(distance <= S_DatabaseUtils.getInstance().getSessionRadius()){
				session.enterPlayer(this,1);
				this.getSession().enterPlayer(player,0);
			}
		}
	}
					
	public void getRespawnCoords(int mapId){
		S_Parser playerSpawns = this.getMap().getPlayerSpawnReference();
		Iterator<S_ParsedItem> iter = playerSpawns.getItemListIterator();
		
		while (iter.hasNext())
		{
			S_ParsedItem i = iter.next();
			
			if(i.getName().equals("Default")){
				S_Server.getInstance().getWorldModule().getWorldCommand().GoToPos(this,
			    		Integer.parseInt(i.getMemberValue("respawnx")) + (int)(Integer.parseInt(i.getMemberValue("respawnwidth"))*Math.random()),
			    		Integer.parseInt(i.getMemberValue("respawny")) + (int)(Integer.parseInt(i.getMemberValue("respawnheight"))*Math.random()));
				return;
			}
			else{ 
				Rectangle rectangle = new Rectangle(Integer.parseInt(i.getMemberValue("targetx")),
						 Integer.parseInt(i.getMemberValue("targety")),
						 Integer.parseInt(i.getMemberValue("targetwidth")),
						 Integer.parseInt(i.getMemberValue("targetheight")));
				
				if(rectangle.contains(this.getPosX(),this.getPosY())) { 
					S_Server.getInstance().getWorldModule().getWorldCommand().GoToPos(this,
			    		Integer.parseInt(i.getMemberValue("respawnx")) + (int)(Integer.parseInt(i.getMemberValue("respawnwidth"))*Math.random()),
			    		Integer.parseInt(i.getMemberValue("respawny")) + (int)(Integer.parseInt(i.getMemberValue("respawnheight"))*Math.random()));
					return;
				}
			}
		}
	}
	
	/*** 	Return the distance between the player and the living object	***/
	public int getDistance(G_LivingObject livingObject){
		double xcomp = Math.pow(livingObject.getPosX() - this.getPosX(), 2);
		double ycomp = Math.pow(livingObject.getPosY() - this.getPosY(), 2);
		double distance = Math.sqrt(xcomp + ycomp);
		
		return (int)distance;
	}

	/******		Handles all the Status Updates		******/
	public void updateStatus(int id, int curr, int max) {
		String packetData = new String();
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
		
		if(client==null)
			return;

		switch (id) {
		  case 0: { //Hp Status
			if(curr > max)
				curr = max;
			setCurrHp(curr);
			setMaxHp(max);
			packetData = "status "+id+" "+getCurrHp()+
				" "+getMaxHp()+"\n";
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
			break;
		  }
		  case 1: { //Mana Status
			if(curr > max)
				curr = max;
			setCurrMana(curr);
			setMaxMana(max);
			packetData = "status "+id+" "+getCurrMana()+
				" "+getMaxMana()+"\n";
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
			break;
		  }
		  case 2: { //Stamina Status
			if(curr > max)
				curr = max;  
			setCurrStm(curr);
			setMaxStm(max);
			packetData = "status "+id+" "+getCurrStm()+
				" "+getMaxStm()+"\n";
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
			break;
		  }
		  case 3: { //Electric Energy Status
			if(curr > max)
				curr = max; 
			setCurrElect(curr);
			setMaxElect(max);
			packetData = "status "+id+" "+getCurrElect()+
				" "+getMaxElect()+"\n";
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
			break;
		  }
		  case 4: { //Player Level Status
			setLevel(getLevel()+curr);
			packetData = "status "+id+" "+getLevel()+
				" "+max+"\n";
			S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
			
			packetData = "levelup "+getEntityId()+"\n";
			S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this,id,getLevel());
			
			if (this.getSession().getPlayerListSize() > 0){
				Iterator<G_Player> playerIter = this.getSession().getPlayerListIterator();
			
				while(playerIter.hasNext()){
					G_Player pl = playerIter.next();
					client = S_Server.getInstance().getNetworkModule().getClient(pl);
					
					if(client == null)
						continue;
										
					S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);				
				}
			}
			break;
		  }
		  case 10: { //Player Lime Status
			setLime(getLime()+curr);
			packetData = "status "+id+" " + getLime() + " "+max+"\n";
			S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this,id,getLime());
			break;
		  }
		  case 11: { //Player Total Exp Status
			setTotalExp(getTotalExp()+curr);
			packetData = "status "+id+" " + getTotalExp() + " "+max+"\n";
			S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this,id,getTotalExp());
			break;
		  }
		  case 12: { //Player Next Level Up Exp Status 
			if(curr <= 0){
				updateStatus(4, 1, 0);
				updateStatus(13, 3, 0);
				updateStatus(0, getMaxHp(), getMaxHp());
				updateStatus(1, getMaxMana(), getMaxMana());
				updateStatus(2, getMaxStm(), getMaxStm());
				updateStatus(3, getMaxElect(), getMaxElect());
				
				loadFromReference(getLevel());
				packetData = "status "+id+" " + getLvlUpExp() + " "+max+"\n";
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
				S_DatabaseUtils.getInstance().updateCharStatus(this,id,getLvlUpExp());
			}
			else{
				setLvlUpExp(curr);
				packetData = "status "+id+" " + getLvlUpExp() + " "+max+"\n";
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
				S_DatabaseUtils.getInstance().updateCharStatus(this,id,getLvlUpExp());
			}
			break;
		  }
		  case 13: { //Player Distribution Status Points
			setStatusPoints(getStatusPoints()+curr);
			packetData = "status "+id+" " + getStatusPoints() + " "+max+"\n";
			S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this,id,getStatusPoints());
			break;
		  }
		  case 14: { //Player Strenght Status
			if(getStatusPoints() <= 0)
				return;
			setStr(getStr() + curr);
			packetData = "status " + id + " " + getStr() + " "+max+"\n";
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this,id,getStr());
			
			updateStatus(0, getCurrHp(), getMaxHp()	+ ( (int)(getStr() / 50) + 1));
			updateStatus(2, getCurrStm(), getMaxStm() + ( (int)(getStr() / 60) + 1));
			updateStatus( 13, -1, 0);
			break;
		  }
		  case 15: { //Player Wisdom Status
			if(getStatusPoints() <= 0)
				return;
			setWis(getWis() + curr);
			packetData = "status " + id + " " + getWis() + " "+max+"\n";
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this,id,getWis());
			
			updateStatus(1, getCurrMana(), getMaxMana()
					+ ( (int)(getWis() / 50) + 2));
			updateStatus(3, getCurrElect(), getMaxElect()
					+ ( (int)(getWis() / 50) + 1));
			updateStatus(13, -1, 0);
			break;
		  }
		  case 16: { //Player Dex Status
			if(getStatusPoints() <= 0)
				return;
			setDex(getDex() + curr);
			packetData = "status " + id + " " + getDex() + " "+max+"\n";
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this,id,getDex());
			
			updateStatus(1, getCurrMana(), getMaxMana()
					+ ( (int)(getDex() / 50) + 1));
			updateStatus(3, getCurrElect(), getMaxElect()
					+ ( (int)(getDex() / 50) + 2));
			updateStatus(13, -1, 0);
			break;
		  }
		  case 17: { //Player Strain Status
			if(getStatusPoints() <= 0)
				return;
			setCons(getCons() + curr);
			packetData = "status " + id + " " + getCons() + " "+max+"\n";
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this,id,getCons());
			
			updateStatus(0, getCurrHp(), getMaxHp()
					+ ( (int)(getCons() / 50) + 2));
			updateStatus(2, getCurrStm(), getMaxStm()
					+ ( (int)(getCons() / 50) + 1));
			updateStatus(13, -1, 0);
			break;
		  }
		  case 18: { //Player Charisma Status
			if(getStatusPoints() <= 0)
				return;
			setLead(getLead() + curr);
			packetData = "status " + id + " " + getLead() + " "+max+"\n";
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
			S_DatabaseUtils.getInstance().updateCharStatus(this,id,getLead());
			
			if((getLead()%2) == 0){
				updateStatus(0, getCurrHp(), getMaxHp() + 1);
				updateStatus(1, getCurrMana(), getMaxMana() + 1);
				updateStatus(2, getCurrStm(), getMaxStm() + 1);
				updateStatus(3, getCurrElect(), getMaxElect() + 1);
			}
			updateStatus(13, -1, 0);
			break;
		  }
		  case 19: { //Player Penalty Points Status ([inGame=packet] -> 100=10; 1000=100; 10000=1000)
				packetData = "status "+id+" "+curr+" "+max+"\n";
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
				S_DatabaseUtils.getInstance().updateCharStatus(this,id,getPenaltyPoints());
				break;
			  }
		}
		// S> status [StatusConstant] [Current] [Max]
		
		//S_DatabaseUtils.getInstance().saveCharStatus(this);
	}
		
	public void wearSlot(int slotid)
	{
		G_InventoryItem invItem = getInventory().getItemSelected();
		String packetData = new String();
		String extraPacketData = null;
		
		if (invItem==null)
		{
			if(getEquipment().getItem(slotid) instanceof G_Weapon){
				this.setMinDmg(1);
				this.setMaxDmg(2);
			}
				
			getInventory().setItemSelected(new G_InventoryItem(getEquipment().getItem(slotid),0,0,0));
			getEquipment().setItem(slotid,null);
			
			packetData = "char_remove "+getEntityId()+" "+slotid+"\n";
		}
		else {
			if(getEquipment().getItem(slotid) == null){
				G_Item item = invItem.getItem();
				packetData = "char_wear "+getEntityId()+" "+slotid+" "+item.getType()+" "+item.getGemNumber()+"\n";
				getEquipment().setItem(slotid,item);
				getInventory().setItemSelected(null);
				if(getEquipment().getItem(slotid) instanceof G_Weapon){
					G_Weapon weapon = (G_Weapon)getEquipment().getItem(slotid);
					this.setMinDmg(weapon.getMinDamage());
					this.setMaxDmg(weapon.getMaxDamage());
				}
			}
			else{
				G_Item currentItem = getEquipment().getItem(slotid);
				extraPacketData = "char_remove "+getEntityId()+" "+slotid+"\n";
				getEquipment().setItem(slotid,invItem.getItem());
				getInventory().setItemSelected(new G_InventoryItem(currentItem,0,0,0));
				if(getEquipment().getItem(slotid) instanceof G_Weapon){
					G_Weapon weapon = (G_Weapon)getEquipment().getItem(slotid);
					this.setMinDmg(weapon.getMinDamage());
					this.setMaxDmg(weapon.getMaxDamage());
				}
				G_Item item = getEquipment().getItem(slotid);
				packetData = "char_wear "+getEntityId()+" "+slotid+" "+item.getType()+" "+item.getGemNumber()+"\n";
			}
		
		}
		//S_DatabaseUtils.getInstance().saveEquipment(this);
		
		if(getSession().getPlayerListSize() > 0){
			Iterator<G_Player> playerIter = getSession().getPlayerListIterator();
			
			while(playerIter.hasNext()){
				G_Player player = playerIter.next();
				S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
				
				if(client == null)
					continue;
				if(extraPacketData != null)
					S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, extraPacketData);
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
			}
		}
	}
	
	/*** 	Manages the Items add/Remove from Trade Box	***/
	public void itemExchange(int posX, int posY){
		
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
		
		if(client == null)
			return;
		
		if(this.getInventory().getItemSelected() == null){
			G_ExchangeItem item = this.getExchange().getItem(posX,posY);
			
			if(item == null)
				return;
			
			G_InventoryItem invItem = new G_InventoryItem(item.getItem(),0,0,0);
			
			this.getInventory().setItemSelected(invItem);
			this.getExchange().removeItem(item);
		}
		else {
			G_Item item = this.getInventory().getItemSelected().getItem();
			G_ExchangeItem newExchangeItem = new G_ExchangeItem(item,posX,posY);
			G_ExchangeItem oldExchangeItem = null;
			int x=0,y=0;
			
			while(oldExchangeItem==null && x<item.getSizeX()) {
				while(oldExchangeItem==null && y < item.getSizeY()){
					oldExchangeItem = this.getExchange().getItem(posX+x,posY+y);
					y++;
				}
				y=0;
				x++;
			}
			
			if(oldExchangeItem == null){
				this.getInventory().setItemSelected(null);
			}
			else {
				G_InventoryItem invItem = new G_InventoryItem(oldExchangeItem.getItem(),0,0,0);
				this.getInventory().setItemSelected(invItem);
				this.getExchange().removeItem(oldExchangeItem);
			}
			this.getExchange().addItem(newExchangeItem);
		}
	}

	/******		load Quick Slot Items	******/
	public void loadInventory(){
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
		
		if(client == null)
			return;
		
		Iterator<G_InventoryItem> invIter = getInventory().getInventoryIterator();
		while(invIter.hasNext()){
			G_InventoryItem invItem = invIter.next();
			
			String packetData = "inven "+invItem.getTab()+" "+invItem.getItem().getEntityId()+" "
			+invItem.getItem().getType()+" "+invItem.getPosX()+" "+invItem.getPosY()+" "
			+invItem.getItem().getGemNumber()+" "+invItem.getItem().getExtraStats()+"\n";
			// inven [Tab] [UniqueId] [Type] [PosX] [PosY] [Gems] [Special]
			S_Server.getInstance().getNetworkModule().SendPacket(client.networkId,packetData);
			
		}
	}
	
	/******		Load Items at Trade Box	******/
	public void loadExchange(){
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
		
		if(client == null)
			return;
		
		Iterator<G_ExchangeItem> exchangeIter = getExchange().itemListIterator();
		while(exchangeIter.hasNext()){
			G_ExchangeItem exchangeItem = exchangeIter.next();
			
			String packetData = "inven 3 "+exchangeItem.getItem().getEntityId()+" "
			+exchangeItem.getItem().getType()+" "+exchangeItem.getPosX()+" "
			+exchangeItem.getPosY()+" "+exchangeItem.getItem().getGemNumber()+" "
			+exchangeItem.getItem().getExtraStats()+"\n";
			// inven [Tab] [UniqueId] [Type] [PosX] [PosY] [Gems] [Special]
			S_Server.getInstance().getNetworkModule().SendPacket(client.networkId,packetData);
		}
	}
	
	/******		load Quick Slot Items	******/
	public void loadQuickSlot(){
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
		
		if(client == null)
			return;
		
		Iterator<G_QuickSlotItem> quickSlot = getQuickSlot().getQuickSlotIterator();
		while(quickSlot.hasNext()){
			G_QuickSlotItem qsItem = quickSlot.next();
		
			String packetData = "quick "+qsItem.getSlot()+" "+qsItem.getItem().getEntityId()+
				" "+qsItem.getItem().getType()+" "+qsItem.getItem().getGemNumber()+
				" "+qsItem.getItem().getExtraStats()+"\n";
			S_Server.getInstance().getNetworkModule().SendPacket(client.networkId,packetData);
		}
	}
	
	public void loadFromReference(int id)
	{	
	  S_ParsedItem exp = S_Reference.getInstance().getExpReference().getItemById(id);
		
	  if (exp==null)
	  {
		// cant find Item in the reference continue to load defaults:
		  setLvlUpExp(1000);  
	  }
	  else {
		
		if(exp.checkMembers(new String[]{"Exp"}))
		{
			// use member from file
			setLvlUpExp(Integer.parseInt(exp.getMemberValue("Exp")));
		}
		else
		{
			// use default
			setLvlUpExp(1000);
		}
	  }
	}
	
}
