package com.googlecode.reunion.jreunion.game;

import java.util.Random;
import java.util.TimerTask;

import com.googlecode.reunion.jreunion.events.map.SpawnEvent;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Spawn {


	private int id;	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	private int radius;
	
	private Position position;

	public Spawn() {
	}	
	
	public Spawn(Position position) {
		this.setPosition(position);
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	/**
	 * @return Returns the mobType.
	 */


	public int getRadius() {
		return radius;
	}



	/**
	 * @param mobType
	 *            The mobType to set.
	 */


	public void setRadius(int radius) {
		this.radius = radius;
	}

	private Random rand = new Random(System.currentTimeMillis());
	
	public Position generatePosition(){
		
		Position position = getPosition();		
		LocalMap map = position.getLocalMap();
		
		int posX = (radius>0?rand.nextInt(radius * 2) - radius:0)+position.getX();
		int posY = (radius>0?rand.nextInt(radius * 2) - radius:0)+position.getY();
		
		double rotation = position.getRotation();
		
		if(Double.isNaN(rotation))
			rotation = Server.getRand().nextDouble() * Math.PI * 2;
		
		return new Position(posX, posY, position.getZ(), map, rotation);
	}

	protected Position spawn(LivingObject entity) {
		
		Position position = getPosition();
		if(position==null)
			position = generatePosition();
		entity.setPosition(position);
		LocalMap map = position.getLocalMap();
		if(map.getEntity(entity.getEntityId())!=entity)
			map.createEntityId(entity);
		map.fireEvent(SpawnEvent.class, entity);
		return position;
		
	}
}