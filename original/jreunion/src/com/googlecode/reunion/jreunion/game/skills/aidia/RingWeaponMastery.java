package com.googlecode.reunion.jreunion.game.skills.aidia;

import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.RingWeapon;
import com.googlecode.reunion.jreunion.game.skills.Modifier;
import com.googlecode.reunion.jreunion.server.SkillManager;

public abstract class RingWeaponMastery extends Skill implements Modifier {
	
	
	public RingWeaponMastery(SkillManager skillManager,int id) {
		super(skillManager, id);
	}	
	
	@Override
	public abstract int getLevelRequirement(int level);
	
	@Override
	public abstract int getMaxLevel();

	public Class<?> getWeaponType(){
		return RingWeapon.class;
	}

	public float getDamageModifier(){
		/*
		 * lvl 1 = 10%
		 * lvl 2 = 13%
		 * ...
		 * lvl 25 = 100%
		 */
		
		return 0.9f/(getMaxLevel()-1);		
		
	}
	
	public float getDamageModifier(Player player){
		
		float modifier = 1;
		Item<?> weapon  = player.getEquipment().getMainHand();
			
		if(weapon != null&&getWeaponType().isInstance(weapon.getType())){		
			int level = player.getSkillLevel(this);
			if(level>0){
				modifier += (0.1+((level-1)*getDamageModifier()));			
			}				
		}
	
		return modifier;
	}
	
	public boolean getCondition(LivingObject owner){
		if(owner instanceof Player){
			Player player = (Player)owner;
			if(player.getSkillLevel(this)==0)
				return false;
			Item<?> weapon = player.getEquipment().getMainHand();
			return weapon!=null && getWeaponType().isInstance(weapon);			
		}		
		return false;	
	}
	
	@Override
	public ValueType getValueType() {
		return Modifier.ValueType.DAMAGE;
		
	}

	@Override
	public ModifierType getModifierType() {

		return Modifier.ModifierType.MULTIPLICATIVE;
	}

	private int [] affectedSkillIds = {0};
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