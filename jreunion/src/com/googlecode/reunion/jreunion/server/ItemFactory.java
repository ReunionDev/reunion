package com.googlecode.reunion.jreunion.server;

import com.googlecode.reunion.jcommon.S_ParsedItem;
import com.googlecode.reunion.jreunion.game.G_Item;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ItemFactory {

	public static G_Item createItem(int type) {
		G_Item item = null;
		if (type == 0) {
			item = new G_Item(type);
			com.googlecode.reunion.jreunion.game.G_EntityManager
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
			item = (G_Item) c.getConstructors()[0].newInstance(type);

		} catch (Exception e) {

			System.out.println("Cannot create class:" + classname);
			e.printStackTrace();
			return null;
		}
		com.googlecode.reunion.jreunion.game.G_EntityManager.getEntityManager()
				.createEntity(item);
		DatabaseUtils.getInstance().addItem(item);
		return item;
	}

	public static G_Item loadItem(int id) {

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

		G_Item item = null;

		try {
			Class c = Class.forName("com.googlecode.reunion.jreunion.game." + classname);
			item = (G_Item) c.getConstructors()[0].newInstance(type);

		} catch (Exception e) {

			System.out.println("Cannot create class:" + classname);
			e.printStackTrace();
			return null;
		}
		com.googlecode.reunion.jreunion.game.G_EntityManager.getEntityManager()
				.loadEntity(item, id);
		DatabaseUtils.getInstance().loadItemInfo(item);

		return item;

	}

	public ItemFactory() {
		super();

	}

}
