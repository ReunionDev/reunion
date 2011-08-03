package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.events.EventDispatcher;
import com.googlecode.reunion.jreunion.events.session.SendPacketSessionEvent;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.PacketFactory;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Sendable;
import com.googlecode.reunion.jreunion.server.Session;
import com.googlecode.reunion.jreunion.server.SessionList;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class WorldObject extends EventDispatcher implements Entity {
	
	private int id = -1;
	public int getEntityId() {
		return id;
	}
	
	private Interested interested = new Interested(this);

	public Interested getInterested() {
		return interested;
	}

	public void setEntityId(int id) {
		this.id = id;		
	}
	
	public void update(){
		LocalMap map = position.getLocalMap();
		
		synchronized(map){			
			map.notify();			
		}
	}
	
	private Position position = new Position();

	public Position getPosition() {
		return position;
	}
	
	public void setPosition(Position position) {
		this.position = position;
	}
	
	public abstract void enter(Session session);
	
	public abstract void exit(Session session);
	
	public class Interested implements Sendable{

		private WorldObject entity;
		
		public WorldObject getEntity() {
			return entity;
		}
		public Interested(WorldObject entity){
			this.entity = entity;
			
		}
		public SessionList<Session> getSessions(){
			return entity.getPosition().getLocalMap().GetSessions(entity);
			
		}
		
		@Override
		public void sendPacket(Type packetType, Object... args) {
			String data = PacketFactory.createPacket(packetType, args);
			entity.fireEvent(SendPacketSessionEvent.class, null, data);
			
		}
				
	}
}
