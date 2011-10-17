package com.googlecode.reunion.jreunion.game.skills.bulkan;

import java.util.List;

import com.googlecode.reunion.jreunion.game.BulkanPlayer;
import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Axe;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.game.skills.Modifier;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class OverHeadBlow extends WeaponAttack implements Castable{

	public OverHeadBlow(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public Class<?> getWeaponType() {
		return Axe.class;
	}
	
	public float getDamageModifier(Player player){
		
		float modifier = 1;
		
		Item<?> weapon = player.getEquipment().getMainHand();
		
		if(weapon!=null&&getWeaponType().isInstance(weapon)){
		
			int level = player.getSkillLevel(this);
			if(level>0){
				modifier += (0.07+((level-1)*getDamageModifier()));			
			}				
		}
		
		return modifier;
	}
	public float getDamageModifier(){
		/*
		 * lvl 1 = 7%
		 * lvl 2 = 7%
		 * lvl 3 = 8%
		 * 
		 * lvl 25 = 30%
		 * 
		 * 0.23 = 30% - 7%
		 * 24 = m
		 */
		
		return 0.23f/(getMaxLevel()-1);		
		
	}
	
	public float getStaminaModifier(){
		/* mana spent:
		 * level 1 = 10
		 * level 2 = 11
		 * ...
		 * level 25 = 40
		 */
		return 30f/(getMaxLevel()-1);
	}
	
	float getStaminaModifier(Player player){
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (10 + ((level-1) * getStaminaModifier()));			
		}	
		
		return modifier;
	}
	
	@Override
	public boolean cast(LivingObject caster, List<LivingObject> victims) {
		
		if(caster instanceof BulkanPlayer){	
			Player player = (Player)caster;
			int currentStamina = ((BulkanPlayer) caster).getStamina();
			int staminaSpent = (int) getStaminaModifier(player); 
					
			player.setStamina(currentStamina - staminaSpent);
			
			float baseDamage = player.getBaseDamage();
			float skillDamage = getDamageModifier(player);
			float weaponDamage = 0;
			Item<?> weapon = player.getEquipment().getMainHand();
			
			if( weapon != null)
				weaponDamage += ((Weapon)weapon.getType()).getMinDamage(weapon) + 
						(Server.getRand().nextFloat()*(((Weapon)weapon.getType()).getMaxDamage(weapon)
								- ((Weapon)weapon.getType()).getMinDamage(weapon)));
			
			float damage = (baseDamage +  weaponDamage) * skillDamage;
			
			for(Skill skill: player.getSkills().keySet()){
				if (Modifier.class.isInstance(skill)){
					Modifier modifier = (Modifier)skill; 
					if(modifier.getAffectedSkills().contains(this)){
						if(modifier.getCondition(caster)){
							if(modifier.getValueType()==Modifier.ValueType.DAMAGE){
								
								switch(modifier.getModifierType()){
									
									case MULTIPLICATIVE:
										damage *= modifier.getModifier(caster);
										break;
									case ADDITIVE:
										damage += modifier.getModifier(caster);
										break;
								}
							}
						}
					}						
				}
			}
			
	
			synchronized(victims){
				for(LivingObject victim : victims){
					victim.getsAttacked(player, (int)damage);		
				}
				return true;
			}
		}		
		return false;
	}
}
