package com.googlecode.reunion.jreunion.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.ItemType;

public class ItemManager {

	private java.util.Map<Integer, ItemType> itemsList = new HashMap<Integer, ItemType>();
	
	public ItemManager(){
		loadItemsList();
	}
	
	public void loadItemsList(){
		
		Parser parser = new Parser();
		
		try {
			parser.Parse("data/Items.dta");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		Iterator<ParsedItem> iter = parser.getItemListIterator();
		
		while(iter.hasNext()){
			
			ParsedItem parsedItem = iter.next();
			if(!parsedItem.checkMembers(new String[]{"Id","Class"}))
				continue;
			
			int id = Integer.parseInt(parsedItem.getMemberValue("Id"));
			String className = "com.googlecode.reunion.jreunion.game.items."+parsedItem.getMemberValue("Class");
			
			
			ItemType itemType = (ItemType)ClassFactory.create(className, id);
			
			if(itemType == null)
				continue;
			
			
			itemsList.put(id, itemType);
		}
		parser.clear();
		Logger.getLogger(ItemManager.class).info(itemsList.size()+" item types loaded.");
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
