package org.reunionemu.jreunion.server;

import org.reunionemu.jcommon.ParsedItem;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ServerSetings {

	private long xp;

	private long lime;
	
	private long startLime;
	
	private long defaultMapId;
	
	private long defaultVersion;
	
	private long sessionRadius;
	
	private long dropExclusivity;
	
	private String welcomeMessage;
	
	private long spawnAttempts;
	
	private long dropTimeOut;
	
	private float criticalMultiplier; //% ammount of critical damage increase. 
	
	private float criticalChance; //% chance to get a critical damage
	
	private float petEquipmentCompensation; //% of exp compensation when changing pet equipment at npc.
	
	private int petBreedTime; //Time it will take to breed a pet (in seconds).
	
	private float itemPlusByOne; //% chance to drop a +1 item.
	
	private float itemPlusByTwo; //% chance to drop a +2 item.
	
	private float mobMutantChance; //% chance to spawn mutant mob.
	
	private float mobMutantModifier; //modifier to aply on mob stats.
	
	private long mobRadiusArea; //mob area radius.
	
	private int mobsMovement;	//Enable/Disable movements from mobs.

	private int expPlayermMobDifference; //lvl difference between mob and player, after that difference exp starts dropping

	private int expLowerStep; //its % value
	
	private int closeAttackRadius;	//Minimum distance when close attack mobs will start attacking 
	
	private int rangeAttackRadius;	//Minimum distance when range attack mobs will start attacking
	
	private float demolitionModifier; //% of damage increase of demolition attacks from Slayer weapon
	
	public ServerSetings() {
		loadFromReference();
	}

	public long getLime() {
		return lime;
	}

	public long getXp() {
		return xp;
	}

	public void loadFromReference() {
		ParsedItem server = Reference.getInstance().getServerReference().getItem("Server");

		if (server == null) {
			// cant find Item in the reference continue to load defaults:
			setXp(1);
			setLime(1);
			setStartLime(100);
			setDefaultMapId(4);
			setDefaultVersion(2000);
			setSessionRadius(300);
			setDropExclusivity(10);
			setSpawnAttempts(1);
			setDropTimeOut(600);
			setCriticalMultiplier(1);
			setCriticalChance((float)0.5);
			setPetEquipmentCompensation((float)0.3);
			setItemPlusByOne((float)0.05);
			setItemPlusByTwo((float)0.01);
			setMobMutantChance((float)0.1);
			setMobRadiusArea(10);
			setPetBreedTime(72000);
			setMobsMovement(1);
			setCloseAttackRadius(20);
			setRangeAttackRadius(100);
			setDemolitionModifier(0.5f);
			setWelcomeMessage("Hey, welcome on the Reunion Testserver");
		} else {

			if (server.checkMembers(new String[] { "xp" })) {
				// use member from file
				setXp(Long.parseLong(server.getMemberValue("xp")));
			} else {
				// use default
				setXp(1);
			}
			if (server.checkMembers(new String[] { "lime" })) {
				// use member from file
				setLime(Long.parseLong(server.getMemberValue("lime")));
			} else {
				// use default
				setLime(1);
			}
			if (server.checkMembers(new String[] { "DefaultMap" })) {
				// use member from file
				setDefaultMapId(Long.parseLong(server.getMemberValue("DefaultMap")));
			} else {
				// use default
				setDefaultMapId(4);
			}
			if (server.checkMembers(new String[] { "StartLime" })) {
				// use member from file
				setStartLime(Long.parseLong(server.getMemberValue("StartLime")));
			} else {
				// use default
				setStartLime(100);
			}
			if (server.checkMembers(new String[] { "Version" })) {
				// use member from file
				setDefaultVersion(Long.parseLong(server.getMemberValue("Version")));
			} else {
				// use default
				setDefaultVersion(2000);
			}
			if (server.checkMembers(new String[] { "WelcomeMsg" })) {
				// use member from file
				setWelcomeMessage(server.getMemberValue("WelcomeMsg"));
			} else {
				// use default
				setWelcomeMessage("Hey, welcome on the Reunion Testserver");
			}
			if (server.checkMembers(new String[] { "SessionRadius" })) {
				// use member from file
				setSessionRadius(Long.parseLong(server.getMemberValue("SessionRadius")));
			} else {
				// use default
				setSessionRadius(300);
			}
			if (server.checkMembers(new String[] { "DropExclusivity" })) {
				// use member from file
				setDropExclusivity(Long.parseLong(server.getMemberValue("DropExclusivity")));
			} else {
				// use default
				setDropExclusivity(10);
			}
			if (server.checkMembers(new String[] { "SpawnAttempts" })) {
				// use member from file
				setSpawnAttempts(Long.parseLong(server.getMemberValue("SpawnAttempts")));
			} else {
				// use default
				setSpawnAttempts(1);
			}
			if (server.checkMembers(new String[] { "DropTimeOut" })) {
				// use member from file
				setDropTimeOut(Long.parseLong(server.getMemberValue("DropTimeOut")));
			} else {
				// use default
				setDropTimeOut(600);
			}
			if (server.checkMembers(new String[] { "CriticalMultiplier" })) {
				// use member from file
				setCriticalMultiplier(Float.parseFloat(server.getMemberValue("CriticalMultiplier")));
			} else {
				// use default
				setCriticalMultiplier(1);
			}
			if (server.checkMembers(new String[] { "CriticalChance" })) {
				// use member from file
				setCriticalChance(Float.parseFloat(server.getMemberValue("CriticalChance")));
			} else {
				// use default
				setCriticalChance((float)0.5);
			}
			if (server.checkMembers(new String[] { "PetEquipmentCompensation" })) {
				// use member from file
				setPetEquipmentCompensation(Float.parseFloat(server.getMemberValue("PetEquipmentCompensation")));
			} else {
				// use default
				setPetEquipmentCompensation((float)0.3);
			}
			if (server.checkMembers(new String[] { "ItemPlusByOne" })) {
				// use member from file
				setItemPlusByOne(Float.parseFloat(server.getMemberValue("ItemPlusByOne")));
			} else {
				// use default
				setItemPlusByOne((float)0.05);
			}
			if (server.checkMembers(new String[] { "ItemPlusByTwo" })) {
				// use member from file
				setItemPlusByTwo(Float.parseFloat(server.getMemberValue("ItemPlusByTwo")));
			} else {
				// use default
				setItemPlusByTwo((float)0.01);
			}
			if (server.checkMembers(new String[] { "MobMutantChance" })) {
				// use member from file
				setMobMutantChance(Float.parseFloat(server.getMemberValue("MobMutantChance")));
			} else {
				// use default
				setMobMutantChance((float)0.1);
			}
			if (server.checkMembers(new String[] { "MobRadiusArea" })) {
				// use member from file
				setMobRadiusArea(Long.parseLong(server.getMemberValue("MobRadiusArea")));
			} else {
				// use default
				setMobRadiusArea(10);
			}
			if (server.checkMembers(new String[] { "MobMutantModifier" })) {
				// use member from file
				setMobMutantModifier(Float.parseFloat(server.getMemberValue("MobMutantModifier")));
			} else {
				// use default
				setMobMutantModifier((float)0.5);
			}
			if (server.checkMembers(new String[] { "PetBreedTime" })) {
				// use member from file
				setPetBreedTime(Integer.parseInt(server.getMemberValue("PetBreedTime")));
			} else {
				// use default
				setPetBreedTime(72000);
			}
			if (server.checkMembers(new String[] { "MobsMovement" })) {
				// use member from file
				setMobsMovement(Integer.parseInt(server.getMemberValue("MobsMovement")));
			} else {
				// use default
				setMobsMovement(1);
			}
			
			if (server.checkMembers(new String[] { "ExpPlayerMobDifference" })) {
				// use member from file
				setExpPlayerMobDifference(Integer.parseInt(server.getMemberValue("ExpPlayerMobDifference")));
			} else {
				// use default
				setExpPlayerMobDifference(5);
			}

			if (server.checkMembers(new String[] { "ExpLowerStep" })) {
				// use member from file
				setExpLowerStep(Integer.parseInt(server.getMemberValue("ExpLowerStep")));
			} else {
				// use default
				setExpLowerStep(5);
			}
			
			if (server.checkMembers(new String[] { "CloseAttackRadius" })) {
				// use member from file
				setCloseAttackRadius(Integer.parseInt(server.getMemberValue("CloseAttackRadius")));
			} else {
				// use default
				setCloseAttackRadius(20);
			}
			
			if (server.checkMembers(new String[] { "RangeAttackRadius" })) {
				// use member from file
				setRangeAttackRadius(Integer.parseInt(server.getMemberValue("RangeAttackRadius")));
			} else {
				// use default
				setRangeAttackRadius(100);
			}
			
			if (server.checkMembers(new String[] { "DemolitionModifier" })) {
				// use member from file
				setDemolitionModifier(Float.parseFloat(server.getMemberValue("DemolitionModifier")));
			} else {
				// use default
				setDemolitionModifier(0.5f);
			}
			
		}
	}

	public void setLime(long lime) {
		this.lime = lime;
	}

	public void setXp(long xp) {
		this.xp = xp;
	}

	public long getStartLime() {
		return startLime;
	}

	public void setStartLime(long startLime) {
		this.startLime = startLime;
	}

	public long getDefaultMapId() {
		return defaultMapId;
	}

	public void setDefaultMapId(long defaultMapId) {
		this.defaultMapId = defaultMapId;
	}

	public long getDefaultVersion() {
		return defaultVersion;
	}

	public void setDefaultVersion(long defaultVersion) {
		this.defaultVersion = defaultVersion;
	}

	public long getSessionRadius() {
		return sessionRadius;
	}

	public void setSessionRadius(long sessionRadius) {
		this.sessionRadius = sessionRadius;
	}

	public long getDropExclusivity() {
		return dropExclusivity;
	}

	public void setDropExclusivity(long dropExclusivity) {
		this.dropExclusivity = dropExclusivity;
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}

	public long getSpawnAttempts() {
		return spawnAttempts;
	}

	public void setSpawnAttempts(long spawnAttempts) {
		this.spawnAttempts = spawnAttempts;
	}

	public long getDropTimeOut() {
		return dropTimeOut;
	}

	public void setDropTimeOut(long dropTimeOut) {
		this.dropTimeOut = dropTimeOut;
	}
	
	public float getCriticalMultiplier() {
		return criticalMultiplier;
	}

	public void setCriticalMultiplier(float criticalMultiplier) {
		this.criticalMultiplier = criticalMultiplier;
	}
	
	public float getCriticalChance() {
		return criticalChance;
	}

	public void setCriticalChance(float criticalChance) {
		this.criticalChance = criticalChance;
	}

	public float getPetEquipmentCompensation() {
		return petEquipmentCompensation;
	}

	public void setPetEquipmentCompensation(float petEquipmentCompensation) {
		this.petEquipmentCompensation = petEquipmentCompensation;
	}
	
	public int getExpPlayerMobDifference() {
		return expPlayermMobDifference;
	}

	public void setExpPlayerMobDifference(int mobPlayerDiff) {
		this.expPlayermMobDifference = mobPlayerDiff;
	}

	public int getExpLowerStep() {
		return expLowerStep;
	}

	public void setExpLowerStep(int expLowerStep) {
		this.expLowerStep = expLowerStep;
	}
	
	public float getItemPlusByOne() {
		return itemPlusByOne;
	}

	public void setItemPlusByOne(float itemPlusByOne) {
		this.itemPlusByOne = itemPlusByOne;
	}

	public float getItemPlusByTwo() {
		return itemPlusByTwo;
	}

	public void setItemPlusByTwo(float itemPlusByTwo) {
		this.itemPlusByTwo = itemPlusByTwo;
	}

	public float getMobMutantChance() {
		return mobMutantChance;
	}

	public void setMobMutantChance(float mobMutantChance) {
		this.mobMutantChance = mobMutantChance;
	}

	public long getMobRadiusArea() {
		return mobRadiusArea;
	}

	public void setMobRadiusArea(long mobRadiusArea) {
		this.mobRadiusArea = mobRadiusArea;
	}

	public float getMobMutantModifier() {
		return mobMutantModifier;
	}

	public void setMobMutantModifier(float mobMutantModifier) {
		this.mobMutantModifier = mobMutantModifier;
	}

	public int getPetBreedTime() {
		return petBreedTime;
	}

	public void setPetBreedTime(int petBreedTime) {
		this.petBreedTime = petBreedTime;
	}

	public int getMobsMovement() {
		return mobsMovement;
	}

	public void setMobsMovement(int mobsMovement) {
		this.mobsMovement = mobsMovement;
	}

	public int getCloseAttackRadius() {
		return closeAttackRadius;
	}

	public void setCloseAttackRadius(int closeAttackRadius) {
		this.closeAttackRadius = closeAttackRadius;
	}

	public int getRangeAttackRadius() {
		return rangeAttackRadius;
	}

	public void setRangeAttackRadius(int rangeAttackRadius) {
		this.rangeAttackRadius = rangeAttackRadius;
	}

	public float getDemolitionModifier() {
		return demolitionModifier;
	}

	public void setDemolitionModifier(float demolitionModifier) {
		this.demolitionModifier = demolitionModifier;
	}
}
