package org.reunionemu.jreunion.server;

import java.io.File;

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
		serverReference.Parse("config/Settings.dta");
		mapConfigReference.Parse("config/Maps.dta");

		itemReference.Parse(getDataPathFile("Items.dta"));
		mobReference.Parse(getDataPathFile("Mob.dta"));
		expReference.Parse(getDataPathFile("ExpTable.dta"));
		mapReference.Parse(getDataPathFile("Maps.dta"));
		npcReference.Parse(getDataPathFile("Npc.dta"));
		dropListReference.Parse(getDataPathFile("DropList.dta"));
		skillReference.Parse(getDataPathFile("Skills.dta"));

	}
	
	public static String getDataPathFile(String filename){
		String dataPath = getInstance().getServerReference().getItem("Server").getMemberValue("DataPath");
		
		return new File(dataPath, filename).getPath();
		
		
	}
}