package com.googlecode.reunion.jreunion.game;

import java.util.Random;
import java.util.TimerTask;

import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Timer;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Spawn {
	private int spawnId;

	private int mobType;

	private float respawnTime; // seconds

	private int centerX;

	private int centerY;

	private int radius;

	private LocalMap map;

	public Spawn() {
	}

	public int getCenterX() {
		return centerX;
	}

	public int getCenterY() {
		return centerY;
	}


	public LocalMap getMap() {
		return map;
	}


	/**
	 * @return Returns the mobType.
	 * @uml.property name="mobType"
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
	 * @return Returns the spawnId.
	 */
	public int getSpawnId() {
		return spawnId;
	}
	
	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	/**
	 * @param map
	 *            The map to set.
	 */
	public void setMap(LocalMap map) {
		this.map = map;
	}

	

	/**
	 * @param mobType
	 *            The mobType to set.
	 * @uml.property name="mobType"
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
		
		Mob newMob = getMap().getWorld().getMobManager()
				.createMob(getMobType());
		newMob.setSpawn(this);
		
		int posX = rand.nextInt(radius * 2)-radius+centerX;
		int posY = rand.nextInt(radius * 2)-radius+centerY;
		double rotation = rand.nextDouble() *Math.PI*2;
		Position position = new Position(posX, posY, 0, getMap(), rotation);
		newMob.setPosition(position);
		
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