package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.S_Map;
import com.googlecode.reunion.jreunion.server.S_Server;
import com.googlecode.reunion.jreunion.server.S_Timer;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Spawn {
	private int spawnId;

	private int mobType;

	private int respawnTime; // seconds

	private S_Timer diedTime = new S_Timer(); // seconds

	private boolean dead;

	private G_Mob mob;

	private int centerX;

	private int centerY;

	private int radius;

	private S_Map map;

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
	public S_Timer getDiedTime() {
		return diedTime;
	}

	public S_Map getMap() {
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
	public void setMap(S_Map map) {
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

		G_Mob newMob = S_Server.getInstance().getWorldModule().getMobManager()
				.createMob(getMobType());

		newMob.setPosX(centerX);// + ((int)(Math.random()*radius)));
		newMob.setPosY(centerY);// + ((int)(Math.random()*radius)));
		newMob.setMap(map);
		setMob(newMob);

		setDead(false);
		diedTime.Stop();

	}

}