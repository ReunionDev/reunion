package com.googlecode.reunion.jreunion.game;

import java.util.Random;
import java.util.TimerTask;

import com.googlecode.reunion.jreunion.events.map.MobSpawnEvent;
import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Timer;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Spawn {

	private int mobType;

	private float respawnTime; // seconds

	private int centerX;

	private int centerY;

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
	public int getMobType() {
		return mobType;
	}

	public int getRadius() {
		return radius;
	}

	/**
	 * @return Returns the respawnTime.
	 */
	public float getRespawnTime() {
		return respawnTime;
	}
	
	/**
	 * @param map
	 *            The map to set.
	 */

	/**
	 * @param mobType
	 *            The mobType to set.
	 */
	public void setMobType(int mobType) {
		this.mobType = mobType;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * @param respawnTime
	 *            The respawnTime to set.
	 */
	public void setRespawnTime(float respawnTime) {
		this.respawnTime = respawnTime;
	}
	
	private Random rand = new Random(System.currentTimeMillis());

	public void spawnMob() {		
		
		
		Position position = getPosition();
		
		LocalMap map = position.getMap();
		
		Mob mob = map.getWorld().getMobManager()
				.createMob(getMobType());
		mob.setSpawn(this);
		
		int posX = rand.nextInt(radius * 2) - radius + centerX;
		int posY = rand.nextInt(radius * 2) - radius + centerY;
		double rotation = rand.nextDouble() * Math.PI * 2;
		
		
		Position mobPosition = new Position(posX, posY, 0, map, rotation);
		mob.setPosition(mobPosition);
		map.fireEvent(MobSpawnEvent.class, mob);
		
	}

	public void kill() {
		
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				spawnMob();				
			}
			
		}, (long) (respawnTime*1000));
	}
}