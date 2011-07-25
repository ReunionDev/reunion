package com.googlecode.reunion.jreunion.game.skills;

import com.googlecode.reunion.jreunion.game.BulkanPlayer;
import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.Effectable;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class BasicAttack extends Skill implements Castable, Effectable {
	
	
	public BasicAttack(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public boolean cast(LivingObject attacker, LivingObject... victim) {
		
		float damage = 0;
		
		if (attacker instanceof Player){
			Player player = (Player)attacker; 
			Weapon weapon = player.getEquipment().getMainHand();
			float baseDamage = player.getBaseDamage();
			float weaponDamage = 0;
			
			if(weapon!=null){
				weaponDamage += weapon.getMinDamage() + 
							(Server.getRand().nextFloat()*(weapon.getMaxDamage()-weapon.getMinDamage()));
				if(!weapon.use(player)){
					return false;
				}
			}
			
			damage = baseDamage + weaponDamage;
			
			for(Skill skill: ((Player)attacker).getSkills().keySet()){
				if (Modifier.class.isInstance(skill)){
					Modifier modifier = (Modifier)skill; 
					if(modifier.getAffectedSkills().contains(this)){
						if(modifier.getCondition(attacker)){
							if(modifier.getValueType()==Modifier.ValueType.DAMAGE){
								
								switch(modifier.getModifierType()){
									
									case MULTIPLICATIVE:
										damage *= modifier.getModifier(attacker);
										break;
									case ADDITIVE:
										damage += modifier.getModifier(attacker);
										break;
								}
							}
						}
					}						
				}
			}
		}
		
		synchronized(victim){
			int newHp = victim[0].getHp() - (int) (damage);				
			if (newHp <= 0) {
				((Mob)victim[0]).kill((BulkanPlayer)attacker);
			} else {
				victim[0].setHp(newHp);
			}
		}		
		
		return true;
	}
	
	@Override 
	public void effect(LivingObject source, LivingObject target){	
		//TODO: Figure out what to send on attack to other players.
		
		
	}
	

	@Override
	public int getMaxLevel() {

		return 0;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {

		return 0;
	}
}
