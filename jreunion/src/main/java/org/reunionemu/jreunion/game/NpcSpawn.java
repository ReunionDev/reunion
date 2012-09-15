package org.reunionemu.jreunion.game;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.reunionemu.jreunion.game.npc.Merchant;
import org.reunionemu.jreunion.game.npc.Mob;
import org.reunionemu.jreunion.server.LocalMap;

public class NpcSpawn extends Spawn 
{
	public NpcSpawn(){
		
	}
	
	private int npcType;
	
	public void setNpcType(int npcType) {
		this.npcType = npcType;
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
	
	public Position spawn() {
		
		LocalMap map = getPosition().getLocalMap();
		Npc<?> npc = map.getWorld().getNpcManager().create(getNpcType());
		
		if(npc==null)
			return null;
		
		npc.setSpawn(this);
		
		Position position = super.spawn(npc);
		
		if(npc.getType() instanceof Merchant){
			npc.loadShop();
		} else if(npc.getType() instanceof Mob){
			if(position.getLocalMap().getMobsAI() != null){
				position.getLocalMap().stopMobsAI();
				position.getLocalMap().startMobsAI(1000);
			}
		}
		
		return position;
		
	}
	
	public void kill() {
		
		if(getRespawnTime() == -1)
			return;
		
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				spawn();
			}
			
		}, (long)getRespawnTime()*1000); //value in milliseconds
	}

}
