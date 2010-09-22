package com.googlecode.reunion.jreunion.events;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Event {
	
	public EventBroadcaster getSource() {
		return source;
	}
	EventBroadcaster source;
	
	private void setSource(EventBroadcaster source) {
		this.source = source;
	}

	protected Event() {
	}
		
	public  static  <T extends Event> T Create(Class<T> cl, EventBroadcaster source, Object... args)
	{
		Event event = null;
		try {
			
			event = (Event)cl.getConstructors()[0].newInstance(args);		
			event.setSource(source);
			return (T) event;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
