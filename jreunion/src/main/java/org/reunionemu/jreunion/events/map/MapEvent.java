package org.reunionemu.jreunion.events.map;

import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.server.LocalMap;

public class MapEvent extends Event {
		
	LocalMap map;
	
	public MapEvent(LocalMap map){
		this.map = map;
		
	}

	public LocalMap getMap() {
		return map;
	}

}
