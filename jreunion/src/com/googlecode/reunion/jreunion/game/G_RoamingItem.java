package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.S_Server;
import com.googlecode.reunion.jreunion.server.S_Session;

public class G_RoamingItem extends G_WorldObject{

	private G_Item item;
	public G_RoamingItem(G_Item item) {
		this.setItem(item);
	}
	private void setItem(G_Item item) {
		this.item = item;
	}
	public G_Item getItem() {
		return item;
	}
	@Override
	public void enter(S_Session session) {
		S_Server.getInstance().getWorldModule().getWorldCommand().itemIn(session.getOwner(), this);
		
	}
	@Override
	public void exit(S_Session session) {
		S_Server.getInstance().getWorldModule().getWorldCommand().itemOut(session.getOwner(), this);
		
	}
	
	@Override
	public int getEntityId() {		
		return item.getEntityId();
	}

}
