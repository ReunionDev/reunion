package com.googlecode.reunion.jreunion.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventBroadcaster;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.map.MapEvent;
import com.googlecode.reunion.jreunion.events.session.SendPacketSessionEvent;
import com.googlecode.reunion.jreunion.events.session.SessionEvent;
import com.googlecode.reunion.jreunion.game.Entity;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.WorldObject;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Session extends EventBroadcaster implements EventListener{
	
	private List<WorldObject> entities = new Vector<WorldObject>();

	private boolean sessionActive = false;

	private Player owner;

	public Session(Player player) {

		super();
		player.setSession(this);
		owner = player;
		setActive(true);
	}

	public void close() {
		this.empty();
		setActive(false);
	}
	
	public boolean contains(Position position) {		
		
		Player owner = getOwner();
		return owner.getPosition().distance(position)<owner.getSessionRadius();

	}

	public boolean contains(WorldObject entity) {
		synchronized(entities){
			return entities.contains(entity);
		}
	}

	public boolean getActive() {
		return sessionActive;
	}

	/**
	 * @return Returns the sessionOwner.
	 * @uml.property name="sessionOwner"
	 */
	public Player getOwner() {
		return owner;
	}

	public void setActive(boolean sessionActive) {
		this.sessionActive = sessionActive;
	}
	
	public void exit(WorldObject entity){
		
		exit(entity, true);
		
	}
	
	public void exit(WorldObject entity, boolean defaultAction){

		Logger.getLogger(Session.class).debug("exit "+getOwner()+" "+entity);
		synchronized(entities){
			if (!entities.contains(entity)) {
				return;
			}
			while (entities.contains(entity)) {
				entities.remove(entity);
			}
			entity.addEventListener(SessionEvent.class, this);
			if(defaultAction)
				entity.exit(this);
		}
	}
	public void enter(WorldObject entity){
		enter(entity, true);
	
	}
	public void enter(WorldObject entity, boolean defaultAction){
		
		Logger.getLogger(Session.class).debug("enter "+getOwner().getName()+" "+entity);
		synchronized(entities){
			if(this.contains(entity))
				return;
		
			entity.addEventListener(SessionEvent.class, this);
			entities.add(entity);
			if(defaultAction)
				entity.enter(this);
			
		}
	}

	public void empty() {
		
		List<WorldObject> tmpEntities = new Vector<WorldObject>(entities);
		for(WorldObject object:tmpEntities)
		{		
			exit(object);			
		}
		entities.clear();
		
	}

	@Override
	public void handleEvent(Event event) {
		
		if(event instanceof MapEvent){
			LocalMap map = ((MapEvent)event).getMap();
		}
		
		if(event instanceof SessionEvent){
			Session session = ((SessionEvent)event).getSession();
			if(event instanceof SendPacketSessionEvent){
				SendPacketSessionEvent sendPacketSessionEvent = (SendPacketSessionEvent)event;
				String data = sendPacketSessionEvent.getData();
				getOwner().getClient().sendData(data);
			}
		}		
	}
}