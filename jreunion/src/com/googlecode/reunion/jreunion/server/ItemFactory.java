package com.googlecode.reunion.jreunion.server;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Item;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ItemFactory {

	public static Item createItem(int type) {
		Item item = null;
		

		ParsedItem parseditem = Reference.getInstance().getItemReference()
				.getItemById(type);
		if (parseditem == null) {
			return null;
		}

		String classname = parseditem.getMemberValue("Class");

		try {

			Class c = Class.forName("com.googlecode.reunion.jreunion.game."
					+ classname);
			item = (Item) c.getConstructors()[0].newInstance(type);

		} catch (Exception e) {

			System.out.println("Cannot create class:" + classname);
			e.printStackTrace();
			return null;
		}
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
