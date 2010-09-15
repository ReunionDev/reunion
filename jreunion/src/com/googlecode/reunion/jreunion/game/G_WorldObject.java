package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.S_Session;

public abstract class G_WorldObject extends G_Entity{
	
	private G_Position position = new G_Position();

	public G_Position getPosition() {
		return position;
	}
	public void setPosition(G_Position position) {
		this.position = position;
	}
	
	public abstract void enter(S_Session session);
	
	public abstract void exit(S_Session session);

}
