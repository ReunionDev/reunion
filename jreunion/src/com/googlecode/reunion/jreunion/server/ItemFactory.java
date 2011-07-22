package com.googlecode.reunion.jreunion.server;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Item;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ItemFactory {

	public static Item create(int type) {
		Item item = null;
		
		ParsedItem parseditem = Reference.getInstance().getItemReference()
				.getItemById(type);
		if (parseditem == null) {
			return null;
		}

		String className = "com.googlecode.reunion.jreunion.game.items." + parseditem.getMemberValue("Class");		
		
		item = (Item)ClassFactory.create(className, type);
		
		DatabaseUtils.getInstance().saveItem(item);
		return item;
	}

	public static Item loadItem(int itemId) {
		
		return DatabaseUtils.getInstance().loadItem(itemId);
		
	}

	public ItemFactory() {
		super();

	}

}
