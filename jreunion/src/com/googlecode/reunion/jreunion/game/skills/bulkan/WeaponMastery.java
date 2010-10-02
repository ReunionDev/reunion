package com.googlecode.reunion.jreunion.game.skills.bulkan;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;

public abstract class WeaponMastery extends Skill {
	
	
	public WeaponMastery(int id) {
		super(id);
	}
	
	public abstract Class getWeaponType();
	

	public double getDamageModifier(Player player){
		
		double modifier = 1;
		
		Weapon weapon = player.getEquipment().getMainHand();
		
		
		if(weapon!=null&&getWeaponType().isInstance(weapon)){
		
			int level = player.getSkillLevel(this);
			modifier+=level * getDamageModifier();
		}
		
		return modifier;
	}
	public double getDamageModifier(){
		
		return 0.1;
		
	}
	
	@Override
	public int getLevelRequirement(int level) {
		return level;
	}
	
	@Override
	public int getMaxLevel() {
		return 10;
	}

}
