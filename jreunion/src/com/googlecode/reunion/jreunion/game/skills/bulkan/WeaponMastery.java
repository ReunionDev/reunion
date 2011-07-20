package com.googlecode.reunion.jreunion.game.skills.bulkan;

import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.game.skills.Modifier;
import com.googlecode.reunion.jreunion.server.SkillManager;

public abstract class WeaponMastery extends Skill implements Modifier {
	
	
	public WeaponMastery(SkillManager skillManager,int id) {
		super(skillManager, id);
	}
	
	public abstract Class<?> getWeaponType();
	

	public float getDamageModifier(Player player){
		
		float modifier = 1;
	
		Weapon weapon = player.getEquipment().getMainHand();
		
		if(weapon!=null&&getWeaponType().isInstance(weapon)){
		
			int level = player.getSkillLevel(this);
			if(level>0){
				modifier += (0.1+((level-1)*getDamageModifier()));			
			}				
		}
	
		return modifier;
	}
	public float getDamageModifier(){
		/*
		 * lvl 1 = 10%
		 * lvl 2 = 17%
		 * 
		 * 
		 * lvl 25 = 200%
		 * 
		 * 1.90 = 200% - 10%
		 * 24 = m
		 */
		
		return 1.90f/(getMaxLevel()-1);		
		
	}
	
	public boolean getCondition(LivingObject owner){
		
		if(owner instanceof Player){
			Player player = (Player)owner;
			Weapon weapon= player.getEquipment().getMainHand();
			return weapon!=null && getWeaponType().isInstance(weapon);			
		}		
		return false;		
	}
	
	@Override
	public int getLevelRequirement(int level) {
		return 4+level;
	}	
	
	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public ValueType getValueType() {
		return Modifier.ValueType.DAMAGE;
		
	}

	@Override
	public ModifierType getModifierType() {

		return Modifier.ModifierType.MULTIPLICATIVE;
	}

	private int [] affectedSkillIds = {0, 18, 31};
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
