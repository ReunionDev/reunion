package com.googlecode.reunion.jreunion.server;

import org.apache.log4j.Logger;

public class ClassFactory {

	public static Object create(String className, Object...args){
		
		try {
			Class<?> c = Class.forName(className);
			return ClassFactory.create(c, args);

		} catch (Exception e) {
			Logger.getLogger(ClassFactory.class).error("Cannot create class: " + className, e);
			return null;
		}		
	}

	public static Object create(Class<?> c, Object...args) {

		try {
			return c.getConstructors()[0].newInstance(args);

		} catch (Exception e) {
			Logger.getLogger(ClassFactory.class).error("Cannot create class: " + c, e);
			return null;
		}		
	}
}
