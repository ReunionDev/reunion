package com.googlecode.reunion.jreunion.server;

import java.util.LinkedList;

import com.googlecode.reunion.jreunion.game.WorldObject;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

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
	
}
