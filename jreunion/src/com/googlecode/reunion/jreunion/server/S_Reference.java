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
	private S_Parser dropListReference;

	private S_Parser mapReference;
	private S_Parser mapConfigReference;

	private S_Parser serverReference;
	private static S_Reference _instance = null;

	public S_Reference() {
		super();
		itemReference = new S_Parser();
		mobReference = new S_Parser();
		skillReference = new S_Parser();
		expReference = new S_Parser();
		mapReference = new S_Parser();
		mapConfigReference = new S_Parser();
		npcReference = new S_Parser();
		serverReference = new S_Parser();
		dropListReference = new S_Parser();
	}

	public void clear() {
		mobReference.clear();
		itemReference.clear();
		skillReference.clear();
		mapReference.clear();
		mapConfigReference.clear();
		expReference.clear();
		npcReference.clear();
		serverReference.clear();
		dropListReference.clear();
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
	
	public S_Parser getMapConfigReference() {
		return mapConfigReference;
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
	public S_Parser getDropListReference() {
		return dropListReference;
	}

	public void Load() throws Exception {
		clear();
		
		itemReference.Parse("data/Items.dta");
		mobReference.Parse("data/Mob.dta");
		skillReference.Parse("data/Skills.dta");
		expReference.Parse("data/ExpTable.dta");
		mapReference.Parse("data/Maps.dta");
		mapConfigReference.Parse("config/Maps.dta");
		npcReference.Parse("data/Npc.dta");
		serverReference.Parse("config/Settings.dta");
		dropListReference.Parse("data/DropList.dta");
		
	}

}