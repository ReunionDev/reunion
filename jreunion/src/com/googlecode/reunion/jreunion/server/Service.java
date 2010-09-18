package com.googlecode.reunion.jreunion.server;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventBroadcaster;
import com.googlecode.reunion.jreunion.events.EventListener;

public class Service extends EventBroadcaster implements EventListener {
	static List<Class> services = new ArrayList<Class>();
	
	
	public String getName(){		
		return this.getClass().getName();
		
	}
	
	
	@Override
	public void handleEvent(Event event) {
		
	}
	
	
	static void registerService(Class cl){
		if(services.contains(cl))
			return;		
		services.add(cl);		
	}
	static Service getService(String name){
		
		return null;
		
		
	}

}
