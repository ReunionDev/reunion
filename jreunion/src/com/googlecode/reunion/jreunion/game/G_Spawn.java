package com.googlecode.reunion.jreunion.game;

import java.util.Random;

import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Timer;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Spawn {
	private int spawnId;

	private int mobType;

	private int respawnTime; // seconds

	private Timer diedTime = new Timer(); // seconds

	private boolean dead;

	private G_Mob mob;

	private int centerX;

	private int centerY;

	private int radius;

	private Map map;

	public G_Spawn() {
		// spawnMob();
	}

	public int getCenterX() {
		return centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	/**
	 * @return Returns the dead.
	 */
	public boolean getDead() {
		return dead;
	}

	/**
	 * @return Returns the diedTime.
	 */
	public Timer getDiedTime() {
		return diedTime;
	}

	public Map getMap() {
		return map;
	}

	public G_Mob getMob() {
		return mob;
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
	public int getRespawnTime() {
		return respawnTime;
	}

	/**
	 * @return Returns the spawnId.
	 */
	public int getSpawnId() {
		return spawnId;
	}

	public boolean readyToSpawn() {
		if (getDead() == true) {
			if (diedTime.isRunning() == false) {
				diedTime.Start();
				return false;
			} else if (diedTime.getTimeElapsedSeconds() >= getRespawnTime()) {
				return true;
			}
		}
		return false;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	/**
	 * @param dead
	 *            The dead to set.
	 */
	public void setDead(boolean dead) {
		this.dead = dead;
	}

	/**
	 * @param map
	 *            The map to set.
	 */
	public void setMap(Map map) {
		this.map = map;
	}

	public void setMob(G_Mob mob) {
		this.mob = mob;
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
	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}

	public void spawnMob() {	
		
		Random rand = new Random(System.currentTimeMillis());
		
		G_Mob newMob = Server.getInstance().getWorldModule().getMobManager()
				.createMob(getMobType());
		
		newMob.getPosition().setX(rand.nextInt(radius * 2)-radius+centerX);
		newMob.getPosition().setY(rand.nextInt(radius * 2)-radius+centerY);
		newMob.getPosition().setMap(getMap());
		setMob(newMob);

		setDead(false);
		diedTime.Stop();

	}

}