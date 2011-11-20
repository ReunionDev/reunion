package com.googlecode.reunion.jreunion.game;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.SessionList;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Session;

public class RoamingItem extends WorldObject{

	private Item<?> item;
	private Player owner;
	private java.util.Timer deleteRoamingItemTimer = new java.util.Timer();
	
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
		buffer.append(getItem());
		buffer.append(", ");
		
		buffer.append("map: ");
		buffer.append(getPosition().getLocalMap());
				
		buffer.append("}");
		return buffer.toString();
	}
	
	public void delete(){
		LocalMap map = getPosition().getLocalMap();
		SessionList<Session> list = map.GetSessions(getPosition());
		
		map.removeEntity(this);
		DatabaseUtils.getDinamicInstance().deleteRoamingItem(getItem());
		map.removeEntity(this.getItem());
		DatabaseUtils.getDinamicInstance().deleteItem(getItem().getItemId());
		list.exit(this, false);
		getInterested().sendPacket(Type.OUT, this);
	}
	
	public void startDeleteTimer(boolean randomTime){
		long dropTimeOut = getPosition().getLocalMap().getWorld().getServerSetings().getDropTimeOut();
		long extraTime = 0;
		
		if(randomTime){
			extraTime = (long)(dropTimeOut * Math.random());
		}
		
		deleteRoamingItemTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Logger.getLogger(RoamingItem.class).info("Server deleted roaming item {id:"
						+getEntityId()+", item:"+getItem()+", map:"+getPosition().getLocalMap()+"}");
				delete();
				
			}
		},(dropTimeOut+extraTime)*1000);
	}
	
	public void stopDeleteTimer(){
		deleteRoamingItemTimer.cancel();
	}
}
