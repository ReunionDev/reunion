package org.reunionemu.jreunion.game;

import java.util.TimerTask;

import org.reunionemu.jreunion.game.npc.Merchant;
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
		
		Npc<?> npc = null;
		
		//npc = Npc.create(npcType);
		npc = map.getWorld().getNpcManager().create(getNpcType());
		
		if(npc==null)
			return null;
		
		npc.setSpawn(this);
		
		Position position = super.spawn(npc);
		
		if(npc.getType() instanceof Merchant){
			npc.loadShop();
			//((Merchant)npc.getType()).loadShop(npc);
		}
		
		return position;
		
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
