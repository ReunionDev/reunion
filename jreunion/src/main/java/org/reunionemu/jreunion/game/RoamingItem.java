package org.reunionemu.jreunion.game;

import java.util.*;

import org.reunionemu.jreunion.dao.RoamingItemDao;
import org.reunionemu.jreunion.server.*;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;

@Configurable
public abstract class RoamingItem extends WorldObject{
	
	@Autowired 
	RoamingItemDao<RoamingItem> roamingItemDao;

	private java.util.Timer deleteTimer = new java.util.Timer();
	
	public RoamingItem(){
		
	}
	
	public abstract Date getCreated();
	
	public abstract void setCreated(Date created);
	
	@Override
	public Integer getEntityId() {
		return getItem().getEntityId();
	}
	
	@Override
	public void setEntityId(Integer id) {
		getItem().setEntityId(id);
	}
		
	protected abstract void setItem(Item<?> item);
	
	public abstract Item<?> getItem();
	
	@Override
	public void enter(Session session) {
		session.getOwner().getClient().sendPacket(Type.IN_ITEM, this);
		
	}
	@Override
	public void exit(Session session) {
		session.getOwner().getClient().sendPacket(Type.OUT, this);
	}
	
	
	public abstract void setOwner(Player owner);

	public abstract Player getOwner();

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
		roamingItemDao.delete(this);
		//Database.getInstance().deleteRoamingItem(item);
		map.removeEntity(item);
		//item.delete();
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
