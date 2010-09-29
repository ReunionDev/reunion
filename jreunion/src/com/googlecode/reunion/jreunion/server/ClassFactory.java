package com.googlecode.reunion.jreunion.server;

import org.apache.log4j.Logger;

public class ClassFactory {

	public static Object create(String className, Object...args){
		
		try {
			Class<?> c = Class.forName(className);
			return c.getConstructors()[0].newInstance(args);

		} catch (Exception e) {
			Logger.getLogger(ClassFactory.class).warn("Cannot create class: " + className, e);
			return null;
		}		
	}
}
