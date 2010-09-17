package com.googlecode.reunion.jreunion.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.Entity;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.WorldObject;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Session {
	
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

	public boolean contains(WorldObject entity) {
		
		return entities.contains(entity);
			
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
		
		if (!entities.contains(entity)) {
			return;
		}
		while (entities.contains(entity)) {
			entities.remove(entity);
		}
		entity.exit(this);
	}
	
	
	public void enter(WorldObject entity){
		if(this.contains(entity))
			return;
		entities.add(entity);
		entity.enter(this);
		
	}

	public void empty() {
		
		List<WorldObject> tmpEntities = new Vector<WorldObject>();
		Collections.copy(tmpEntities, entities);
		for(WorldObject object:tmpEntities)
		{		
			exit(object);			
		}
		entities.clear();
		
	}

	public Iterator<WorldObject> getPlayerListIterator() {
		
		List<WorldObject> players = new ArrayList<WorldObject>(entities);
		Iterator<WorldObject> iter = players.iterator();
		while (iter.hasNext()) {
		WorldObject player = iter.next(); 
			if(!(player instanceof Player))
				iter.remove();
		}
		return players.iterator();
	}
}