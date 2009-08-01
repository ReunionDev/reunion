package com.googlecode.reunion.jreunion.game;


import com.googlecode.reunion.jreunion.server.*;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Spawn {
	private int spawnId;
	
	private int mobType;

	private int respawnTime; // seconds

	private S_Timer diedTime = new S_Timer(); // seconds

	private boolean dead ;
	
	private G_Mob mob;
	
	private int centerX;
	
	private int centerY;
	
	private int radius;
	
	private S_Map map;
	
	public G_Spawn() {
		//spawnMob();
	}
	
	/**
	 * @return Returns the mobType.
	 * @uml.property name="mobType"
	 */
	public int getMobType() {
		return this.mobType;
	}

	/**
	 * @param mobType
	 *            The mobType to set.
	 * @uml.property name="mobType"
	 */
	public void setMobType(int mobType) {
		this.mobType = mobType;
	}

	/**
	 * @return Returns the respawnTime.
	 */
	public int getRespawnTime() {
		return this.respawnTime;
	}

	/**
	 * @param respawnTime
	 *            The respawnTime to set.
	 */
	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}

	/**
	 * @return Returns the diedTime.
	 */
	public S_Timer getDiedTime() {
		return this.diedTime;
	}

	public boolean readyToSpawn() {
		if(getDead() == true){
			if(diedTime.isRunning() == false){
				diedTime.Start();
				return false;
			}
			else
			  if(diedTime.getTimeElapsedSeconds() >= getRespawnTime())
				  return true;
		}
		return false;
	}
	
	/**
	 * @return Returns the dead.
	 */
	public boolean getDead() {
		return this.dead;
	}

	/**
	 * @param dead
	 *            The dead to set.
	 */
	public void setDead(boolean dead) {
		this.dead = dead;
	}
		
	public void spawnMob(){

		G_Mob newMob = S_Server.getInstance().getWorldModule().getMobManager().createMob(getMobType());
		 
		newMob.setPosX(centerX );//+ ((int)(Math.random()*radius)));
		newMob.setPosY(centerY);// + ((int)(Math.random()*radius)));
		newMob.setMap(map);
		setMob(newMob);
		
		setDead(false);
		this.diedTime.Stop();
		
	}
	
	public void setMob(G_Mob mob){
		this.mob = mob;
	}
	public G_Mob getMob(){
		return this.mob;
	}
	
	public void setCenterX(int centerX){
		this.centerX = centerX;
	}
	public int getCenterX(){
		return this.centerX;
	}
	public void setCenterY(int centerY){
		this.centerY = centerY;
	}
	public int getCenterY(){
		return this.centerY;
	}
	public void setRadius(int radius){
		this.radius = radius;
	}
	public int getRadius(){
		return this.radius;
	}

	public S_Map getMap(){
		return this.map;
	}

	/**
	 * @return Returns the spawnId.
	 */
	public int getSpawnId() {
		return spawnId;
	}

	/**
	 * @param map The map to set.
	 */
	public void setMap(S_Map map) {
		this.map = map;
	}


}