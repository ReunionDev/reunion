package org.reunionemu.jreunion.server;

import java.util.ArrayList;
import java.util.List;

import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.events.EventDispatcher;
import org.reunionemu.jreunion.events.EventListener;

public class Service extends EventDispatcher implements EventListener {
	static List<Class<?>> services = new ArrayList<Class<?>>();
	
	
	public String getName(){		
		return this.getClass().getName();
		
	}
	
	
	@Override
	public void handleEvent(Event event) {
		
	}
	
	
	static void registerService(Class<?> cl){
		if(services.contains(cl))
			return;		
		services.add(cl);		
	}
	static Service getService(String name){
		
		return null;
		
		
	}

}
