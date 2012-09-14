package org.reunionemu.jreunion.server;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jcommon.Parser;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.ItemType;
import org.reunionemu.jreunion.game.Npc;
import org.reunionemu.jreunion.game.NpcType;
import org.reunionemu.jreunion.game.npc.Merchant;
import org.reunionemu.jreunion.game.npc.Mob;

public class NpcManager {

	private java.util.Map<Integer, NpcType> npcList = new HashMap<Integer, NpcType>();
	
	public NpcManager(){
		
		Parser npcParser = Reference.getInstance().getNpcReference();
		
		//load npc types
		for(ParsedItem parsedItem : npcParser){
			
			if(!parsedItem.checkMembers(new String[]{"Id","Class"})){
				continue;
			}
			
			int id = Integer.parseInt(parsedItem.getMemberValue("Id"));
			String className = "org.reunionemu.jreunion.game.npc."+parsedItem.getMemberValue("Class");
						
			NpcType npcType = (NpcType)ClassFactory.create(className, id);
			
			if(npcType == null){
				LoggerFactory.getLogger(NpcManager.class).warn("Failed to load Npc type {id:"+id+" name:"
						+parsedItem.getName()+"}");
				continue;
			}
			
			npcList.put(id, npcType);
		}
		npcParser.clear();
		LoggerFactory.getLogger(ItemManager.class).info("Loaded "+npcList.size()+" npc types");
		
		
		Parser mobParser = Reference.getInstance().getMobReference();
		
		//load mob types
		for(ParsedItem parsedItem : mobParser){
			
			if(!parsedItem.checkMembers(new String[]{"Id","Class"}))
				continue;
			
			int id = Integer.parseInt(parsedItem.getMemberValue("Id"));
			String className = "org.reunionemu.jreunion.game.npc."+parsedItem.getMemberValue("Class");
			
			NpcType npcType = (NpcType)ClassFactory.create(className, id);
			
			if(npcType == null){
				LoggerFactory.getLogger(NpcManager.class).warn("Failed to load Mob type {id:"+id+" name:"
						+parsedItem.getName()+"}");
				continue;
			}
			
			npcList.put(id, npcType);
		}
		LoggerFactory.getLogger(ItemManager.class).info("Loaded "+getMobList().size()+" mob types");
		mobParser.clear();
		
	}
	
	public NpcType getNpcType(int type){
		return npcList.get(type);
	}
	
	public NpcType getNpcType(Class<?> classType){
		for(NpcType npcType: npcList.values()){
			if(npcType.getClass()==classType)
				return npcType;
		}
		throw new IllegalArgumentException(classType+" not found");
	}
	
	public Npc<?> create(int type) {
		
		NpcType npcType = getNpcType(type);
		
		if(npcType == null)
			return null;
		
		Npc<?> npc = npcType.create();
		
		return npc;
	}
	
	public List<Mob> getMobList(){
		List<Mob> mobList = new Vector<Mob>();
		
		for(NpcType npcType : npcList.values()){
			if(npcType instanceof Mob){
				mobList.add((Mob)npcType);
			}
		}
		return mobList;
	}
}
