package org.reunionemu.jreunion.server;

import org.reunionemu.jcommon.Parser;


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
	private Parser expReference;
	private Parser npcReference;
	private Parser dropListReference;
	private Parser skillReference;

	private Parser mapReference;
	private Parser mapConfigReference;

	private Parser serverReference;
	private static Reference _instance = null;

	public Reference() {
		super();
		itemReference = new Parser();
		mobReference = new Parser();
		expReference = new Parser();
		mapReference = new Parser();
		mapConfigReference = new Parser();
		npcReference = new Parser();
		serverReference = new Parser();
		dropListReference = new Parser();
		skillReference = new Parser();
	}

	public void clear() {
		mobReference.clear();
		itemReference.clear();
		mapReference.clear();
		mapConfigReference.clear();
		expReference.clear();
		npcReference.clear();
		serverReference.clear();
		dropListReference.clear();
		skillReference.clear();
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
	
	public Parser getDropListReference() {
		return dropListReference;
	}
	
	public Parser getSkillReference() {
		return skillReference;
	}

	public void setSkillReference(Parser skillReference) {
		this.skillReference = skillReference;
	}

	public void Load() throws Exception {
		clear();
		itemReference.Parse("data/static/file/Items.dta");
		mobReference.Parse("data/static/file/Mob.dta");
		expReference.Parse("data/static/file/ExpTable.dta");
		mapReference.Parse("data/static/file/Maps.dta");
		mapConfigReference.Parse("config/Maps.dta");
		npcReference.Parse("data/static/file/Npc.dta");
		serverReference.Parse("config/Settings.dta");
		dropListReference.Parse("data/static/file/DropList.dta");
		skillReference.Parse("data/static/file/Skills.dta");
		
	}
}