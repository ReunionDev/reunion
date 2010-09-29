package com.googlecode.reunion.jreunion.game;

import java.util.Random;
import java.util.TimerTask;

import com.googlecode.reunion.jreunion.events.map.SpawnEvent;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Timer;

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

	private Type type;
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public static enum Type{
		PLAYER,
		NPC,
		MOB,
	}
	

	private int radius;
	
	private Position position;

	public Spawn() {
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
		LocalMap map = position.getMap();
		
		int posX = (radius>0?rand.nextInt(radius * 2) - radius:0)+position.getX();
		int posY = (radius>0?rand.nextInt(radius * 2) - radius:0)+position.getY();
		
		double rotation = position.getRotation();
		
		if(Double.isNaN(rotation))
			rotation = Server.getRand().nextDouble() * Math.PI * 2;
		
		return new Position(posX, posY, position.getZ(), map, rotation);
		
	}

	protected void spawn(LivingObject entity) {		
		
		
		LocalMap map = position.getMap();
		map.fireEvent(SpawnEvent.class, entity);
		
	}


}