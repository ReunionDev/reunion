package org.reunionemu.jreunion.server;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jcommon.Parser;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.ItemType;

public class ItemManager {

	private java.util.Map<Integer, ItemType> itemsList = new HashMap<Integer, ItemType>();
	
	public ItemManager(){
		loadItemsList();
	}
	
	public void loadItemsList(){
		
		Parser parser = Reference.getInstance().getItemReference();
		Iterator<ParsedItem> iter = parser.getItemListIterator();
		
		while(iter.hasNext()){
			
			ParsedItem parsedItem = iter.next();
			if(!parsedItem.checkMembers(new String[]{"Id","Class"}))
				continue;
			
			int id = Integer.parseInt(parsedItem.getMemberValue("Id"));
			String className = "org.reunionemu.jreunion.game.items."+parsedItem.getMemberValue("Class");		
			
			ItemType itemType = (ItemType)ClassFactory.create(className, id);
			
			if(itemType == null){
				Logger.getLogger(ItemManager.class).warn("Failed to load Item type {id:"+id+" name:"
						+parsedItem.getName()+"}");
				continue;
			}
			
			itemsList.put(id, itemType);
		}
		parser.clear();
		Logger.getLogger(ItemManager.class).info(itemsList.size()+" items loaded.");
	}
	
	public ItemType getItemType(int type){
		return itemsList.get(type);
	}
	
	public ItemType getItemType(Class<?> classType){
		for(ItemType itemType: itemsList.values()){
			if(itemType.getClass()==classType)
				return itemType;
		}
		throw new IllegalArgumentException(classType+" not found");
	}
	
	public Item<?> create(int type) {
		
		ItemType itemType = getItemType(type);
		
		if(itemType == null)
			return null;
				
		Item<?> item = itemType.create();
		
		DatabaseUtils.getDinamicInstance().saveItem(item);
		
		return item;
	}
	
	
	
}
