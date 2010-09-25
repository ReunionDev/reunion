package com.googlecode.reunion.jreunion.game;

import java.util.TimerTask;

import com.googlecode.reunion.jreunion.server.LocalMap;

public class NpcSpawn extends Spawn 
{
	public NpcSpawn(){
		
	}
	
	private int npcType;
	
	public void setNpcType(int mobType) {
		this.npcType = mobType;
	}
	public int getNpcType() {
		return npcType;
	}
	
	/**
	 * @param respawnTime
	 *            The respawnTime to set.
	 */
	public void setRespawnTime(float respawnTime) {
		this.respawnTime = respawnTime;
	}
	
	private float respawnTime; // seconds
	
	/**
	 * @return Returns the respawnTime.
	 */
	public float getRespawnTime() {
		return respawnTime;
	}
	
	
	public void spawn() {
		
		LocalMap map = getPosition().getMap();
		Position entityPosition = generatePosition();
		LivingObject entity = null;
		switch(getType()){
		case MOB:
			
			Mob mob = map.getWorld().getMobManager()
			.createMob(getNpcType());
			mob.setSpawn(this);
			
			entity = mob;
			
			break;
			
		case NPC:
			
			Npc npc = map.getWorld().getNpcManager()
			.createNpc(getNpcType());
			npc.setSpawn(this);			
			entity = npc;
			break;
		}
		
		entity.setPosition(entityPosition);
		super.spawn(entity);
	}
	
	public void kill() {
		
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				spawn();				
			}
			
		}, (long) (respawnTime*1000));
	}

}
