package com.googlecode.reunion.jreunion.game.skills;

import java.util.List;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class BasicAttack extends Skill implements Castable{
	
	public BasicAttack(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public boolean cast(LivingObject attacker, List<LivingObject> victims) {
		
		float damage = 0;
		
		if (attacker instanceof Player){
			Player player = (Player)attacker; 
			Item<?> item = player.getEquipment().getMainHand();
			Weapon weapon = null;
			float baseDamage = player.getBaseDamage();
			float weaponDamage = 0;
			
			if(item!=null){
				weapon = (Weapon) item.getType();
				weaponDamage += weapon.getMinDamage(item) + 
							(Server.getRand().nextFloat()*(weapon.getMaxDamage(item)-weapon.getMinDamage(item)));
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
			
			synchronized(victims){
				for(LivingObject victim : victims){
					victim.getsAttacked(player, (int)damage);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override 
	public void effect(LivingObject source, LivingObject target){	
		
		//TODO merge interested of source and target
		source.getInterested().sendPacket(Type.ATTACK, source, target);		
		
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
