package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.SkillManager;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class Skill {
	private int id;

	private int type;
	
	private SkillManager skillManager;

	public Skill(SkillManager skillManager, int id) {
		this.setSkillManager(skillManager);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public abstract int  getMaxLevel();
	
	public int getMinLevel(){
		return 0;
	}

	public int getType() {
		return type;
	}
	
	public void reset(Player player){
		
		int skillLevel = player.getSkillLevel(this);
		
		//if skill current level is zero, there is no need to reset it.
		if(skillLevel == 0)
			return;
		
		int min = getMinLevel();
		player.setSkillLevel(this, min);
		player.getClient().sendPacket(Type.SKILLLEVEL, this, min);
		player.setStatusPoints(player.getStatusPoints() + skillLevel - min);
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

	public SkillManager getSkillManager() {
		return skillManager;
	}

	private void setSkillManager(SkillManager skillManager) {
		this.skillManager = skillManager;
	}
}