package org.reunionemu.jreunion.server;

import java.util.HashMap;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jcommon.Parser;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.ItemType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service
public class ItemManager {

	private java.util.Map<Integer, ItemType> itemsList = new HashMap<Integer, ItemType>();
	
	@Autowired
	Reference reference;
	
	public ItemManager(){
		
	}
	
	@PostConstruct
	public void loadItemsList(){
		
		Parser parser = reference.getItemReference();
		Iterator<ParsedItem> iter = parser.getItemListIterator();
		
		while(iter.hasNext()){
			
			ParsedItem parsedItem = iter.next();
			if(!parsedItem.checkMembers(new String[]{"Id","Class"}))
				continue;
			
			int id = Integer.parseInt(parsedItem.getMemberValue("Id"));
			String className = "org.reunionemu.jreunion.game.items."+parsedItem.getMemberValue("Class");		
			
			ItemType itemType = (ItemType)ClassFactory.create(className, id);
			
			if(itemType == null){
				LoggerFactory.getLogger(ItemManager.class).warn("Failed to load Item type {id:"+id+" name:"
						+parsedItem.getName()+"}");
				continue;
			}
			
			itemsList.put(id, itemType);
		}
		parser.clear();
		LoggerFactory.getLogger(ItemManager.class).info("Loaded "+itemsList.size()+" item types");
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
	
	public Item<?> create(int type, int gemNumber, int extraStats, int durability, int unknown1, int unknown2) {
		Item<?> item = create(type);
		
		item.setGemNumber(gemNumber);
		item.setExtraStats(extraStats);
		item.setDurability(durability);
		item.setUnknown1(unknown1);
		item.setUnknown2(unknown2);
		
		return item;
	}
	
}
