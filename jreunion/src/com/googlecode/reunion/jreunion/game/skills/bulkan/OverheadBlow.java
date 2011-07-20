package com.googlecode.reunion.jreunion.game.skills.bulkan;

import com.googlecode.reunion.jreunion.game.BulkanPlayer;
import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.Effectable;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Axe;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.game.skills.Modifier;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.SkillManager;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

public class OverheadBlow extends WeaponAttack implements Castable{

	public OverheadBlow(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 44+skillLevel;
	}

	@Override
	public Class<?> getWeaponType() {
		return Axe.class;
	}
	
	public float getDamageModifier(Player player){
		
		float modifier = 1;
		
		Weapon weapon = player.getEquipment().getMainHand();
		
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
	
	@Override
	public boolean cast(LivingObject caster, LivingObject target) {
		
		if(caster instanceof BulkanPlayer){	
			
			float baseDamage = ((BulkanPlayer) caster).getBaseDamage();
			float skillDamage = getDamageModifier((BulkanPlayer) caster);
			float weaponDamage = 0;
			Weapon weapon = ((BulkanPlayer) caster).getEquipment().getMainHand();
			
			if( weapon != null)
				weaponDamage += weapon.getMinDamage() + 
						(Server.getRand().nextFloat()*(weapon.getMaxDamage()-weapon.getMinDamage()));
			
			float damage = (baseDamage +  weaponDamage) * skillDamage;
			
			for(Skill skill: ((Player)caster).getSkills().keySet()){
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
			
				
			synchronized(target){	
				int newHp = target.getHp() - (int) (damage);				
				if (newHp <= 0) {
					((Mob)target).kill((BulkanPlayer)caster);
				} else {
					target.setHp(newHp);
				}				
				return true;
			}
		}		
		return false;
	}
}
