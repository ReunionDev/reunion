package org.reunionemu.jreunion.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.reunionemu.jcommon.Parser;
import org.reunionemu.jreunion.server.beans.SpringApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
@Service
public class Reference {
	
	
	@Autowired
	ApplicationContext context;

	public ApplicationContext getContext() {
		return context;
	}

	private synchronized static void createInstance() {
		if (_instance == null) {
			_instance = new Reference();
		}
	}

	public static Reference getInstance() {
		if (_instance == null) {
			throw new RuntimeException();
			//createInstance();
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
		
		
		itemReference = new Parser();
		mobReference = new Parser();
		expReference = new Parser();
		mapReference = new Parser();
		mapConfigReference = new Parser();
		npcReference = new Parser();
		serverReference = new Parser();
		dropListReference = new Parser();
		skillReference = new Parser();
		_instance = this;
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

	@PostConstruct
	public void Load(){
		clear();
		try {
			serverReference.Parse("config/Settings.dta");
		
			mapConfigReference.Parse("config/Maps.dta");
	
			itemReference.Parse(getDataResourceS("Items.dta"));
			mobReference.Parse(getDataResourceS("Mob.dta"));
			expReference.Parse(getDataResourceS("ExpTable.dta"));
			mapReference.Parse(getDataResourceS("Maps.dta"));
			npcReference.Parse(getDataResourceS("Npc.dta"));
			dropListReference.Parse(getDataResourceS("DropList.dta"));
			skillReference.Parse(getDataResourceS("Skills.dta"));
		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public InputStream getDataResourceS(String filename){
		String dataPath = getServerReference().getItem("Server").getMemberValue("DataPath");
		//ApplicationContext context = SpringApplicationContext.getApplicationContext();
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
	
	public static InputStream getDataResource(String filename){
		String dataPath = getInstance().getServerReference().getItem("Server").getMemberValue("DataPath");
		ApplicationContext context = getInstance().getContext();
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