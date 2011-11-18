package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Session;

public class RoamingItem extends WorldObject{

	private Item<?> item;
	private Player owner;
	
	public RoamingItem(Item<?> item) {
		this.setItem(item);
	}	
	
	private void setItem(Item<?> item) {
		this.item = item;
	}
	public Item<?> getItem() {
		return item;
	}
	@Override
	public void enter(Session session) {
		session.getOwner().getClient().sendPacket(Type.IN_ITEM, this);
		
	}
	@Override
	public void exit(Session session) {
		session.getOwner().getClient().sendPacket(Type.OUT, this);
	}
	
	
	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Player getOwner() {
		return owner;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");

		buffer.append("id:");
		buffer.append(getEntityId());
		buffer.append(", ");
		
		buffer.append("item: ");
		buffer.append("{");
		
		buffer.append("id:");
		buffer.append(getItem().getEntityId());
		buffer.append("("+getItem().getItemId()+")");
		buffer.append(", ");
		
		buffer.append("name:");
		buffer.append(getItem().getType().getName());	
				
		buffer.append("} ");
				
		buffer.append("}");
		return buffer.toString();
	}
	
	public void delete(){
		LocalMap map = getPosition().getLocalMap();
		
		map.removeRoamingItem(this);
		map.removeEntity(map.getEntity(getItem().getEntityId()));
		DatabaseUtils.getDinamicInstance().deleteItem(getItem().getItemId());
		getInterested().sendPacket(Type.OUT, this);
	}
}
