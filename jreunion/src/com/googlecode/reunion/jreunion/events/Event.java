package com.googlecode.reunion.jreunion.events;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Event {
	
	public EventDispatcher getSource() {
		return source;
	}
	EventDispatcher source;
	
	private void setSource(EventDispatcher source) {
		this.source = source;
	}

	protected Event() {
	}
		
	public  static  <T extends Event> T Create(Class<T> cl, EventDispatcher source, Object... args)
	{
		Event event = null;
		try {
			
			event = (Event)cl.getConstructors()[0].newInstance(args);		
			event.setSource(source);
			return (T) event;
		} catch (Exception e) {
			Logger.getLogger(Event.class).warn("Exception",e);
		}
		return null;
	}

}
