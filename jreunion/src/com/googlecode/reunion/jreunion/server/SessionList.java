package com.googlecode.reunion.jreunion.server;

import java.util.Vector;

import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

public class SessionList<T extends Session> extends Vector<T> implements Sendable{

	public SessionList() {
	}

	@Override
	public void sendPacket(Type packetType, Object... args) {
		synchronized(this){
			for(Session session : this){
				
				session.getOwner().getClient().sendPacket(packetType, args);
				
			}
				
		}
		
	}
	
}
