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
		Npc npc = null;
		switch(getType()){
		case MOB:
			npc = map.getWorld().getMobManager().createMob(getNpcType());				
			break;
			
		case NPC:
			npc = map.getWorld().getNpcManager().createNpc(getNpcType());
			//npc.setUnknown2(10);
			break;
		}
		if(npc==null)
			return;
		npc.setSpawn(this);
		npc.setPosition(entityPosition);
		npc.loadFromReference(getNpcType());
		
		super.spawn(npc);
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
