package com.googlecode.reunion.jreunion.server;

import com.googlecode.reunion.jcommon.S_ParsedItem;
import com.googlecode.reunion.jreunion.game.Item;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ItemFactory {

	public static Item createItem(int type) {
		Item item = null;
		if (type == 0) {
			item = new Item(type);
			com.googlecode.reunion.jreunion.game.EntityManager
					.getEntityManager().createEntity(item);
			DatabaseUtils.getInstance().addItem(item);
			return item;
		}

		S_ParsedItem parseditem = Reference.getInstance().getItemReference()
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
		com.googlecode.reunion.jreunion.game.EntityManager.getEntityManager()
				.createEntity(item);
		DatabaseUtils.getInstance().addItem(item);
		return item;
	}

	public static Item loadItem(int id) {

		int type = DatabaseUtils.getInstance().getItemType(id);
		if (type < 0) {
			return null;
		}

		S_ParsedItem parseditem = Reference.getInstance().getItemReference()
				.getItemById(type);
		if (parseditem == null) {
			System.out.println("Item loaded failed, no such item type!");
			return null;
		}

		String classname = parseditem.getMemberValue("Class");

		Item item = null;

		try {
			Class c = Class.forName("com.googlecode.reunion.jreunion.game." + classname);
			item = (Item) c.getConstructors()[0].newInstance(type);

		} catch (Exception e) {

			System.out.println("Cannot create class:" + classname);
			e.printStackTrace();
			return null;
		}
		com.googlecode.reunion.jreunion.game.EntityManager.getEntityManager()
				.loadEntity(item, id);
		DatabaseUtils.getInstance().loadItemInfo(item);

		return item;

	}

	public ItemFactory() {
		super();

	}

}
