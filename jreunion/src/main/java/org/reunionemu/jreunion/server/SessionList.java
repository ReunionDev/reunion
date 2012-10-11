package org.reunionemu.jreunion.server;

import java.util.LinkedList;

import org.reunionemu.jreunion.game.WorldObject;
import org.reunionemu.jreunion.server.PacketFactory.Type;

public class SessionList<T extends Session> extends LinkedList<T> implements Sendable{

	public SessionList() {
	}

	public SessionList(SessionList<T> sessions) {
		super(sessions);
	}

	@Override
	public void sendPacket(Type packetType, Object... args) {
		synchronized(this){
			for(Session session : this){
				session.getOwner().getClient().sendPacket(packetType, args);
			}				
		}
		
	}
	
	public void exit(WorldObject entity){
		synchronized(this){
			for(Session session : this){
				
				session.exit(entity);
			}
		}
		
	}
	
	public void exit(WorldObject entity, boolean defaultAction){

		synchronized(this){
			for(Session session : this){
				
				session.exit(entity, defaultAction);
				
			}
		}
	}
	public void enter(WorldObject entity){
		synchronized(this){
			for(Session session : this){
				
				session.enter(entity);
			}
		}
	
	}
	public void enter(WorldObject entity, boolean defaultAction){
		
		synchronized(this){
			for(Session session : this){
				
				session.enter(entity, defaultAction);
				
			}
		}
	}
	
	public void update(){
		synchronized(this){
			for(Session session : this){
				session.getOwner().update();
			}				
		}
	}
}
