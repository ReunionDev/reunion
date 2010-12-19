package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class Skill {
	private int id;

	private int type;

	public Skill(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}


	public abstract int  getMaxLevel();

	public int getType() {
		return type;
	}

	public boolean levelUp(Player player) {
	
		synchronized(player){			
			
			java.util.Map<Skill,Integer> skills = player.getSkills();
			
			if(!skills.containsKey(this))
				return false; //cheater?
		
			int currentSkillLevel = skills.get(this);
			
			if(currentSkillLevel < this.getMaxLevel() && this.getLevelRequirement(currentSkillLevel+1) <= player.getLevel()){
				
				skills.put(this, ++currentSkillLevel);
				
				player.getClient().sendPacket(Type.SKILLLEVEL, this, currentSkillLevel);
				return true;
			}
			return false;
		}
	}
	
	
	public void effect(LivingObject source, LivingObject target){
		source.getInterested().sendPacket(Type.EFFECT, source, target, this);
		target.getInterested().sendPacket(Type.EFFECT, source, target, this);
	}

	public abstract int getLevelRequirement(int skillLevel);

	

	public void setType(int type) {
		this.type = type;
	}
}