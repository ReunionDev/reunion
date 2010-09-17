package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.Session;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class WorldObject extends Entity{
	
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
