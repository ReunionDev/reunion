package org.reunionemu.jreunion.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.reunionemu.jcommon.Parser;
import org.reunionemu.jreunion.server.beans.SpringApplicationContext;
import org.springframework.context.ApplicationContext;


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

		itemReference.Parse(getDataResource("Items.dta"));
		mobReference.Parse(getDataResource("Mob.dta"));
		expReference.Parse(getDataResource("ExpTable.dta"));
		mapReference.Parse(getDataResource("Maps.dta"));
		npcReference.Parse(getDataResource("Npc.dta"));
		dropListReference.Parse(getDataResource("DropList.dta"));
		skillReference.Parse(getDataResource("Skills.dta"));

	}
	
	public static InputStream getDataResource(String filename){
		String dataPath = getInstance().getServerReference().getItem("Server").getMemberValue("DataPath");
		ApplicationContext context = SpringApplicationContext.getApplicationContext();
		String path = new File(dataPath, filename).getPath();
		try{
			if(context!=null){
				return context.getResource(path).getInputStream();
			}else{
				return new FileInputStream(path);
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	public static String getDataPathFile(String filename){
		String dataPath = getInstance().getServerReference().getItem("Server").getMemberValue("DataPath");
		String path = new File(dataPath, filename).getPath();
		return path;
	
	}
}