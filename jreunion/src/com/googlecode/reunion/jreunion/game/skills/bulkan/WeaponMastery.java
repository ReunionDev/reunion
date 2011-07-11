package com.googlecode.reunion.jreunion.game.skills.bulkan;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;

public abstract class WeaponMastery extends Skill {
	
	
	public WeaponMastery(int id) {
		super(id);
	}
	
	public abstract Class<?> getWeaponType();
	

	public double getDamageModifier(Player player){
		
		double modifier = 1;
		
		Weapon weapon = player.getEquipment().getMainHand();
		
		if(weapon!=null&&getWeaponType().isInstance(weapon)){
		
			int level = player.getSkillLevel(this);
			if(level>0){
				modifier += (0.1+((level-1)*getDamageModifier()));			
			}			
			player.getPosition().getLocalMap().getWorld().getCommand().serverSay("Weapon Mastery Skill Level: "+level);
							
		}
		
		return modifier;
	}
	public double getDamageModifier(){
		/*
		 * lvl 1 = 10%
		 * lvl 2 = 17%
		 * 
		 * 
		 * lvl 25 = 200%
		 * 
		 * 2.90 = 200% - 10% + 100%
		 * 24 = m
		 */
		
		return 2.90/(getMaxLevel()-1);		
		
	}
	
	
	@Override
	public int getLevelRequirement(int level) {
		return 4+level;
	}
	
	
	@Override
	public int getMaxLevel() {
		return 25;
	}
	

}
