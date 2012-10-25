package org.reunionemu.jreunion.game;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jreunion.server.Database;
import org.reunionemu.jreunion.server.LocalMap;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.Session;
import org.reunionemu.jreunion.server.SessionList;

public class RoamingItem extends WorldObject{

	private Item<?> item;
	private Player owner;
	private java.util.Timer deleteTimer = new java.util.Timer();
	
	public RoamingItem(Item<?> item) {
		this.setItem(item);
		this.setEntityId(item.getEntityId());
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
		Item<?> item = this.getItem();
		Database.getInstance().deleteRoamingItem(item);
		map.removeEntity(item);
		item.delete();
		list.exit(this, true);
		list.update();
		//getInterested().sendPacket(Type.OUT, this);
	}
	
	public void startDeleteTimer(boolean randomTime){
		long dropTimeOut = getPosition().getLocalMap().getWorld().getServerSettings().getDropTimeOut();
		long extraTime = 0;
		
		if(randomTime){
			extraTime = (long)(dropTimeOut * Math.random());
		}
		
		deleteTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				LoggerFactory.getLogger(RoamingItem.class).info("Server deleted roaming item {id:"
						+getEntityId()+", item:"+getItem()+", map:"+getPosition().getLocalMap()+"}");
				delete();
				
			}
		},(dropTimeOut+extraTime)*1000);
	}
	
	public void stopDeleteTimer(){
		deleteTimer.cancel();
	}
	
	public void setDropExclusivity(Player player){
		java.util.Timer dropExclusivityTimer = new java.util.Timer();
		long dropExclusivity = player.getClient().getWorld().getServerSettings().getDropExclusivity();
		dropExclusivityTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				setOwner(null);
			}
		},dropExclusivity*1000);
	}
}
