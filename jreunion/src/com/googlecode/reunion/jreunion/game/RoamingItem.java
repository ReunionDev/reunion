package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Session;

public class RoamingItem extends WorldObject{

	private Item item;
	public RoamingItem(Item item) {
		this.setItem(item);
	}
	private void setItem(Item item) {
		this.item = item;
	}
	public Item getItem() {
		return item;
	}
	@Override
	public void enter(Session session) {
		this.getPosition().getMap().getWorld().getCommand().itemIn(session.getOwner(), this);
		
	}
	@Override
	public void exit(Session session) {
		this.getPosition().getMap().getWorld().getCommand().itemOut(session.getOwner(), this);
		
	}
	
	@Override
	public int getEntityId() {		
		return item.getEntityId();
	}

}
