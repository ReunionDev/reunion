package com.googlecode.reunion.jreunion.server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Reference {

	private synchronized static void createInstance() {
		if (_instance == null) {
			_instance = new S_Reference();
		}
	}

	public static S_Reference getInstance() {
		if (_instance == null) {
			createInstance();
		}
		return _instance;
	}

	private S_Parser itemReference;
	private S_Parser mobReference;
	private S_Parser skillReference;
	private S_Parser expReference;
	private S_Parser npcReference;

	private S_Parser mapReference;

	private S_Parser serverReference;
	private static S_Reference _instance = null;

	public S_Reference() {
		super();
		itemReference = new S_Parser();
		mobReference = new S_Parser();
		skillReference = new S_Parser();
		expReference = new S_Parser();
		mapReference = new S_Parser();
		npcReference = new S_Parser();
		serverReference = new S_Parser();
	}

	public void clear() {
		mobReference.clear();
		itemReference.clear();
		skillReference.clear();
		mapReference.clear();
		expReference.clear();
		npcReference.clear();
	}

	public S_Parser getExpReference() {
		return expReference;
	}

	public S_Parser getItemReference() {
		return itemReference;
	}

	/**
	 * @return Returns the mapReference.
	 */
	public S_Parser getMapReference() {
		return mapReference;
	}

	public S_Parser getMobReference() {
		return mobReference;
	}

	public S_Parser getNpcReference() {
		return npcReference;
	}

	public S_Parser getServerReference() {
		return serverReference;
	}

	public S_Parser getSkillReference() {
		return skillReference;
	}

	public void Load() {
		clear();
		itemReference.Parse("Items.dta");
		mobReference.Parse("Mob.dta");
		skillReference.Parse("Skills.dta");
		expReference.Parse("ExpTable.dta");
		mapReference.Parse("Maps.dta");
		npcReference.Parse("Npc.dta");
		serverReference.Parse("ServerSetings.dta");
	}

}