package org.reunionemu.jreunion.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jreunion.game.items.ItemPlaceHolder;

public class ClassFactory {

	public static Object create(String className, Object...args){
		
		try {
			Class<?> c = Class.forName(className);
			return ClassFactory.create(c, args);

		} catch (Exception e) {
			if(className.contains("items")){
				LoggerFactory.getLogger(ClassFactory.class).error("Failed to load item class: "+className+
						" using ItemPlaceHolder");
				ItemPlaceHolder itemPlaceHolder = new ItemPlaceHolder(Integer.parseInt(args[0]+""));
				return itemPlaceHolder;
			}
			LoggerFactory.getLogger(ClassFactory.class).error("Cannot create class: " + className, e);
			return null;
		}		
	}

	public static Object create(Class<?> c, Object...args) {

		try {
			return c.getConstructors()[0].newInstance(args);

		} catch (Exception e) {
			LoggerFactory.getLogger(ClassFactory.class).error("Cannot create class: " + c, e);
			return null;
		}		
	}
}
