package com.googlecode.reunion.jreunion.events;

import java.lang.reflect.Constructor;

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
		
	
	public static  <T extends Event> T Create(Class<T> eventClass, EventDispatcher source, Object... args)
	{
		for(Constructor<?> constructor: eventClass.getConstructors()){
			Class<?> [] parameterTypes = constructor.getParameterTypes();
			if(parameterTypes.length!=args.length){
				continue;
			}
			for(int i=0;i < args.length; i++){
				Object obj = args[i];
				if(obj!=null){
					if(!obj.getClass().isAssignableFrom(parameterTypes[i])){
						continue;
					}
				}
			}
			T event = null;
			try {
				Constructor<T> parameterizedConstructor = (Constructor<T>) eventClass.getConstructor(parameterTypes);
				event = parameterizedConstructor.newInstance(args);		
				event.setSource(source);
				return event;
			} catch (Exception e) {
				Logger.getLogger(Event.class).error("Exception",e);
				throw new RuntimeException(e);
			}	
		}
		throw new RuntimeException("No matching constructors found on: "+eventClass);
		
	}

}
