package com.googlecode.reunion.jreunion.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.G_Entity;
import com.googlecode.reunion.jreunion.game.G_LivingObject;
import com.googlecode.reunion.jreunion.game.G_Mob;
import com.googlecode.reunion.jreunion.game.G_Npc;
import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.game.G_WorldObject;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Session {
	
	private List<G_WorldObject> entities = new Vector<G_WorldObject>();

	private boolean sessionActive = false;

	private G_Player owner;

	public S_Session(G_Player player) {

		super();
		player.setSession(this);
		owner = player;
		setActive(true);
	}

	public void close() {
		this.empty();
		setActive(false);
	}

	public boolean contains(G_WorldObject entity) {
		
		return entities.contains(entity);
			
	}


	public boolean getActive() {
		return sessionActive;
	}


	/**
	 * @return Returns the sessionOwner.
	 * @uml.property name="sessionOwner"
	 */
	public G_Player getOwner() {
		return owner;
	}

	public void setActive(boolean sessionActive) {
		this.sessionActive = sessionActive;
	}
	
	public void exit(G_WorldObject entity){
		
		if (!entities.contains(entity)) {
			return;
		}
		while (entities.contains(entity)) {
			entities.remove(entity);
		}
		entity.exit(this);
	}
	
	
	public void enter(G_WorldObject entity){
		if(this.contains(entity))
			return;
		entities.add(entity);
		entity.enter(this);
		
	}

	public void empty() {
		
		List<G_WorldObject> tmpEntities = new Vector<G_WorldObject>();
		Collections.copy(tmpEntities, entities);
		for(G_WorldObject object:tmpEntities)
		{		
			exit(object);			
		}
		entities.clear();
		
	}

	public Iterator<G_WorldObject> getPlayerListIterator() {
		
		List<G_WorldObject> players = new ArrayList<G_WorldObject>(entities);
		Iterator<G_WorldObject> iter = players.iterator();
		while (iter.hasNext()) {
		G_WorldObject player = iter.next(); 
			if(!(player instanceof G_Player))
				iter.remove();
		}
		return players.iterator();
	}
}