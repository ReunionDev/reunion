package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.events.EventBroadcaster;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Session;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class WorldObject extends EventBroadcaster implements Entity {
	
	private int id = -1;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;		
	}
	
	public void update(){
		LocalMap map = position.getMap();
		
		synchronized(map){			
			map.notify();			
		}
		
	}
		
	private Position position = new Position();

	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	
	public abstract void enter(Session session);
	
	public abstract void exit(Session session);

}
