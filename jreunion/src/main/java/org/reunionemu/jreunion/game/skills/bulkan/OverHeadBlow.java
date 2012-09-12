package org.reunionemu.jreunion.game.skills.bulkan;

import java.util.List;

import org.reunionemu.jreunion.game.BulkanPlayer;
import org.reunionemu.jreunion.game.Castable;
import org.reunionemu.jreunion.game.Effectable;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.items.equipment.Axe;
import org.reunionemu.jreunion.game.items.equipment.Weapon;
import org.reunionemu.jreunion.game.skills.Modifier;
import org.reunionemu.jreunion.server.Server;
import org.reunionemu.jreunion.server.SkillManager;
import org.reunionemu.jreunion.server.PacketFactory.Type;

public class OverHeadBlow extends WeaponAttack implements Castable, Effectable{

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
		/* stamina spent:
		 * level 1 = 10
		 * level 2 = 11
		 * ...
		 * level 25 = 40
		 */
		return 30f/(getMaxLevel()-1);
	}
	
	long getStaminaModifier(Player player){
		long modifier = 0;
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
			long currentStamina = ((BulkanPlayer) caster).getStamina();
			long staminaSpent = getStaminaModifier(player); 
					
			player.setStamina(currentStamina - staminaSpent);
			
			long baseDamage = player.getBaseDamage();
			float skillDamage = getDamageModifier(player);
			long weaponDamage = 0;
			float criticalMultiplier = 0;
			Item<?> weapon = player.getEquipment().getMainHand();
			
			if( weapon != null && weapon.getType() instanceof Weapon){
				weaponDamage += ((Weapon)weapon.getType()).getDamage(weapon);
				criticalMultiplier = ((Weapon)weapon.getType()).getCritical();
			}
			
			long damage = (long)((baseDamage +  weaponDamage) * skillDamage);
			
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
			
			damage += (long)(damage*criticalMultiplier);
	
			player.setDmgType(criticalMultiplier > 0 ? 1 : 0);
			
			synchronized(victims){
				for(LivingObject victim : victims){
					victim.getsAttacked(player, damage);
					player.getClient().sendPacket(Type.AV, victim, player.getDmgType());
				}
				return true;
			}
		}		
		return false;
	}

	public void effect(LivingObject source, LivingObject target){
		source.getInterested().sendPacket(Type.EFFECT, source, target , this, 0, 0, 0);
	}
	
	@Override
	public int getEffectModifier() {
		return 0;
	}
}
