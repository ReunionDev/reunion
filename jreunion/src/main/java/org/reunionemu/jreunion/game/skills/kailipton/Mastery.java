package org.reunionemu.jreunion.game.skills.kailipton;

import java.util.List;
import java.util.Vector;

import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.skills.GroupedSkill;
import org.reunionemu.jreunion.game.skills.Modifier;
import org.reunionemu.jreunion.server.SkillManager;

public abstract class Mastery extends GroupedSkill implements Modifier{
	
	public Mastery(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int level) {
		return 24+level;
	}

	@Override
	public int getAffectedTargets() {
		return 1;
	}
	
	@Override
	public int[] getSkillsInGroup() {
		return new int[]{8,11,14};
	}
	
	public float getDamageModifier(Player player){
		
		float modifier = 1;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (0.1+((level-1)*getDamageModifier()));			
		}				
		return modifier;
	}
	
	public float getDamageModifier(){
		/*
		 * lvl 1 = 10%
		 * lvl 2 = 17%
		 * ...
		 * lvl 25 = 200%
		 */
		
		return 1.90f/(getMaxLevel()-1);		
		
	}
	
	public abstract ValueType getValueType();
	
	public boolean getCondition(LivingObject owner){
		if(owner instanceof Player){
			Player player = (Player)owner;
			if(player.getSkillLevel(this)==0)
				return false;
			return true;
		}
		return false;
	}

	@Override
	public ModifierType getModifierType() {

		return Modifier.ModifierType.MULTIPLICATIVE;
	}
	
	private int [] affectedSkillIds = {3, 4, 12, 5, 10, 13, 26, 27, 28};
	private List<Skill>  affectedSkills = null ;
	
	@Override
	public List<Skill> getAffectedSkills() {
		synchronized(affectedSkillIds){
			if (affectedSkills==null){
				affectedSkills = new Vector<Skill>();
				for(int skillId:affectedSkillIds){					
					SkillManager skillManager = getSkillManager();
					affectedSkills.add(skillManager.getSkill(skillId));					
				}
			}		
		}		
		return affectedSkills;
	}

	@Override
	public float getModifier(LivingObject livingObject) {
		return getDamageModifier((Player)livingObject);
	}

}
