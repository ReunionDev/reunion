package com.googlecode.reunion.jreunion.server;

import com.googlecode.reunion.jcommon.Parser;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Reference {

	private synchronized static void createInstance() {
		if (_instance == null) {
			_instance = new Reference();
		}
	}

	public static Reference getInstance() {
		if (_instance == null) {
			createInstance();
		}
		return _instance;
	}

	private Parser itemReference;
	private Parser mobReference;
	private Parser skillReference;
	private Parser expReference;
	private Parser npcReference;
	private Parser dropListReference;

	private Parser mapReference;
	private Parser mapConfigReference;

	private Parser serverReference;
	private static Reference _instance = null;

	public Reference() {
		super();
		itemReference = new Parser();
		mobReference = new Parser();
		skillReference = new Parser();
		expReference = new Parser();
		mapReference = new Parser();
		mapConfigReference = new Parser();
		npcReference = new Parser();
		serverReference = new Parser();
		dropListReference = new Parser();
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

	public Parser getExpReference() {
		return expReference;
	}

	public Parser getItemReference() {
		return itemReference;
	}

	/**
	 * @return Returns the mapReference.
	 */
	public Parser getMapReference() {
		return mapReference;
	}
	
	public Parser getMapConfigReference() {
		return mapConfigReference;
	}

	public Parser getMobReference() {
		return mobReference;
	}

	public Parser getNpcReference() {
		return npcReference;
	}

	public Parser getServerReference() {
		return serverReference;
	}

	public Parser getSkillReference() {
		return skillReference;
	}
	public Parser getDropListReference() {
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