package org.reunionemu.jreunion.game.skills;

import java.util.List;

import org.reunionemu.jreunion.game.Castable;
import org.reunionemu.jreunion.game.Effectable;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.items.equipment.Weapon;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.Server;
import org.reunionemu.jreunion.server.SkillManager;

public class BasicAttack extends Skill implements Castable, Effectable{
	
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
			float criticalMultiplier = 0;
			
			if(item!=null){ // confirm if player is wearing a weapon
				weapon = (Weapon) item.getType();
				weaponDamage += weapon.getDamage(item);
				criticalMultiplier = weapon.getCritical();
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
			
			damage += (long)(damage*criticalMultiplier);
			
			player.setDmgType(criticalMultiplier > 0 ? 1 : 0);
			
			synchronized(victims){
				for(LivingObject victim : victims){
					victim.getsAttacked(player, (int)damage);
					player.getClient().sendPacket(Type.ATTACK, player,victim,player.getDmgType());
					//player.getInterested().sendPacket(Type.ATTACK, player, victim, criticalMultiplier > 0 ? 1 : 0);
					
					return true;
				}
			}
		}
		return false;
	}
	
	@Override 
	public void effect(LivingObject source, LivingObject target){	
		source.getInterested().sendPacket(Type.ATTACK, source, target, source.getDmgType());
	}
	
	@Override
	public int getMaxLevel() {

		return 0;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {

		return 0;
	}

	@Override
	public int getEffectModifier() {
		return 0;
	}
}
