package com.googlecode.reunion.jreunion.events.map;

import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.server.LocalMap;

public class MapEvent extends Event {
		
	LocalMap map;
	
	public MapEvent(LocalMap map){
		this.map = map;
		
	}

	public LocalMap getMap() {
		return map;
	}

}
